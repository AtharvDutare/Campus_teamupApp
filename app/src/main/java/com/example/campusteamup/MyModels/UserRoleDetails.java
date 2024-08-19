package com.example.campusteamup.MyModels;

public class UserRoleDetails {
    private String roleName , linkedInUrl , codingProfileUrl,userId;
    private  UserSignUpDetails userSignUpDetails;

    public UserRoleDetails(String roleName, String linkedInUrl, String codingProfileUrl,String userId,UserSignUpDetails userSignUpDetails) {
        this.roleName = roleName;
        this.linkedInUrl = linkedInUrl;
        this.codingProfileUrl = codingProfileUrl;
        this.userId = userId;
        this.userSignUpDetails = userSignUpDetails;
    }

    public UserRoleDetails() {
    }

    public UserSignUpDetails getUserSignUpDetails() {
        return userSignUpDetails;
    }

    public void setUserSignUpDetails(UserSignUpDetails userSignUpDetails) {
        this.userSignUpDetails = userSignUpDetails;
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
}
