package com.platformatory.source.connector;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.auth0.json.mgmt.users.UsersPage;
import com.auth0.net.Request;
import com.auth0.net.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jcustenborder.kafka.connect.utils.VersionUtil;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class MySourceTask extends SourceTask {
    /*
        Your connector should never use System.out for logging. All of your classes should use slf4j
        for logging
    */

    private static final Logger log = LoggerFactory.getLogger(MySourceTask.class);
    public MySourceConnectorConfig config;
    private static final long TOKEN_EXPIRATION_BUFFER_SECONDS = 60;
    private String accessToken;
    private Instant tokenExpiration;
    private String domain;
    private String clientId;
    private String clientSecret;
    private Long lastStoredPosition = null; // You would declare this at the beginning of your class.

    @Override
    public String version() {
        return VersionUtil.version(this.getClass());
    }

    @Override
    public void start(Map<String, String> map) {
        // TODO: Do things here that are required to start your task. This could be open a connection to a database, etc.
        config = new MySourceConnectorConfig(map);
        domain = config.getDomain();
        clientId = config.getClientIdConfig();
        clientSecret = config.getClientSecretConfig();

        // Get last saved offset
        Map<String, Object> offset = context.offsetStorageReader().offset(Collections.singletonMap("my_source", "users"));
        if (offset != null) {
            // If there was a previously saved offset, use it to resume processing
            lastStoredPosition = (Long) offset.get("position");
        }

        try {
            refreshToken(); // to get the initial access token
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SourceRecord> poll() {
        // TODO: Create SourceRecord objects that will be sent to the Kafka cluster.
        // fetch data and create SourceRecord's

        // Check if the token has expired or about to expire
        if (tokenExpiration == null || Instant.now().isAfter(tokenExpiration.minusSeconds(TOKEN_EXPIRATION_BUFFER_SECONDS))) {
            try {
                refreshToken(); // Refresh the access token
            } catch (UnirestException e) {
                log.error("Failed to refresh the access token: {}", e.getMessage());
                return Collections.emptyList();
            }
        }

        // Use the refreshed access token to make API calls
        ManagementAPI mgmt = ManagementAPI.newBuilder(domain, accessToken).build();

        Request<UsersPage> usersPageRequest = mgmt.users().list(new UserFilter());
        try {
            Response<UsersPage> usersPageResponse = usersPageRequest.execute();
            int statusCode = usersPageResponse.getStatusCode();

            if (statusCode == 200){
                ObjectMapper mapper = new ObjectMapper();
                UsersPage usersPage = usersPageResponse.getBody();

                List<SourceRecord> records = new ArrayList<>();
                for (User user : usersPage.getItems()) {
                    // Only process users that are newer than the last stored position
                    if (lastStoredPosition == null || user.getUpdatedAt().getTime() > lastStoredPosition) {
                        String jsonString = mapper.writeValueAsString(user);

                        Map<String, String> sourcePartition = Collections.singletonMap("my_source", "users");
                        Map<String, Object> sourceOffset = Collections.singletonMap("position", user.getUpdatedAt().getTime());

                        String key = user.getId();
                        SourceRecord record = new SourceRecord(sourcePartition, sourceOffset, "users_" + config.getTopic(), Schema.STRING_SCHEMA, key, Schema.STRING_SCHEMA, jsonString);
                        records.add(record);

                        // Update the lastStoredPosition with the current user's updatedAt time
                        lastStoredPosition = user.getUpdatedAt().getTime();
                    }
                }

                return records;
            } else {
                switch(statusCode) {
                    case 400:
                        log.error("Bad Request: {}", usersPageResponse.getBody());
                        break;
                    case 401:
                        log.error("Unauthorized: {}", usersPageResponse.getBody());
                        refreshToken(); // Assuming refreshToken can refresh the token
                        break;
                    case 403:
                        log.error("Forbidden: {}", usersPageResponse.getBody());
                        // Potentially handle insufficient scope here
                        break;
                    case 429:
                        log.error("Too many requests: {}", usersPageResponse.getBody());
                        // Exponential backoff
                        int retryAfter = 1;
                        if (usersPageResponse.getHeaders().containsKey("Retry-After")) {
                            try {
                                retryAfter = Integer.parseInt(usersPageResponse.getHeaders().get("Retry-After"));
                            } catch (NumberFormatException e) {
                                log.warn("Invalid Retry-After header", e);
                            }
                        }
                        log.info("Sleeping for {} seconds", retryAfter);
                        Thread.sleep(1000L * retryAfter);
                        break;
                    case 503:
                        log.error("Service unavailable: {}", usersPageResponse.getBody());
                        Thread.sleep(1000 * 60 * 5); // Wait for 5 minutes before next request
                        break;
                    default:
                        log.error("Unhandled status code: {}", statusCode);
                }
                return Collections.emptyList();
            }

        } catch (Auth0Exception | JsonProcessingException e) {
            log.error("Error fetching data: {}", e.getMessage());
        } catch (UnirestException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return Collections.emptyList(); // if no data
    }

    @Override
    public void stop() {
        // TODO: Do whatever is required to stop your task.
    }

    private void refreshToken() throws UnirestException {
        String url = "https://%s/oauth/token";
        String audience = "https://%s/api/v2/";

        String formattedUrl = String.format(url, domain);
        String formattedAudience = String.format(audience, domain);

        HttpResponse<String> response = Unirest.post(formattedUrl)
                .header("content-type", "application/x-www-form-urlencoded")
                .body(String.format("grant_type=client_credentials&client_id=%s&client_secret=%s&audience=%s",
                        clientId, clientSecret, formattedAudience))
                .asString();

        // Assuming 'response' contains the JSON response as a string
        String responseBody = response.getBody();

        // Parse the JSON response
        JSONObject jsonResponse = new JSONObject(responseBody);

        // Extract the access_token value
        accessToken = jsonResponse.getString("access_token");

        // Calculate token expiration time
        long expiresIn = jsonResponse.getLong("expires_in");
        tokenExpiration = Instant.now().plusSeconds(expiresIn);
    }

}
