package com.dappcloud.humanspace.Databases;

import android.view.View;

import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;

public class ViewList {

    String username;
    String account;
    String category;
    String gender;
    View view;
    MarkerView markerView;
    double lat;
    double lng;

    public ViewList(String username, String account, String category, String gender, View view, MarkerView markerView, Double lat, Double lng) {
        this.username = username;
        this.account = account;
        this.category = category;
        this.gender = gender;
        this.view = view;
        this.markerView = markerView;
        this.lat = lat;
        this.lng = lng;
    }

    public ViewList() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public MarkerView getMarkerView() {
        return markerView;
    }

    public void setMarkerView(MarkerView markerView) {
        this.markerView = markerView;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

}
