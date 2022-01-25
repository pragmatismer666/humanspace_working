package com.dappcloud.humanspace.Databases;

public class Post {

    private String postId;
    private String postUrl;
    private String type;
    private String createdOn;
    private long createdAt;
    private String caption;
    private String publisher;

    public Post(String postId, String postUrl, String type, String createdOn, long createdAt, String caption, String publisher) {
        this.postId = postId;
        this.postUrl = postUrl;
        this.type = type;
        this.createdOn = createdOn;
        this.createdAt = createdAt;
        this.caption = caption;
        this.publisher = publisher;
    }

    public Post() {
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
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

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
