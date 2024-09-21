package com.example.campusteamup.FCM;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import android.os.Build;
import androidx.annotation.NonNull;

import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyModels.FCM_Model;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    static String FCM_TOKEN;
    String CHANNEL_ID = "Chat_Channel";


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("NotificationDetails","Channel Created");
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "My Notification Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }


        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Log.d("NotificationDetails", "Title: " + title);
            Log.d("NotificationDetails", "Body: " + body);
        } else {
            Log.d("NotificationDetails", "Notification is null");
        }

        if (remoteMessage.getData().size() > 0) {
            String senderId = remoteMessage.getData().get("senderId");
            String senderImage = remoteMessage.getData().get("senderImage");
            Log.d("NotificationDetails", "Sender ID: " + senderId);
            Log.d("NotificationDetails", "Sender Image: " + senderImage);
        } else {
            Log.d("NotificationDetails", "Data is empty");
        }





    }

    public static void generateFCM(OnCompleteListener<String> listener){
        try{
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()){
                                Log.d("FCM","Exception is" + task.getException() );
                                return;
                            }
                            FCM_TOKEN = task.getResult();
                            listener.onComplete(task);
                            Log.d("FCM",FCM_TOKEN);
                        }
                    });
        }
        catch (Exception e){
           Log.d("FCM","Generation Failed");
        }

    }
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        FCM_TOKEN = token;
        Log.d("FCM UPDated",FCM_TOKEN);
        saveFcmToFirebase(FCM_TOKEN);
    }
    public static void saveFcmToFirebase(String fcm){

        FCM_Model fcmModel = new FCM_Model(FCM_TOKEN);

        generateUserId(task -> {
            if(task.isSuccessful()){
                String currentUserId = task.getResult();
                if(currentUserId != null){
                    FirebaseUtil.saveFCM(currentUserId).set(fcmModel)
                            .addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()){
                                    Log.d("FCM Saved ","FCM Saved");
                                }
                                else {
                                    Log.d("FCM Save Error", task1.getException() + " ");
                                }
                            });
                }
                else{
                    Log.d("User null",currentUserId + " ");
                }

            }
        });


    }
    public static void generateUserId(OnCompleteListener<String>listener){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            String currentUserId = auth.getCurrentUser().getUid();

            TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();
            taskCompletionSource.setResult(currentUserId);

            listener.onComplete(taskCompletionSource.getTask());
        } else {
            Log.d("FCM", "User not logged in");

            TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();
            taskCompletionSource.setException(new Exception("User not logged in"));

            listener.onComplete(taskCompletionSource.getTask());
        }

    }

}
