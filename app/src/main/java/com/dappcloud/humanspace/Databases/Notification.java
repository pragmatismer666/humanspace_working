package com.dappcloud.humanspace.Databases;

public class Notification {
    private String userId;
    private String text;

    public Notification(String userId, String text) {
        this.userId = userId;
        this.text = text;
    }

    public Notification() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
