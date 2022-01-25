package com.dappcloud.humanspace.Databases;

public class PostComment {

    private String comment;
    private String publisher;

    public PostComment(String comment, String publisher) {
        this.comment = comment;
        this.publisher = publisher;
    }

    public PostComment() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
