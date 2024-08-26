package com.example.campusteamup.MyViewModel;

import androidx.lifecycle.ViewModel;

public class ViewProfile_ViewModel extends ViewModel {
    String userId ;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
