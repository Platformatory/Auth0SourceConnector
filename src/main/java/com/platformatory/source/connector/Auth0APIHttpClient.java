package com.platformatory.source.connector;

import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import org.apache.kafka.connect.errors.ConnectException;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Auth0APIHttpClient {

    private static final Logger log = LoggerFactory.getLogger(Auth0APIHttpClient.class);

    // for efficient http requests
    private Integer XRateLimit = 9999;
    private Integer XRateRemaining = 9999;
    private long XRateReset = Instant.MAX.getEpochSecond();

    MySourceConnectorConfig config;

    public static final String X_RATELIMIT_LIMIT_HEADER="X-Ratelimit-Limit";
    public static final String X_RATELIMIT_REMAINING_HEADER="X-Ratelimit-Remaining";
    public static final String X_RATELIMIT_RESET_HEADER="X-Ratelimit-Reset";

    public Auth0APIHttpClient(MySourceConnectorConfig config){
        this.config = config;
    }


    protected JSONArray getNextIssues() throws InterruptedException {

        HttpResponse<JsonNode> jsonResponse;
        try {
            jsonResponse = getNextIssuesAPI();

            // deal with headers in any case
            Headers headers = jsonResponse.getHeaders();
            XRateLimit = Integer.valueOf(headers.getFirst(X_RATELIMIT_LIMIT_HEADER));
            XRateRemaining = Integer.valueOf(headers.getFirst(X_RATELIMIT_REMAINING_HEADER));
            XRateReset = Integer.parseInt(headers.getFirst(X_RATELIMIT_RESET_HEADER));
            switch (jsonResponse.getStatus()){
                case 200:
                    return jsonResponse.getBody().getArray();
                case 401:
                    throw new ConnectException("Bad GitHub credentials provided, please edit your config");
                case 403:
                    // we have issues too many requests.
                    log.info(jsonResponse.getBody().getObject().getString("message"));
                    log.info(String.format("Your rate limit is %s", XRateLimit));
                    log.info(String.format("Your remaining calls is %s", XRateRemaining));
                    log.info(String.format("The limit will reset at %s",
                            LocalDateTime.ofInstant(Instant.ofEpochSecond(XRateReset), ZoneOffset.systemDefault())));
                    long sleepTime = XRateReset - Instant.now().getEpochSecond();
                    log.info(String.format("Sleeping for %s seconds", sleepTime ));
                    Thread.sleep(1000 * sleepTime);
                    return getNextIssues();
                default:
                    log.error(constructUrl());
                    log.error(String.valueOf(jsonResponse.getStatus()));
                    log.error(jsonResponse.getBody().toString());
                    log.error(jsonResponse.getHeaders().toString());
                    log.error("Unknown error: Sleeping 5 seconds " +
                            "before re-trying");
                    Thread.sleep(5000L);
                    return getNextIssues();
            }
        } catch (UnirestException e) {
            e.printStackTrace();
            Thread.sleep(5000L);
            return new JSONArray();
        }
    }

    protected HttpResponse<JsonNode> getNextIssuesAPI() throws UnirestException {
        GetRequest unirest = Unirest.get(constructUrl())
                .header("authorization", "Bearer "+ config.getAPIToken());

        log.debug(String.format("GET %s", unirest.getUrl()));
        return unirest.asJson();
    }

    protected String constructUrl(){
        return String.format(
                "%s%s",
                config.getDomain(),
                config.getRequestConfig());
    }

    public void sleep() throws InterruptedException {
        long sleepTime = (long) Math.ceil(
                (double) (XRateReset - Instant.now().getEpochSecond()) / XRateRemaining);
        log.debug(String.format("Sleeping for %s seconds", sleepTime ));
        Thread.sleep(1000 * sleepTime);
    }

    public void sleepIfNeed() throws InterruptedException {
        // Sleep if needed
        if (XRateRemaining <= 10 && XRateRemaining > 0) {
            log.info(String.format("Approaching limit soon, you have %s requests left", XRateRemaining));
            sleep();
        }
    }
}
