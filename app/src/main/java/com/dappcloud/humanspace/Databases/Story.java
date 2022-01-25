package com.dappcloud.humanspace.Databases;

public class Story {

    private String imageurl;
    private long timestart;
    private long timeend;
    private String caption;
    private String storyId;
    private String userId;

    public Story(String imageurl, long timestart, long timeend, String caption, String storyId, String userId) {
        this.imageurl = imageurl;
        this.timestart = timestart;
        this.timeend = timeend;
        this.caption = caption;
        this.storyId = storyId;
        this.userId = userId;
    }

    public Story() {
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public long getTimestart() {
        return timestart;
    }

    public void setTimestart(long timestart) {
        this.timestart = timestart;
    }

    public long getTimeend() {
        return timeend;
    }

    public void setTimeend(long timeend) {
        this.timeend = timeend;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
