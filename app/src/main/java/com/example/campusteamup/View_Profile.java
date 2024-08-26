package com.example.campusteamup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.MyViewModel.ViewProfile_ViewModel;
import com.example.campusteamup.databinding.ActivityViewProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

public class View_Profile extends AppCompatActivity {
    ActivityViewProfileBinding binding;
    FragmentManager fragmentManager;
    String userId , userLinkedIn , userImage , userName;
    Fragment fragmentToLoad ;
    ViewProfile_ViewModel viewProfileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        manageToolbar();
        Call_Method.lightActionBar(getWindow());



        String userIdFromIntent = getIntent().getStringExtra("userId");
        String userImageFromIntent = getIntent().getStringExtra("userImage");
        String userNameFromIntent = getIntent().getStringExtra("userName");

        if(userIdFromIntent != null){   // this is when we browse from adapter to this activity
            Call_Method.userId = userIdFromIntent;
            Call_Method.userImage = userImageFromIntent;
            userId = userIdFromIntent;
            userImage = userImageFromIntent;
            userName = userNameFromIntent;
        }
        else {                           // this is when we browse back to this activity from chatActivity
            userId = Call_Method.userId;
            userImage = Call_Method.userImage;
            userName  = Call_Method.userName;
        }


        if(userId != null){
            binding.progressBar.setVisibility(View.VISIBLE);
            FirebaseUtil.differentUserImages(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot snapshot = task.getResult();
                        if(snapshot.exists()){
                            userImage = snapshot.getString("imageUri");
                            binding.progressBar.setVisibility(View.GONE);
                            Glide.with(View_Profile.this).load(userImage).into(binding.viewImage);
                        }

                    }
                }
            });


        }



        // default show college details

        fragmentManager = getSupportFragmentManager();
        fragmentToLoad = new View_college_details();
        fragmentManager.beginTransaction()
                        .add(R.id.mainFrameLayout , fragmentToLoad)
                                .commit();

        Fragment currentFragment = fragmentManager.findFragmentById(R.id.mainFrameLayout);

        binding.codingDetails.setOnClickListener(v->{
            fragmentToLoad = new View_coding_profiles();
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
                fragmentManager.beginTransaction()
                        .replace(R.id.mainFrameLayout , fragmentToLoad)
                        .commit();
            }
        });

        binding.sendMessage.setOnClickListener(v->{
            Intent intent = new Intent(View_Profile.this , Chat.class);

            intent.putExtra("otherUserId",userId);
            intent.putExtra("otherUserImage",userImage);
            intent.putExtra("otherUserName",userName);
            startActivity(intent);
        });



    }
    public void manageToolbar(){
        setSupportActionBar(binding.toolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }
    public void manageBtnBackground(TextView textView ) {
        if (textView == binding.codingDetails) {
            binding.codingDetails.setBackground(AppCompatResources.getDrawable(this, R.drawable.blue_bg_rounded_btn));
            binding.codingDetails.setTextColor(getResources().getColor(R.color.white, null));
            binding.collegeDetails.setBackground(AppCompatResources.getDrawable(this, R.drawable.rounded_btn));
            binding.collegeDetails.setTextColor(getResources().getColor(R.color.blue_btn, null));
        } else {
            binding.collegeDetails.setBackground(AppCompatResources.getDrawable(this, R.drawable.blue_bg_rounded_btn));
            binding.collegeDetails.setTextColor(getResources().getColor(R.color.white, null));
            binding.codingDetails.setBackground(AppCompatResources.getDrawable(this, R.drawable.rounded_btn));
            binding.codingDetails.setTextColor(getResources().getColor(R.color.blue_btn, null));
        }
    }


}