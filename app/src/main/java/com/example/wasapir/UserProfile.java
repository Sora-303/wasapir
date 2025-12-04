package com.example.wasapir;

public class UserProfile {
    private String nickname;
    private Integer age;
    private String description;
    private String profileImageUrl;

    public UserProfile() {
        // Constructor vacío requerido por Firebase
    }

    public UserProfile(String nickname, Integer age, String description, String profileImageUrl) {
        this.nickname = nickname;
        this.age = age;
        this.description = description;
        this.profileImageUrl = profileImageUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public Integer getAge() {
        return age;
    }

    public String getDescription() {
        return description;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}