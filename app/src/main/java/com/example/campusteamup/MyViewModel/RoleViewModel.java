package com.example.campusteamup.MyViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RoleViewModel extends ViewModel {
    MutableLiveData<Boolean> rolePosted = new MutableLiveData<>(false);

    public void setRolePosted(boolean command){
        rolePosted.setValue(command);
    }
    public LiveData<Boolean> getRolePosted(){
        return rolePosted;
    }
}
