package com.dappcloud.humanspace.Databases.ChatNotifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                   "Content-type:application/json",
                   "Authorization:key=AAAApEK4CVo:APA91bG8iezBsffi1fXxdpcxttF5rbHlu87e2erbYK3xzPfdF5gw_FhL7e_kJc7S-wkMTUa_rTHAZMEwQeV1RHTC1ialkSLVSi7K5x7H1uFE9J4DK1DWD_tvJJZupZB5L7tiP0jUBtvD"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
