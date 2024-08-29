package com.example.campusteamup.MyFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.campusteamup.R;
import com.example.campusteamup.databinding.FragmentTeamDetailsBinding;


public class Team_Details extends Fragment {
    FragmentTeamDetailsBinding binding ;
    public Team_Details() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTeamDetailsBinding.inflate(inflater);

        notAllowToEditTeamDetails();

        binding.editTeamDetailsBtn.setOnClickListener(v -> {
            binding.editTeamDetailsBtn.setVisibility(View.GONE);
            binding.updateTeamDetailsBtn.setVisibility(View.VISIBLE);
            allowToEditTeamDetails();
        });

        binding.updateTeamDetailsBtn.setOnClickListener(v -> {

        });

        return binding.getRoot();
    }
    public void notAllowToEditTeamDetails(){
        int inputType = InputType.TYPE_NULL;
        binding.teamLeaderInput.setInputType(inputType);
        binding.member1Input.setInputType(inputType);
        binding.member2Input.setInputType(inputType);
        binding.member3Input.setInputType(inputType);
        binding.member4Input.setInputType(inputType);
        binding.member5Input.setInputType(inputType);

    }
    public void allowToEditTeamDetails(){
        int inputType = InputType.TYPE_CLASS_TEXT;

        binding.teamLeaderInput.setInputType(inputType);
        binding.member1Input.setInputType(inputType);
        binding.member2Input.setInputType(inputType);
        binding.member3Input.setInputType(inputType);
        binding.member4Input.setInputType(inputType);
        binding.member5Input.setInputType(inputType);
    }
}