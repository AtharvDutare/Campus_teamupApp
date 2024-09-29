package com.example.campusteamup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;

import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyFragments.Notifications_Section;
import com.example.campusteamup.MyFragments.Team_Details;
import com.example.campusteamup.databinding.ActivityDrawerItemsBinding;

import java.util.Objects;

public class Drawer_Items_Activity extends AppCompatActivity {

    ActivityDrawerItemsBinding binding;
    Fragment fragmentToLoad;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDrawerItemsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        Call_Method.lightActionBar(getWindow());

        setSupportActionBar(binding.toolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        fragmentManager = getSupportFragmentManager();

        String fragmentTeamDetails = getIntent().getStringExtra("team_details");
        String fragmentNotification = getIntent().getStringExtra("notification_details");

        if(fragmentNotification == null){  // means that fragmentTeamDetails is set
            fragmentToLoad = new Team_Details();
            fragmentManager.beginTransaction()
                    .add(R.id.mainFrameLayout,fragmentToLoad)
                    .commit();
        }
        else if(fragmentTeamDetails == null || fragmentTeamDetails.isEmpty()){
            String senderName = getIntent().getStringExtra("title");
            String senderId = getIntent().getStringExtra("senderId");
            String senderImage = getIntent().getStringExtra("senderImage");

            Bundle bundle = new Bundle();
            bundle.putString("senderName",senderName);
            bundle.putString("senderId",senderId);
            bundle.putString("senderImage",senderImage);

            fragmentToLoad = new Notifications_Section();
            fragmentToLoad.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .add(R.id.mainFrameLayout , fragmentToLoad)
                    .commit();
        }


    }
}