package com.example.campusteamup.MyModels;

public class Team_Members_Model {
    String name , email ;

    public Team_Members_Model() {
    }

    public Team_Members_Model(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
