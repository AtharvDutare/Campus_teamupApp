package com.example.campusteamup;

import android.util.Log;
import android.view.PixelCopy;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class NotificationHelper {
    public static void sendNotification(String receiverToken , String message , String senderName){

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        JSONObject json = new JSONObject();
        try{
            json.put("token",receiverToken);
            json.put("title",senderName);
            json.put("body",message);



            RequestBody requestBody = RequestBody.create(json.toString() , MediaType.parse("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .header("Content-Type", "application/json")
                    .url("https://sendnotification-kfxepdypba-uc.a.run.app")
                    .post(requestBody)
                    .build();


            client.newCall(request).enqueue(new okhttp3.Callback(){

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : null;
                        Log.d("FCM", "Notification sent successfully.");
                        if (responseBody != null) {
                            Log.d("NotificationHelper", "Response body: " + responseBody);
                        } else {
                            Log.d("NotificationHelper", "Response body is null");
                        }
                    } else {
                        Log.e("FCM", "Error sending notification: " + response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("FCM", "Notification Failure: " + e.toString());
                }
            });


        }
        catch (Exception e){
            Log.d("Notification Error",e.toString());
        }



    }
}
