package com.dappcloud.humanspace.Databases;

public class User {

    String userId, phone, email, newsLater, fullName, gender, date, account, category, city, profession, interest, bio, isVerified, isFeature, website, imageurl, status, username;
    UserLocation location;

    public User(String userId, String phone, String email, String newsLater, String fullName, String gender, String date, String account, String category, String city, String profession, String interest, String bio, String isVerified, String isFeature, String website, String imageurl, String status, String username, UserLocation location) {
        this.userId = userId;
        this.phone = phone;
        this.email = email;
        this.newsLater = newsLater;
        this.fullName = fullName;
        this.gender = gender;
        this.date = date;
        this.account = account;
        this.category = category;
        this.city = city;
        this.profession = profession;
        this.interest = interest;
        this.bio = bio;
        this.isVerified = isVerified;
        this.isFeature = isFeature;
        this.website = website;
        this.imageurl = imageurl;
        this.status = status;
        this.username = username;
        this.location = location;
    }

    public User() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewsLater() {
        return newsLater;
    }

    public void setNewsLater(String newsLater) {
        this.newsLater = newsLater;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(String isVerified) {
        this.isVerified = isVerified;
    }

    public String getIsFeature() {
        return isFeature;
    }

    public void setIsFeature(String isFeature) {
        this.isFeature = isFeature;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserLocation getLocation() {
        return location;
    }

    public void setLocation(UserLocation location) {
        this.location = location;
    }
}

