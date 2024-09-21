package com.example.campusteamup;

import android.content.Context;
import android.util.Log;
import android.view.PixelCopy;

import androidx.annotation.NonNull;

import com.example.campusteamup.Method_Helper.Call_Method;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class NotificationHelper {
    static String TAG = "NotificationDetails";
    public static void sendNotification(String receiverToken , String message , String senderName , String senderId , String senderImage , Context context){


        Log.d(TAG," " + receiverToken);
        Log.d(TAG," " + message);
        Log.d(TAG," " + senderName);
        Log.d(TAG," " + senderId);
        Log.d(TAG," " + senderImage);

        if (receiverToken == null || receiverToken.isEmpty() ||
                message == null || message.isEmpty() ||
                senderName == null || senderName.isEmpty() ||
                senderId == null || senderId.isEmpty() ) {


            Log.e(TAG, "One or more input values are missing or empty.");
            return;
        }
        if( senderImage.isEmpty()){
            senderImage = "noImage";
        }


        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .readTimeout(30 , TimeUnit.SECONDS)
                .build();

        JSONObject json = new JSONObject();
        try{
            json.put("token", receiverToken);


            json.put("title", senderName);
            json.put("body", message);


            json.put("senderId", senderId);
            json.put("senderImage", senderImage);



            Log.d("NotificationPayload", json.toString());

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
                            Log.d("FCM", "Response body: " + responseBody);
                        } else {
                            Log.d("FCM", "Response body is null");
                        }
                    } else {
                        Log.e("FCM", "Error sending notification: " + response.message() + response.code() + response.headers());
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Call_Method.showToast(context , "Network Error");
                    Log.e("FCM", "Notification Failure: " + e);
                }
            });


        }
        catch (Exception e){
            Log.d("Notification Error",e.toString());
        }



    }
}
