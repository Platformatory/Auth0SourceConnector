package com.platformatory.source.connector.models;

import java.util.List;
import java.util.Map;

public class UserData {

    private String userId;
    private String email;
    private boolean emailVerified;
    private String username;
    private String phoneNumber;
    private boolean phoneVerified;
    private String createdAt;
    private String updatedAt;
    private List<IdentityData> identities;
    private Map<String, String> appMetadata;
    private Map<String, String> userMetadata;
    private String picture;
    private String name;
    private String nickname;
    private List<String> multifactor;
    private String lastIp;
    private String lastLogin;
    private int loginsCount;
    private boolean blocked;
    private String givenName;
    private String familyName;

    public UserData() {
    }

    public UserData(String userId, String email, boolean emailVerified, String username, String phoneNumber, boolean phoneVerified, String createdAt, String updatedAt, List<IdentityData> identities, Map<String, String> appMetadata, Map<String, String> userMetadata, String picture, String name, String nickname, List<String> multifactor, String lastIp, String lastLogin, int loginsCount, boolean blocked, String givenName, String familyName) {
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

    public boolean isEmailVerified() {
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

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
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

    public int getLoginsCount() {
        return loginsCount;
    }

    public void setLoginsCount(int loginsCount) {
        this.loginsCount = loginsCount;
    }

    public boolean isBlocked() {
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
}
