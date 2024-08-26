package com.example.campusteamup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyAdapters.ChatAdapter;
import com.example.campusteamup.MyModels.ChatMessageModel;
import com.example.campusteamup.MyModels.ChatRoomModel;
import com.example.campusteamup.MyUtil.FirebaseChatUtil;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.MyViewModel.ViewProfile_ViewModel;
import com.example.campusteamup.databinding.ActivityChatBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Document;

import java.util.Arrays;
import java.util.Objects;

public class Chat extends AppCompatActivity {
    ActivityChatBinding binding ;
    String chatRoomId ;
    String otherUserId , currentUserId ,otherUserImage , otherUserName;
    ChatRoomModel chatRoomModel;
    ChatAdapter adapter;
    ViewProfile_ViewModel viewProfileViewModel;
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
        otherUserImage = Objects.requireNonNull(getIntent().getStringExtra("otherUserImage"));
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
                       binding.messageInput.setText("");
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
}