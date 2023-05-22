package com.platformatory.source.connector;

import com.github.jcustenborder.kafka.connect.utils.VersionUtil;
import com.platformatory.source.connector.models.IdentityData;
import com.platformatory.source.connector.models.UserData;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.platformatory.source.connector.Auth0Schema.*;


public class MySourceTask extends SourceTask {
    /*
        Your connector should never use System.out for logging. All of your classes should use slf4j
        for logging
    */


    private static final Logger log = LoggerFactory.getLogger(MySourceTask.class);
    public MySourceConnectorConfig config;

    Auth0APIHttpClient auth0APIHttpClient;
    protected Instant nextQuerySince;
    protected Instant lastUpdatedAt;

    @Override
    public String version() {
        return VersionUtil.version(this.getClass());
    }

    @Override
    public void start(Map<String, String> map) {
        //TODO: Do things here that are required to start your task. This could be open a connection to a database, etc.
        config = new MySourceConnectorConfig(map);
        auth0APIHttpClient = new Auth0APIHttpClient(config);
        initializeLastVariables();
    }
    private void initializeLastVariables(){
        Map<String, Object> lastSourceOffset = null;
        lastSourceOffset = context.offsetStorageReader().offset(sourcePartition());
        if( lastSourceOffset != null){
            Object updatedAt = lastSourceOffset.get(UPDATED_AT_FIELD);
            if((updatedAt instanceof String)){
                nextQuerySince = Instant.parse((String) updatedAt);
            }
        }
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        //TODO: Create SourceRecord objects that will be sent the kafka cluster.
        auth0APIHttpClient.sleepIfNeed();

        // fetch data
        final ArrayList<SourceRecord> records = new ArrayList<>();
        JSONArray userData = auth0APIHttpClient.getNextIssues();
        // we'll count how many results we get with i
        int i = 0;
        for (Object obj : userData) {
            UserData issue = UserData.fromJson((JSONObject) obj);
            SourceRecord sourceRecord = generateSourceRecord(issue);
            records.add(sourceRecord);
            i += 1;
            lastUpdatedAt = issue.getUpdatedAt();
        }
        if (i > 0) log.info(String.format("Fetched %s record(s)", i));
        if (i == 100){
            // we have reached a full batch, we need to get the next one

        }
        else {
            nextQuerySince = lastUpdatedAt.plusSeconds(1);

            auth0APIHttpClient.sleep();
        }
        return records;
    }

    private SourceRecord generateSourceRecord(UserData userData) {
        return new SourceRecord(
                sourcePartition(),
                sourceOffset(userData.getUpdatedAt()),
                config.getTopic(),
                null, // partition will be inferred by the framework
                KEY_SCHEMA,
                buildRecordKey(userData),
                VALUE_SCHEMA,
                buildRecordValue(userData),
                userData.getUpdatedAt().toEpochMilli());
    }

    @Override
    public void stop() {
        //TODO: Do whatever is required to stop your task.
    }

    private Map<String, String> sourcePartition() {
        Map<String, String> map = new HashMap<>();
        map.put(IDENTIFIER, config.getDomain());
        map.put(REQUEST, config.getDomain());
        return map;
    }

    private Map<String, String> sourceOffset(Instant updatedAt) {
        Map<String, String> map = new HashMap<>();
        map.put(UPDATED_AT_FIELD, updatedAt.toString());
        return map;
    }

    private Struct buildRecordKey(UserData userData){
        // Key Schema
        Struct key = new Struct(KEY_SCHEMA)
                .put(IDENTIFIER, config.getDomain())
                .put(REQUEST, config.getRequestConfig());

        return key;
    }

    public Struct buildRecordValue(UserData userData) {
        Struct valueStruct = new Struct(VALUE_SCHEMA);

        if (userData.getEmail() != null) {
            valueStruct.put(USER_ID_FIELD, userData.getUserId());
        }

        if (userData.getEmail() != null) {
            valueStruct.put(EMAIL_FIELD, userData.getEmail());
        }
        if (userData.isEmailVerified() != null) {
            valueStruct.put(EMAIL_VERIFIED_FIELD, userData.isEmailVerified());
        }
        if (userData.getUsername() != null) {
            valueStruct.put(USERNAME_FIELD, userData.getUsername());
        }
        if (userData.getPhoneNumber() != null) {
            valueStruct.put(PHONE_NUMBER_FIELD, userData.getPhoneNumber());
        }
        if (userData.isPhoneVerified() != null) {
            valueStruct.put(PHONE_VERIFIED_FIELD, userData.isPhoneVerified());
        }
        if (userData.getCreatedAt() != null) {
            valueStruct.put(CREATED_AT_FIELD, userData.getCreatedAt());
        }
        if (userData.getUpdatedAt() != null) {
            valueStruct.put(UPDATED_AT_FIELD, userData.getUpdatedAt());
        }
        if (userData.getPicture() != null) {
            valueStruct.put(PICTURE_FIELD, userData.getPicture());
        }
        if (userData.getName() != null) {
            valueStruct.put(NAME_FIELD, userData.getName());
        }
        if (userData.getNickname() != null) {
            valueStruct.put(NICKNAME_FIELD, userData.getNickname());
        }
        if (userData.getLastIp() != null) {
            valueStruct.put(LAST_IP_FIELD, userData.getLastIp());
        }
        if (userData.getLastLogin() != null) {
            valueStruct.put(LAST_LOGIN_FIELD, userData.getLastLogin());
        }
        if (userData.getLoginsCount() != null) {
            valueStruct.put(LOGINS_COUNT_FIELD, userData.getLoginsCount());
        }
        if (userData.isBlocked() != null) {
            valueStruct.put(BLOCKED_FIELD, userData.isBlocked());
        }
        if (userData.getGivenName() != null) {
            valueStruct.put(GIVEN_NAME_FIELD, userData.getGivenName());
        }
        if (userData.getFamilyName() != null) {
            valueStruct.put(FAMILY_NAME_FIELD, userData.getFamilyName());
        }

        // Add identities array
        if (userData.getIdentities() != null) {
            List<Struct> identitiesList = new ArrayList<>();
            for (IdentityData identityData : userData.getIdentities()) {
                Struct identityStruct = new Struct(IDENTITIES_SCHEMA)
                        .put(IDENTITIES_CONNECTION_FIELD, identityData.getConnection())
                        .put(IDENTITIES_USER_ID_FIELD, identityData.getUserId())
                        .put(IDENTITIES_PROVIDER_FIELD, identityData.getProvider())
                        .put(IDENTITIES_IS_SOCIAL_FIELD, identityData.isSocial());
                identitiesList.add(identityStruct);
            }
            valueStruct.put(IDENTITIES_FIELD, identitiesList.toArray());
        }

        // Add app_metadata map
        if (userData.getAppMetadata() != null) {
            Map<String, String> appMetadataMap = new HashMap<>(userData.getAppMetadata());
            valueStruct.put(APP_METADATA_FIELD, appMetadataMap);
        }

        // Add user_metadata map
        if (userData.getUserMetadata() != null) {
            Map<String, String> userMetadataMap = new HashMap<>(userData.getUserMetadata());
            valueStruct.put(USER_METADATA_FIELD, userMetadataMap);
        }

        // Add multiFactor array
        if (userData.getMultifactor() != null) {
            valueStruct.put(MULTI_FACTOR_FIELD, userData.getMultifactor().toArray());
        }

        return valueStruct;
    }


}
/*Struct valueStruct = new Struct(VALUE_SCHEMA)
                .put(URL_FIELD, issue.getUrl())
                .put(TITLE_FIELD, issue.getTitle())
                .put(CREATED_AT_FIELD, Date.from(issue.getCreatedAt()))
                .put(UPDATED_AT_FIELD, Date.from(issue.getUpdatedAt()))
                .put(NUMBER_FIELD, issue.getNumber())
                .put(STATE_FIELD, issue.getState());

        // User is mandatory
        User user = issue.getUser();
        Struct userStruct = new Struct(USER_SCHEMA)
                .put(USER_URL_FIELD, user.getUrl())
                .put(USER_ID_FIELD, user.getId())
                .put(USER_LOGIN_FIELD, user.getLogin());
        valueStruct.put(USER_FIELD, userStruct);

        // Pull request is optional
        PullRequest pullRequest = issue.getPullRequest();
        if (pullRequest != null) {
            Struct prStruct = new Struct(PR_SCHEMA)
                    .put(PR_URL_FIELD, pullRequest.getUrl())
                    .put(PR_HTML_URL_FIELD, pullRequest.getHtmlUrl());
            valueStruct.put(PR_FIELD, prStruct);
        }

        return valueStruct;*/
