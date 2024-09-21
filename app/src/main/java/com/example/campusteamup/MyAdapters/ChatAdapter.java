package com.example.campusteamup.MyAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusteamup.Chat;
import com.example.campusteamup.MyModels.ChatMessageModel;
import com.example.campusteamup.MyUtil.FirebaseChatUtil;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.databinding.ChatRowBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatAdapter extends FirestoreRecyclerAdapter<ChatMessageModel , ChatAdapter.ChatViewHolder> {


    Context context;
    public ChatAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options , Context context) {
        super(options);
        this.context = context;
    }
    @Override
    protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull ChatMessageModel model) {
        if(model.getSenderId().equals(FirebaseUtil.currentUserUid())){
            holder.binding.currentUser.setVisibility(View.VISIBLE);
            holder.binding.otherUser.setVisibility(View.GONE);
            holder.binding.messageTimeOfOther.setVisibility(View.GONE);
            holder.binding.currentUser.setText(model.getMessage());

            holder.binding.messageTimeOfCurrent.setVisibility(View.VISIBLE);
            holder.binding.messageTimeOfCurrent.setText(formatDate(model.getTimeWhenMessageSent()));
        }
        else{
            holder.binding.otherUser.setVisibility(View.VISIBLE);
            holder.binding.currentUser.setVisibility(View.GONE);
            holder.binding.otherUser.setText(model.getMessage());
            holder.binding.messageTimeOfCurrent.setVisibility(View.GONE);

            holder.binding.messageTimeOfOther.setVisibility(View.VISIBLE);
            holder.binding.messageTimeOfOther.setText(formatDate(model.getTimeWhenMessageSent()));

        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ChatRowBinding binding = ChatRowBinding.inflate(inflater);

        return new ChatViewHolder(binding);
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{
        ChatRowBinding binding;
        public ChatViewHolder(ChatRowBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    public String formatDate(Timestamp timestamp){

        Date date = timestamp.toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        String currentTime = dateFormat.format(date);
        return currentTime;
    }
}
