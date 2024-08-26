package com.example.campusteamup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.databinding.FragmentViewCodingProfilesBinding;
import com.example.campusteamup.databinding.FragmentViewCollegeDetailsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class View_coding_profiles extends Fragment {
    String userId ;
    FragmentViewCodingProfilesBinding binding;

    public View_coding_profiles() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentViewCodingProfilesBinding.inflate(inflater);

            userId = Call_Method.userId;

        setDataToViews();
        return binding.getRoot();
    }
    public void setDataToViews(){



        FirebaseUtil.fetchCodingProfiles(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null){
                            Data_binding_coding_profiles codingProfiles  = task.getResult().toObject(Data_binding_coding_profiles.class);
                            if(codingProfiles != null ){
                                binding.setProfiles(codingProfiles);
                            }
                            else{
                                Call_Method.showToast(requireContext() , "No Coding Profiles Found");
                            }
                        }
                        else {
                            Call_Method.showToast(requireContext() , "No Details Found");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Call_Method.showToast(requireContext() , "Something went wrong");
                    }
                });
    }
}