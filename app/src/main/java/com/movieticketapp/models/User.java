package com.movieticketapp.models;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String userId;
    private String fullName;
    private String email;
    private String phone;
    private String avatarUrl;
    private String createdAt;

    public User() {
    }

    public User(String userId, String fullName, String email, String phone, String avatarUrl, String createdAt) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("fullName", fullName);
        map.put("email", email);
        map.put("phone", phone);
        map.put("avatarUrl", avatarUrl);
        map.put("createdAt", createdAt);
        return map;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
