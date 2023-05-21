package com.platformatory.source.connector;

import com.platformatory.source.connector.models.UserData;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import com.github.jcustenborder.kafka.connect.utils.VersionUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;

import static com.platformatory.source.connector.Auth0Schema.UPDATED_AT_FIELD;
import static com.platformatory.source.connector.MySourceConnectorConfig.API_KEY_CONFIG;
import static com.platformatory.source.connector.MySourceConnectorConfig.API_SECRET_CONFIG;
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
        if( lastSourceOffset == null){
            // we haven't fetched anything yet, so we initialize to 7 days ago
            nextQuerySince = config.getAPIKey();
        } else {
            Object updatedAt = lastSourceOffset.get(UPDATED_AT_FIELD);
            if(updatedAt != null && (updatedAt instanceof String)){
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
        JSONArray issues = auth0APIHttpClient.getNextIssues(nextPageToVisit, nextQuerySince);
        // we'll count how many results we get with i
        int i = 0;
        for (Object obj : issues) {
            UserData issue = Issue.fromJson((JSONObject) obj);
            SourceRecord sourceRecord = generateSourceRecord(issue);
            records.add(sourceRecord);
            i += 1;
            lastUpdatedAt = issue.getUpdatedAt();
        }
        if (i > 0) log.info(String.format("Fetched %s record(s)", i));
        if (i == 100){
            // we have reached a full batch, we need to get the next one
            nextPageToVisit += 1;
        }
        else {
            nextQuerySince = lastUpdatedAt.plusSeconds(1);
            nextPageToVisit = 1;
            gitHubHttpAPIClient.sleep();
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
        map.put(API_KEY_CONFIG, config.getAPIKey());
        map.put(API_SECRET_CONFIG, config.getAPISecret());
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
                .put(OWNER_FIELD, config.getOwnerConfig())
                .put(REPOSITORY_FIELD, config.getRepoConfig())
                .put(NUMBER_FIELD, issue.getNumber());

        return key;
    }

    public Struct buildRecordValue(UserData userData){

        // Issue top level fields
        Struct valueStruct = new Struct(VALUE_SCHEMA)
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

        return valueStruct;
    }

}
