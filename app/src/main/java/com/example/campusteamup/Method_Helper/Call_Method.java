package com.example.campusteamup.Method_Helper;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class Call_Method {
    public static void showToast(Context context , String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public static void lightActionBar(Window window){
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}
