package com.dappcloud.humanspace.Databases;

public class BRequest {

    private String userid;
    private String fullname;
    private String knownas;
    private String business;
    private String description;
    private String category;
    private String photourl;
    private String status;
    private String submitted;

    public BRequest(String userid, String fullname, String knownas, String business, String description, String category, String photourl, String status, String submitted) {
        this.userid = userid;
        this.fullname = fullname;
        this.knownas = knownas;
        this.business = business;
        this.description = description;
        this.category = category;
        this.photourl = photourl;
        this.status = status;
        this.submitted = submitted;
    }

    public BRequest() {
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getKnownas() {
        return knownas;
    }

    public void setKnownas(String knownas) {
        this.knownas = knownas;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubmitted() {
        return submitted;
    }

    public void setSubmitted(String submitted) {
        this.submitted = submitted;
    }
}
