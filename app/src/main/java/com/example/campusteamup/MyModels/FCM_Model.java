package com.example.campusteamup.MyModels;

public class FCM_Model {
    String FCM_TOKEN ;

    public FCM_Model() {
    }

    public FCM_Model(String FCM_TOKEN) {
        this.FCM_TOKEN = FCM_TOKEN;
    }

    public String getFCM_TOKEN() {
        return FCM_TOKEN;
    }

    public void setFCM_TOKEN(String FCM_TOKEN) {
        this.FCM_TOKEN = FCM_TOKEN;
    }
}
