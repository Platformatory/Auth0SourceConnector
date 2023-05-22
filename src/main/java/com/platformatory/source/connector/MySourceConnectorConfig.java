package com.platformatory.source.connector;

import com.platformatory.source.connector.Validator.BatchSizeValidator;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

import java.util.Map;

public class MySourceConnectorConfig extends AbstractConfig {

  public static final String TOPIC_CONFIG = "topic";
  private static final String TOPIC_DOC = "Topic to write to";

  public static final String IDENTIFIER_CONFIG = "your_identifier";
  private static final String IDENTIFIER_CONFIG_DOC = "Unique identifier for the API";

  public static final String BATCH_SIZE_CONFIG = "batch_size";
  private static final String BATCH_SIZE_DOC = "Number of data points to retrieve at a time. Defaults to 100 (max value)";

  public static final String ACCESS_TOKEN_KEY = "access_token_key";
  private static final String ACCESS_TOKEN_KEY_DOC = "API access token key";

  public static final String REQUEST_CONFIG = "api_request_config";
  private static final String REQUEST_DOC = "Request Parameter";

  public MySourceConnectorConfig(Map<String, String> originals) {
    super(config(), originals);
  }

  public static ConfigDef config() {
    return new ConfigDef()
            .define(TOPIC_CONFIG, Type.STRING, Importance.HIGH, TOPIC_DOC)
            .define(IDENTIFIER_CONFIG, Type.STRING, Importance.HIGH, IDENTIFIER_CONFIG_DOC)
            .define(BATCH_SIZE_CONFIG, Type.INT, 100, new BatchSizeValidator(), Importance.LOW, BATCH_SIZE_DOC)
            .define(ACCESS_TOKEN_KEY, Type.STRING,"", Importance.HIGH, ACCESS_TOKEN_KEY_DOC)
            .define(REQUEST_CONFIG, Type.STRING, Importance.HIGH, REQUEST_DOC);
  }

  public String getTopic(){
    return this.getString(TOPIC_CONFIG);
  }

  public String getDomain(){
    return this.getString(IDENTIFIER_CONFIG);
  }

  public Integer getBatchSize() {
    return this.getInt(BATCH_SIZE_CONFIG);
  }

  public String getAPIToken() {
    return this.getString(ACCESS_TOKEN_KEY);
  }

  public String getRequestConfig(){return this.getString(REQUEST_CONFIG);}

}
