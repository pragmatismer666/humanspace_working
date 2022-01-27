package com.dappcloud.humanspace.Maps.Utils;

import com.dappcloud.humanspace.Databases.User;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Common {

    public static String[] categories = new String[]{"Male","Female","Single", "Car","Nanny","Cleaner","Doctor","Lawyer","Mechanic", "Stripper", "Tutor", "Graphic designer/Web developer"};
    public static final String USER_REF = "Users";
    public static User CurrentUser;
    public static List<User> UsersList = new ArrayList<>();
    public static User UserSelected;
    public static final int CALL_ACTIVITY_RESULT = 101;

    public static float GetBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.getLatitude() - end.getLatitude());
        double lng = Math.abs(begin.getLongitude() - end.getLongitude());

        double v = Math.toDegrees(Math.atan(lat / lng));
        if (begin.getLatitude() < end.getLatitude() && begin.getLongitude() < end.getLongitude()) {
            return (float) v;
        } else if (begin.getLatitude() >= end.getLatitude() && begin.getLongitude() < end.getLongitude())
            return (float) ((90 - v) + 90);
        else if (begin.getLatitude() >= end.getLatitude() && begin.getLongitude() >= end.getLongitude())
            return (float) (v + 180);
        else if (begin.getLatitude() < end.getLatitude() && begin.getLongitude() >= end.getLongitude())
            return (float) ((90 - v) + 270);
        return -1;
    }
}
