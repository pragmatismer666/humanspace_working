package com.dappcloud.humanspace.Common;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences usersSession;
    SharedPreferences.Editor editor;
    Context context;

    public static final String SESSION_USERSESSION = "userSignInSessions";
    public static final String SESSION_REMEMBERME = "rememberMe";

    private static final String IS_SIGNIN = "IsSignedIn";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_FULLNAME = "fullName";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_ACCOUNT = "account";
    public static final String KEY_CITY = "city";
    public static final String KEY_DATE = "date";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_INTEREST = "interest";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_PROFESSION = "profession";
    public static final String KEY_BIO = "bio";
    public static final String KEY_WEBSITE = "website";
    public static final String KEY_IMAGEURL = "imageurl";

    private static final String IS_REMEMBERME = "IsRememberMe";
    public static final String KEY_REMUSERNAME = "username";
    public static final String KEY_REMPASSWORD = "password";

    public SessionManager(Context _context, String sessionName) {
        context = _context;
        usersSession = _context.getSharedPreferences(sessionName, Context.MODE_PRIVATE);
        editor = usersSession.edit();

    }

    public void createSignInSession(String username, String fullName, String password, String account, String city, String date, String email, String gender, String interest, String phone, String profession, String bio, String website, String imageurl) {

        editor.putBoolean(IS_SIGNIN, true);

        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_FULLNAME, fullName);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_ACCOUNT, account);
        editor.putString(KEY_CITY, city);
        editor.putString(KEY_DATE, date);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_GENDER, gender);
        editor.putString(KEY_INTEREST, interest);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_PROFESSION, profession);
        editor.putString(KEY_BIO, bio);
        editor.putString(KEY_WEBSITE, website);
        editor.putString(KEY_IMAGEURL, imageurl);

        editor.commit();
    }

    public HashMap<String, String> getUserDetailsFromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();

        userData.put(KEY_USERNAME, usersSession.getString(KEY_USERNAME, null));
        userData.put(KEY_FULLNAME, usersSession.getString(KEY_FULLNAME, null));
        userData.put(KEY_PASSWORD, usersSession.getString(KEY_PASSWORD, null));
        userData.put(KEY_ACCOUNT, usersSession.getString(KEY_ACCOUNT, null));
        userData.put(KEY_CITY, usersSession.getString(KEY_CITY, null));
        userData.put(KEY_DATE, usersSession.getString(KEY_DATE, null));
        userData.put(KEY_EMAIL, usersSession.getString(KEY_EMAIL, null));
        userData.put(KEY_GENDER, usersSession.getString(KEY_GENDER, null));
        userData.put(KEY_INTEREST, usersSession.getString(KEY_INTEREST, null));
        userData.put(KEY_PHONE, usersSession.getString(KEY_PHONE, null));
        userData.put(KEY_PROFESSION, usersSession.getString(KEY_PROFESSION, null));
        userData.put(KEY_BIO, usersSession.getString(KEY_BIO, null));
        userData.put(KEY_WEBSITE, usersSession.getString(KEY_WEBSITE, null));
        userData.put(KEY_IMAGEURL, usersSession.getString(KEY_IMAGEURL, null));

        return userData;
    }

    public boolean checkSignIn() {
        if (usersSession.getBoolean(IS_SIGNIN, false)) {
            return true;

        } else {
            return false;
        }
    }

    public void signOutUserFromSession() {
        editor.clear();
        editor.commit();
    }

    public void createRememberMeSession(String username, String password) {

        editor.putBoolean(IS_REMEMBERME, true);

        editor.putString(KEY_REMUSERNAME, username);
        editor.putString(KEY_REMPASSWORD, password);

        editor.commit();
    }

    public HashMap<String, String> getRememberMeDetailsFromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();

        userData.put(KEY_REMUSERNAME, usersSession.getString(KEY_REMUSERNAME, null));
        userData.put(KEY_REMPASSWORD, usersSession.getString(KEY_REMPASSWORD, null));

        return userData;
    }

    public boolean checkRememberMe() {
        if (usersSession.getBoolean(IS_REMEMBERME, false)) {
            return true;

        } else {
            return false;
        }
    }

}
