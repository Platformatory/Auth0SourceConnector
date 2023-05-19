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

  public static final String DOMAIN_CONFIG = "yourDomain";
  private static final String DOMAIN_DOC = "Domain of your Application";

  public static final String BATCH_SIZE_CONFIG = "batch.size";
  private static final String BATCH_SIZE_DOC = "Number of data points to retrieve at a time. Defaults to 100 (max value)";

  public static final String API_KEY_CONFIG = "access_token_key";
  private static final String API_KEY_DOC = "API Key Token";

  public static final String API_SECRET_CONFIG = "access_token_password";
  private static final String API_SECRET_DOC = "API Secret Token";

  public MySourceConnectorConfig(Map<String, String> originals) {
    super(config(), originals);
  }

  public static ConfigDef config() {
    return new ConfigDef()
            .define(TOPIC_CONFIG, Type.STRING, Importance.HIGH, TOPIC_DOC)
            .define(DOMAIN_CONFIG, Type.STRING, Importance.HIGH, DOMAIN_DOC)
            .define(BATCH_SIZE_CONFIG, Type.INT, 100, new BatchSizeValidator(), Importance.LOW, BATCH_SIZE_DOC)
            .define(API_KEY_CONFIG, Type.STRING,"", Importance.HIGH, API_KEY_DOC)
            .define(API_SECRET_CONFIG, Type.PASSWORD, "", Importance.HIGH, API_SECRET_DOC);
  }

  public String getTopic(){
    return this.getString(TOPIC_CONFIG);
  }

  public String getDomain(){
    return this.getString(DOMAIN_CONFIG);
  }

  public Integer getBatchSize() {
    return this.getInt(BATCH_SIZE_CONFIG);
  }

  public String getAPIKey() {
    return this.getString(API_KEY_CONFIG);
  }

  public String getAPISecret(){return this.getString(API_SECRET_CONFIG);}

}
