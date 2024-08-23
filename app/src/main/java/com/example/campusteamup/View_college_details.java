package com.example.campusteamup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.campusteamup.Method_Helper.Call_Method;

import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.databinding.FragmentViewCollegeDetailsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentSnapshot;


public class View_college_details extends Fragment {
    FragmentViewCollegeDetailsBinding binding;
    String userId ;

    public View_college_details() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         binding = FragmentViewCollegeDetailsBinding.inflate(inflater);
         if(getArguments() != null){
             userId = getArguments().getString("userId");
         }
         setDataToViews();
         return binding.getRoot();
    }
    public void setDataToViews(){



        FirebaseUtil.fetchCollegeDetails(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null){
                            Data_binding_college_details collegeDetails  = task.getResult().toObject(Data_binding_college_details.class);
                            if(collegeDetails != null ){
                                binding.setDetails(collegeDetails);
                            }
                            else{
                                Call_Method.showToast(requireContext() , "No College Details Found");
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