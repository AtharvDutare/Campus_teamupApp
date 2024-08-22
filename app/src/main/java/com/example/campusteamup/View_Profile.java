package com.example.campusteamup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.TextView;

import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.databinding.ActivityViewProfileBinding;

import java.util.Objects;

public class View_Profile extends AppCompatActivity {
    ActivityViewProfileBinding binding;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        manageToolbar();
        Call_Method.lightActionBar(getWindow());


        // default show college details
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                        .add(R.id.mainFrameLayout , new View_college_details())
                                .commit();

        Fragment currentFragment = fragmentManager.findFragmentById(R.id.mainFrameLayout);

        binding.codingDetails.setOnClickListener(v->{
            manageBtnBackground(binding.codingDetails);
            fragmentManager.beginTransaction()
                    .replace(R.id.mainFrameLayout , new View_coding_profiles())
                    .commit();
        });
        binding.collegeDetails.setOnClickListener(v->{
            manageBtnBackground(binding.collegeDetails);
            if(currentFragment instanceof View_college_details)
                return;
            else{
                fragmentManager.beginTransaction()
                        .replace(R.id.mainFrameLayout , new View_college_details())
                        .commit();
            }
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