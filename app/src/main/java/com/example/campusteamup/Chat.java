package com.example.campusteamup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyModels.ChatMessageModel;
import com.example.campusteamup.MyModels.ChatRoomModel;
import com.example.campusteamup.MyUtil.FirebaseChatUtil;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.w3c.dom.Document;

import java.util.Arrays;
import java.util.Objects;

public class Chat extends AppCompatActivity {
    ActivityChatBinding binding ;
    String chatRoomId ;
    String otherUserId , currentUserId ;
    ChatRoomModel chatRoomModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Call_Method.lightActionBar(getWindow());

        manageToolbar();

        initializeUserIds();

        chatRoomId = FirebaseChatUtil.getChatRoomId(currentUserId , otherUserId);

        getOrCreateChatRoomModel(chatRoomId);

        binding.sendMessageBtn.setOnClickListener(v->{
            String message = binding.messageInput.getText().toString().trim();
            if(!message.isEmpty()){
                sendMessageToUser(message);
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
                       binding.messageInput.setText("");
                    }
                });

    }
    public void manageToolbar(){
        setSupportActionBar(binding.toolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }
}