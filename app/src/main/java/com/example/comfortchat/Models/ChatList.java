package com.example.comfortchat.Models;

public class ChatList {

    private String userId;
    private String username;
    private String messages;
    private String date;
    private String urlProfile;

    public ChatList(){

    }

    public ChatList(String userId, String username, String messages, String date, String urlProfile) {
        this.userId = userId;
        this.username = username;
        this.messages = messages;
        this.date = date;
        this.urlProfile = urlProfile;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrlProfile() {
        return urlProfile;
    }

    public void setUrlProfile(String urlProfile) {
        this.urlProfile = urlProfile;
    }
}
