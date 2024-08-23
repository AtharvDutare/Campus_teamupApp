package com.example.campusteamup.MyModels;

import com.google.firebase.Timestamp;

public class ChatMessageModel {
    String message , senderId;
    Timestamp timeWhenMessageSent;

    public ChatMessageModel() {
    }

    public ChatMessageModel(String message, String senderId, Timestamp timeWhenMessageSent) {
        this.message = message;
        this.senderId = senderId;
        this.timeWhenMessageSent = timeWhenMessageSent;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Timestamp getTimeWhenMessageSent() {
        return timeWhenMessageSent;
    }

    public void setTimeWhenMessageSent(Timestamp timeWhenMessageSent) {
        this.timeWhenMessageSent = timeWhenMessageSent;
    }
}
