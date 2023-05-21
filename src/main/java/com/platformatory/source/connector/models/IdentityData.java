package com.platformatory.source.connector.models;

public class IdentityData {

    private String connection;
    private String userId;
    private String provider;
    private boolean isSocial;

    public IdentityData() {
    }

    public IdentityData(String connection, String userId, String provider, boolean isSocial) {
        this.connection = connection;
        this.userId = userId;
        this.provider = provider;
        this.isSocial = isSocial;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isSocial() {
        return isSocial;
    }

    public void setSocial(boolean social) {
        isSocial = social;
    }
}
