package com.example.campusteamup.MyFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyModels.Coding_Profiles_Model;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.R;
import com.example.campusteamup.databinding.FragmentCodingProfileDetailsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;


public class Coding_Profile_Details extends Fragment {
    FragmentCodingProfileDetailsBinding binding;
    String  githubUrl , leetcodeUrl , gfgUrl , codechefUrl , codeforcesUrl;
    String currentUserId ;

    public Coding_Profile_Details() {
        // Required empty public constructor
        currentUserId = FirebaseUtil.currentUserUid();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCodingProfileDetailsBinding.inflate(inflater);
        notAllowToEdit();
        setCodingProfilesDetails();

        showProgressBar(true);


        binding.editPersonalDetailsBtn.setOnClickListener(v -> {
            binding.updatePersonalDetailsBtn.setVisibility(View.VISIBLE);
            allToEdit(InputType.TYPE_CLASS_TEXT);
        });

        binding.updatePersonalDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar(true);
                githubUrl = binding.githubInput.getText().toString();
                leetcodeUrl = binding.leetcodeInput.getText().toString();
                gfgUrl = binding.gfgInput.getText().toString();
                codechefUrl = binding.codechefInput.getText().toString();
                codeforcesUrl = binding.codeforcesInput.getText().toString();

                if(areUrlValid(githubUrl , leetcodeUrl,gfgUrl,codeforcesUrl,codechefUrl)){
                    notAllowToEdit();
                    updateDataToDatabase(githubUrl ,leetcodeUrl,gfgUrl,codeforcesUrl,codechefUrl);
                }
                else {
                    showProgressBar(false);
                }


            }
        });
        return binding.getRoot();
    }
    public void showProgressBar(boolean command){
        if(command){
            binding.editPersonalDetailsBtn.setVisibility(View.INVISIBLE);
            binding.updatePersonalDetailsBtn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else{
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.editPersonalDetailsBtn.setVisibility(View.VISIBLE);
        }
    }
    public void allToEdit(int inputType){
        binding.leetcodeInput.setInputType(inputType | InputType.TYPE_TEXT_VARIATION_NORMAL);
        binding.gfgInput.setInputType(inputType);
        binding.githubInput.setInputType(inputType);
        binding.codechefInput.setInputType(inputType);
        binding.codeforcesInput.setInputType(inputType);
    }
    public void notAllowToEdit(){
        binding.leetcodeInput.setInputType(InputType.TYPE_NULL);
        binding.gfgInput.setInputType(InputType.TYPE_NULL);
        binding.githubInput.setInputType(InputType.TYPE_NULL);
        binding.codechefInput.setInputType(InputType.TYPE_NULL);
        binding.codeforcesInput.setInputType(InputType.TYPE_NULL);

    }
    public void updateDataToDatabase(String githubUrl , String leetcodeUrl , String gfgUrl , String codeforcesUrl , String codechefUrl){

        FirebaseUtil.fetchCodingProfiles(currentUserId).set(new Coding_Profiles_Model(githubUrl,leetcodeUrl,gfgUrl,codeforcesUrl,codechefUrl))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            showProgressBar(false);
                            Call_Method.showToast(requireContext(),"Updated Successfully");
                        }
                        else {
                            Call_Method.showToast(requireContext(),"Task not success");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showProgressBar(false);
                        Call_Method.showToast(requireContext(),"Something went wrong");
                    }
                });
    }
    public boolean areUrlValid(String githubUrl , String leetcodeUrl , String gfgUrl , String codeforcesUrl , String codechefUrl){
        if(!leetcodeUrl.isEmpty() &&  !Patterns.WEB_URL.matcher(leetcodeUrl).matches() )
        {
         binding.leetcodeInput.requestFocus();
         binding.leetcodeInput.setError("Please enter valid url");
         return false;
        }
        if(!gfgUrl.isEmpty() &&  !Patterns.WEB_URL.matcher(gfgUrl).matches() )
        {
            binding.gfgInput.requestFocus();
            binding.gfgInput.setError("Please enter valid url");
            return false;
        }
        if(!codeforcesUrl.isEmpty() && !Patterns.WEB_URL.matcher(codeforcesUrl).matches() )
        {
            binding.codeforcesInput.requestFocus();
            binding.codeforcesInput.setError("Please enter valid url");
            return false;
        }
        if(!codechefUrl.isEmpty() && !Patterns.WEB_URL.matcher(codechefUrl).matches() )
        {
            binding.codechefInput.requestFocus();
            binding.codechefInput.setError("Please enter valid url");
            return false;
        }
        if(!githubUrl.isEmpty() && !Patterns.WEB_URL.matcher(githubUrl).matches() )
        {
            binding.githubInput.requestFocus();
            binding.githubInput.setError("Please enter valid url");
            return false;
        }
        return true;
    }
    public void setCodingProfilesDetails(){
        FirebaseUtil.fetchCodingProfiles(currentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                showProgressBar(false);
                    if (documentSnapshot != null) {
                        Coding_Profiles_Model codingProfilesModel = documentSnapshot.toObject(Coding_Profiles_Model.class);
                        if (codingProfilesModel != null) {
                            binding.githubInput.setText(codingProfilesModel.getGithubUrl());
                            binding.leetcodeInput.setText(codingProfilesModel.getLeetcodeUrl());
                            binding.gfgInput.setText(codingProfilesModel.getGfgUrl());
                            binding.codechefInput.setText(codingProfilesModel.getCodechefUrl());
                            binding.codeforcesInput.setText(codingProfilesModel.getCodeforcesUrl());
                        }
                    } else {
                        Call_Method.showToast(requireContext(), "No Coding profiles found");
                    }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showProgressBar(false);
                        Call_Method.showToast(requireContext(),"Something went wrong");
                    }
                });
    }
}