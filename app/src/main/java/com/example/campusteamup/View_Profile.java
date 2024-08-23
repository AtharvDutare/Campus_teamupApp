package com.example.campusteamup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.databinding.ActivityViewProfileBinding;

import java.util.Objects;

public class View_Profile extends AppCompatActivity {
    ActivityViewProfileBinding binding;
    FragmentManager fragmentManager;
    String userId , userLinkedIn , userImage;
    Fragment fragmentToLoad ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        manageToolbar();
        Call_Method.lightActionBar(getWindow());

        if(getIntent() != null){
            userId = getIntent().getStringExtra("userId");
            userImage = getIntent().getStringExtra("userImage");
            userLinkedIn = getIntent().getStringExtra("linkedInUrl");
        }

        if(userImage != null){
            Glide.with(this).load(userImage).into(binding.viewImage);
        }

        Bundle userIdBundle= new Bundle();
        userIdBundle.putString("userId",userId);

        // default show college details

        fragmentManager = getSupportFragmentManager();
        fragmentToLoad = new View_college_details();
        fragmentToLoad.setArguments(userIdBundle);
        fragmentManager.beginTransaction()
                        .add(R.id.mainFrameLayout , fragmentToLoad)
                                .commit();

        Fragment currentFragment = fragmentManager.findFragmentById(R.id.mainFrameLayout);

        binding.codingDetails.setOnClickListener(v->{
            fragmentToLoad = new View_coding_profiles();
            fragmentToLoad.setArguments(userIdBundle);
            manageBtnBackground(binding.codingDetails);
            fragmentManager.beginTransaction()
                    .replace(R.id.mainFrameLayout , fragmentToLoad)
                    .commit();
        });
        binding.collegeDetails.setOnClickListener(v->{
            manageBtnBackground(binding.collegeDetails);
            if(currentFragment instanceof View_college_details)
                return;
            else{
                fragmentToLoad = new View_college_details();
                fragmentToLoad.setArguments(userIdBundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.mainFrameLayout , fragmentToLoad)
                        .commit();
            }
        });

        binding.sendMessage.setOnClickListener(v->{
            Intent intent = new Intent(View_Profile.this , Chat.class);
            intent.putExtra("otherUserId",userId);
            startActivity(intent);
        });



    }
    public void manageToolbar(){
        setSupportActionBar(binding.toolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }
    public void manageBtnBackground(TextView textView ){
        if(textView == binding.codingDetails){
            binding.codingDetails.setBackground(AppCompatResources.getDrawable(this ,R.drawable.black_bg_rounded_btn));
            binding.codingDetails.setTextColor(getResources().getColor(R.color.white, null));
            binding.collegeDetails.setBackground(AppCompatResources.getDrawable(this ,R.drawable.rounded_btn));
            binding.collegeDetails.setTextColor(getResources().getColor(R.color.black, null));
        }
        else {
            binding.collegeDetails.setBackground(AppCompatResources.getDrawable(this ,R.drawable.black_bg_rounded_btn));
            binding.collegeDetails.setTextColor(getResources().getColor(R.color.white, null));
            binding.codingDetails.setBackground(AppCompatResources.getDrawable(this ,R.drawable.rounded_btn));
            binding.codingDetails.setTextColor(getResources().getColor(R.color.black, null));
        }
    }
}