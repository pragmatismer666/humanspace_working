package com.dappcloud.humanspace.Databases;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private String type;
    private String createdOn;
    private String createdAt;
    private long sentTimeMills;
    private String isSeen;

    public Chat(String sender, String receiver, String message, String type, String createdOn, String createdAt, long sentTimeMills, String isSeen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.type = type;
        this.createdOn = createdOn;
        this.createdAt = createdAt;
        this.sentTimeMills = sentTimeMills;
        this.isSeen = isSeen;
    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public long getSentTimeMills() {
        return sentTimeMills;
    }

    public void setSentTimeMills(long sentTimeMills) {
        this.sentTimeMills = sentTimeMills;
    }

    public String getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(String isSeen) {
        this.isSeen = isSeen;
    }
}
