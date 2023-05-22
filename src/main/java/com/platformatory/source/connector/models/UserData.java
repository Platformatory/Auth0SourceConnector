package com.platformatory.source.connector.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Instant;
import java.util.*;

import static com.platformatory.source.connector.Auth0Schema.*;

public class UserData {

    private String userId;
    private String email;
    private Boolean emailVerified;
    private String username;
    private String phoneNumber;
    private Boolean phoneVerified;
    private Instant createdAt;
    private Instant updatedAt;
    private List<IdentityData> identities;
    private Map<String, String> appMetadata;
    private Map<String, String> userMetadata;
    private String picture;
    private String name;
    private String nickname;
    private List<String> multifactor;
    private String lastIp;
    private String lastLogin;
    private Integer loginsCount;
    private Boolean blocked;
    private String givenName;
    private String familyName;

    public UserData() {
    }

    public UserData(String userId, String email, Boolean emailVerified, String username, String phoneNumber, Boolean phoneVerified, Instant createdAt, Instant updatedAt, List<IdentityData> identities, Map<String, String> appMetadata, Map<String, String> userMetadata, String picture, String name, String nickname, List<String> multifactor, String lastIp, String lastLogin, Integer loginsCount, Boolean blocked, String givenName, String familyName) {
        this.userId = userId;
        this.email = email;
        this.emailVerified = emailVerified;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.phoneVerified = phoneVerified;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.identities = identities;
        this.appMetadata = appMetadata;
        this.userMetadata = userMetadata;
        this.picture = picture;
        this.name = name;
        this.nickname = nickname;
        this.multifactor = multifactor;
        this.lastIp = lastIp;
        this.lastLogin = lastLogin;
        this.loginsCount = loginsCount;
        this.blocked = blocked;
        this.givenName = givenName;
        this.familyName = familyName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean isPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<IdentityData> getIdentities() {
        return identities;
    }

    public void setIdentities(List<IdentityData> identities) {
        this.identities = identities;
    }

    public Map<String, String> getAppMetadata() {
        return appMetadata;
    }

    public void setAppMetadata(Map<String, String> appMetadata) {
        this.appMetadata = appMetadata;
    }

    public Map<String, String> getUserMetadata() {
        return userMetadata;
    }

    public void setUserMetadata(Map<String, String> userMetadata) {
        this.userMetadata = userMetadata;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<String> getMultifactor() {
        return multifactor;
    }

    public void setMultifactor(List<String> multifactor) {
        this.multifactor = multifactor;
    }

    public String getLastIp() {
        return lastIp;
    }

    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Integer getLoginsCount() {
        return loginsCount;
    }

    public void setLoginsCount(int loginsCount) {
        this.loginsCount = loginsCount;
    }

    public Boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public static UserData fromJson(JSONObject jsonObject){
        UserData userData = new UserData();
        userData.setUserId(jsonObject.getString(USER_ID_FIELD));
        userData.setEmail(jsonObject.getString(EMAIL_FIELD));
        userData.setEmailVerified(jsonObject.getBoolean(EMAIL_VERIFIED_FIELD));
        userData.setUsername(jsonObject.getString(USERNAME_FIELD));
        userData.setPhoneNumber(jsonObject.getString(PHONE_NUMBER_FIELD));
        userData.setPhoneVerified(jsonObject.getBoolean(PHONE_VERIFIED_FIELD));
        userData.setCreatedAt(Instant.parse(jsonObject.getString(CREATED_AT_FIELD)));
        userData.setUpdatedAt(Instant.parse(jsonObject.getString(UPDATED_AT_FIELD)));

        // Parse identities array
        JSONArray identitiesArray = jsonObject.getJSONArray(IDENTITIES_FIELD);
        List<IdentityData> identities = new ArrayList<>();
        for (int i = 0; i < identitiesArray.length(); i++) {
            JSONObject identityObject = identitiesArray.getJSONObject(i);
            IdentityData identityData = new IdentityData();
            identityData.setConnection(identityObject.getString(IDENTITIES_CONNECTION_FIELD));
            identityData.setUserId(identityObject.getString(IDENTITIES_USER_ID_FIELD));
            identityData.setProvider(identityObject.getString(IDENTITIES_PROVIDER_FIELD));
            identityData.setSocial(identityObject.getBoolean(IDENTITIES_IS_SOCIAL_FIELD));
            identities.add(identityData);
        }
        userData.setIdentities(identities);

        // Parse app_metadata and user_metadata as maps
        JSONObject appMetadataObject = jsonObject.optJSONObject(APP_METADATA_FIELD);
        if (appMetadataObject != null) {
            Map<String, String> appMetadata = new HashMap<>();
            Iterator<String> appMetadataKeys = appMetadataObject.keys();
            while (appMetadataKeys.hasNext()) {
                String key = appMetadataKeys.next();
                String value = appMetadataObject.getString(key);
                appMetadata.put(key, value);
            }
            userData.setAppMetadata(appMetadata);
        }

        JSONObject userMetadataObject = jsonObject.optJSONObject(USER_METADATA_FIELD);
        if (userMetadataObject != null) {
            Map<String, String> userMetadata = new HashMap<>();
            Iterator<String> userMetadataKeys = userMetadataObject.keys();
            while (userMetadataKeys.hasNext()) {
                String key = userMetadataKeys.next();
                String value = userMetadataObject.getString(key);
                userMetadata.put(key, value);
            }
            userData.setUserMetadata(userMetadata);
        }

        userData.setPicture(jsonObject.getString(PICTURE_FIELD));
        userData.setName(jsonObject.getString(NAME_FIELD));
        userData.setNickname(jsonObject.getString(NICKNAME_FIELD));

        // Parse multiFactor array
        JSONArray multiFactorArray = jsonObject.getJSONArray(MULTI_FACTOR_FIELD);
        List<String> multiFactor = new ArrayList<>();
        for (int i = 0; i < multiFactorArray.length(); i++) {
            String factor = multiFactorArray.getString(i);
            multiFactor.add(factor);
        }
        userData.setMultifactor(multiFactor);

        userData.setLastIp(jsonObject.getString(LAST_IP_FIELD));
        userData.setLastLogin(jsonObject.getString(LAST_LOGIN_FIELD));
        userData.setLoginsCount(jsonObject.getInt(LOGINS_COUNT_FIELD));
        userData.setBlocked(jsonObject.getBoolean(BLOCKED_FIELD));
        userData.setGivenName(jsonObject.getString(GIVEN_NAME_FIELD));
        userData.setFamilyName(jsonObject.getString(FAMILY_NAME_FIELD));

        return userData;
    }
}
