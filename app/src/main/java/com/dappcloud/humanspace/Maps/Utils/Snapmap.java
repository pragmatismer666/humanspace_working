package com.dappcloud.humanspace.Maps.Utils;

import android.app.Application;

public class Snapmap extends Application {

    Utils utils = new Utils();
    @Override
    public void onCreate() {
        super.onCreate();
        utils.usersList(getApplicationContext()); //We fetch all the users from the firebase when the app runs first time
    }
}
