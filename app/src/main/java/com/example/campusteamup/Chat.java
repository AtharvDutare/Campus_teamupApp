package com.example.campusteamup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyAdapters.ChatAdapter;
import com.example.campusteamup.MyModels.ChatMessageModel;
import com.example.campusteamup.MyModels.ChatRoomModel;
import com.example.campusteamup.MyModels.UserSignUpDetails;
import com.example.campusteamup.MyUtil.FirebaseChatUtil;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.MyViewModel.ViewProfile_ViewModel;
import com.example.campusteamup.databinding.ActivityChatBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class Chat extends AppCompatActivity {
    ActivityChatBinding binding ;
    String chatRoomId ;
    String otherUserId , currentUserId ,otherUserImage , otherUserName;
    ChatRoomModel chatRoomModel;
    ChatAdapter adapter;
    String otherUserFCM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Call_Method.lightActionBar(getWindow());



        initializeUserIds();
        setImageAndName();

        chatRoomId = FirebaseChatUtil.getChatRoomId(currentUserId , otherUserId);

        getOrCreateChatRoomModel(chatRoomId);

        showChatToUser();

        binding.sendMessageBtn.setOnClickListener(v->{
            String message = binding.messageInput.getText().toString().trim();
            binding.messageInput.setText("");

            if(!message.isEmpty()){
                sendMessageToUser(message);

                Log.d("FCM","Message Sent");

                fetchFCMWithUserId(task1 -> {
                    if (task1.isSuccessful()) {
                        otherUserFCM = task1.getResult();
                        if(otherUserFCM != null){
                            SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", Context.MODE_PRIVATE);
                            String currentUserName = sharedPreferences.getString("userName","");
                            String currentUseId = sharedPreferences.getString("userId","");
                            String currentUserImage = sharedPreferences.getString("userImage","");

                            Log.d("FCM","Current User Name " + currentUserName);
                            Log.d("FCM","Current User Name " + currentUseId);
                            Log.d("FCM","Current User Name " + currentUserImage);
                            Log.d("FCM", "Sending Notification ");

                            NotificationHelper.sendNotification(otherUserFCM, message, currentUserName , currentUseId , currentUserImage , this);
                        }
                        else{
                            Log.d("FCM","FCM found null");
                        }
                    } else {
                        Log.d("FCM", "Other user FCM error");
                    }

                }, otherUserId);
            }
        });


    }
    public void getOrCreateChatRoomModel(String chatRoomId){
        FirebaseChatUtil.getChatRoomReference(chatRoomId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                             chatRoomModel = task.getResult().toObject(ChatRoomModel.class);
                            if(chatRoomModel == null) {
                                // first time chat
                                chatRoomModel = new ChatRoomModel(chatRoomId , Timestamp.now(),"", Arrays.asList(currentUserId , otherUserId));
                            }
                            FirebaseChatUtil.getChatRoomReference(chatRoomId).set(chatRoomModel);
                        }
                    }
                });
    }
    public void initializeUserIds(){
        otherUserId = Objects.requireNonNull(getIntent().getStringExtra("otherUserId"));

        try{
            otherUserImage = Objects.requireNonNull(getIntent().getStringExtra("otherUserImage"));
        }
        catch (Exception ignored){

        }

        otherUserName = Objects.requireNonNull(getIntent().getStringExtra("otherUserName"));
        currentUserId = FirebaseUtil.currentUserUid();


    }

    public void sendMessageToUser(String message){
        chatRoomModel.setLastMessageSenderId(currentUserId);
        chatRoomModel.setLastMessageTimeStamp(Timestamp.now());


        FirebaseChatUtil.getChatRoomReference(chatRoomId).set(chatRoomModel);


        ChatMessageModel chatMessageModel = new ChatMessageModel(message , currentUserId , Timestamp.now());


        FirebaseChatUtil.getChatRoomMessageReference(chatRoomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("FCM","Error Sending Notification");
                    }
                });

        binding.backToViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void showChatToUser(){
        Query allChatQuery = FirebaseChatUtil.getChatRoomMessageReference(chatRoomId).orderBy("timeWhenMessageSent", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatMessageModel>options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(allChatQuery , ChatMessageModel.class)
                .build();
        adapter = new ChatAdapter(options , Chat.this);
        LinearLayoutManager  manager = new LinearLayoutManager(Chat.this);
        manager.setReverseLayout(true);
        binding.chatRecyclerView.setLayoutManager(manager);
        binding.chatRecyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                binding.chatRecyclerView.smoothScrollToPosition(0);
            }
        });
    }
    public void setImageAndName(){
        if(otherUserImage != null)
            Glide.with(this).load(otherUserImage).into(binding.imageOfOtherUser);
        if(otherUserName != null)
            binding.otherUserName.setText(otherUserName);
    }
    public void fetchFCMWithUserId(OnCompleteListener<String>listener , String otherUserId){
        FirebaseUtil.saveFCM(otherUserId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        TaskCompletionSource<String>taskCompletionSource = new TaskCompletionSource<>();

                        if (task.isSuccessful() && task.getResult() != null){
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot != null){
                                otherUserFCM = snapshot.getString("fcm_TOKEN");

                                taskCompletionSource.setResult(otherUserFCM);

                                Log.d("FCM","Other user FCM fetched " + otherUserFCM);
                            }
                            else {
                                Log.d("FCM","FCM snapshot is null");
                                taskCompletionSource.setException(new Exception("FCM snapsot is null"));
                            }
                        }
                        else {
                            Log.d("FCM","Task fail to get other user FCM");
                            taskCompletionSource.setException(new Exception("Task fail to get other user FCM"));
                        }
                        listener.onComplete(taskCompletionSource.getTask());
                    }
                });
    }

}