package com.platformatory.source.connector;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

import java.util.Map;

public class MySourceConnectorConfig extends AbstractConfig {

  public static final String TOPIC_CONFIG = "topic";
  private static final String TOPIC_DOC = "Topic to write to";

  public static final String DOMAIN_CONFIG = "domain";
  public static final String DOMAIN_CONFIG_DOC = "Domain for the Application";

  public static final String CLIENT_ID_TOKEN = "client.id";
  private static final String CLIENT_ID_DOC = "Client ID";

  public static final String CLIENT_SECRET_CONFIG = "client.secret";
  private static final String CLIENT_SECRET_DOC = "Client Secret";



  public MySourceConnectorConfig(Map<String, String> originals) {
    super(config(), originals);
  }

  public static ConfigDef config() {
    return new ConfigDef()
            .define(TOPIC_CONFIG, Type.STRING, Importance.HIGH, TOPIC_DOC)
            .define(DOMAIN_CONFIG, Type.STRING, Importance.HIGH, DOMAIN_CONFIG_DOC)
            .define(CLIENT_ID_TOKEN, Type.STRING, Importance.HIGH, CLIENT_ID_DOC)
            .define(CLIENT_SECRET_CONFIG, Type.STRING, Importance.HIGH, CLIENT_SECRET_DOC);
  }

  public String getTopic(){
    return this.getString(TOPIC_CONFIG);
  }

  public String getDomain(){
    return this.getString(DOMAIN_CONFIG);
  }

  public String getClientIdConfig() {
    return this.getString(CLIENT_ID_TOKEN);
  }

  public String getClientSecretConfig(){return this.getString(CLIENT_SECRET_CONFIG);}
}
