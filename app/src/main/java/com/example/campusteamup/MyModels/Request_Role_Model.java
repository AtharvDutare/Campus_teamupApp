package com.example.campusteamup.MyModels;

public class Request_Role_Model {
    String requestedBy , requestedTo, userName , userImage , userEmail;

    public Request_Role_Model() {
    }

    public Request_Role_Model(String requestedBy,String requestedTo ,  String userName, String userImage , String userEmail) {
        this.requestedBy = requestedBy;
        this.userName = userName;
        this.userImage = userImage;
        this.requestedTo = requestedTo;
        this.userEmail = userEmail;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getRequestedTo() {
        return requestedTo;
    }

    public void setRequestedTo(String requestedTo) {
        this.requestedTo = requestedTo;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
