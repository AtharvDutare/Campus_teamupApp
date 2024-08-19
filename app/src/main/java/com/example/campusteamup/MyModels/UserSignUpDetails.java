package com.example.campusteamup.MyModels;

import com.google.firebase.Timestamp;

public class UserSignUpDetails {
    private String email ;
    private Timestamp timestamp ;
    private String userId ;
    private String userName;

    public UserSignUpDetails() {
    }

    public UserSignUpDetails(String email, Timestamp timestamp, String userId , String userName) {
        this.email = email;
        this.timestamp = timestamp;
        this.userId = userId;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
