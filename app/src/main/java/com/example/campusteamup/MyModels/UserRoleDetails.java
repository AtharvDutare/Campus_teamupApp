package com.example.campusteamup.MyModels;

public class UserRoleDetails {
    private String roleName , linkedInUrl , codingProfileUrl,userId , userName, userImage;


    public UserRoleDetails(String roleName, String linkedInUrl, String codingProfileUrl,String userId, String userName , String userImage) {
        this.roleName = roleName;
        this.linkedInUrl = linkedInUrl;
        this.codingProfileUrl = codingProfileUrl;
        this.userId = userId;
        this.userName = userName;
        this.userImage = userImage;
    }

    public UserRoleDetails() {
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getLinkedInUrl() {
        return linkedInUrl;
    }

    public void setLinkedInUrl(String linkedInUrl) {
        this.linkedInUrl = linkedInUrl;
    }

    public String getCodingProfileUrl() {
        return codingProfileUrl;
    }

    public void setCodingProfileUrl(String codingProfileUrl) {
        this.codingProfileUrl = codingProfileUrl;
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
}
