package com.example.campusteamup.MyFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyCallBacks.CountRoles;
import com.example.campusteamup.MyModels.VacancyModel;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.R;
import com.example.campusteamup.databinding.FragmentPostVacancyBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Post_Vacancy extends BottomSheetDialogFragment {


    FragmentPostVacancyBinding binding;
    public Post_Vacancy() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPostVacancyBinding.inflate(inflater);
        showProgressBar(false);

        binding.findTeamMember.setOnClickListener(v -> {
            String teamName = binding.teamName.getText().toString();
            String roleLookingFor = binding.roleLookingFor.getText().toString();
            String hackathonName = binding.hackathonName.getText().toString();
            if(allFieldCorrect(teamName , roleLookingFor , hackathonName)){

                checkTotalVacancy(new CountRoles() {
                    @Override
                    public void totalRoles(int count) {
                        //nothing to do
                    }

                    @Override
                    public void countVacancy(int count) {
                        if(count >= 4){
                            Call_Method.showToast(requireContext() , "You can post only 4 role");
                        }
                        else {
                            sendVacancyToDatabase(teamName , roleLookingFor , hackathonName);
                        }
                    }
                });

            }

        });
        return binding.getRoot();
    }
    public void showProgressBar(boolean command){
        if(command){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.findTeamMember.setVisibility(View.GONE);
        }
        else{
            binding.progressBar.setVisibility(View.GONE);
            binding.findTeamMember.setVisibility(View.VISIBLE);
        }
    }
    public boolean allFieldCorrect(String teamName ,String  roleLookingFor ,String hackathonName ){
        if(teamName.trim().isEmpty()){
            binding.teamName.setError("Required");
            binding.teamName.requestFocus();
            return false;
        }
        else if(roleLookingFor.trim().isEmpty()) {
            binding.roleLookingFor.setError("Required");
            binding.roleLookingFor.requestFocus();
            return false;
        }
        else if(hackathonName.trim().isEmpty()){
            binding.hackathonName.setError("Required");
            binding.hackathonName.requestFocus();
            return false;
        }
        return true;
    }
    public void sendVacancyToDatabase(String teamName ,String  roleLookingFor ,String hackathonName){
        showProgressBar(true);
        String postedBy = FirebaseUtil.currentUserUid();
        VacancyModel vacancyModel = new VacancyModel(postedBy , teamName , roleLookingFor , hackathonName , System.currentTimeMillis());
        FirebaseUtil.allVacancy().add(vacancyModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            Call_Method.showToast(requireContext() , "Posted successfully");
                            showProgressBar(false);
                            dismiss();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Call_Method.showToast(requireContext() , "Something went wrong");
                        showProgressBar(false);
                    }
                });
    }
    public  void checkTotalVacancy(CountRoles vacancy){
      FirebaseUtil.allVacancyPostedByUser().get()
              .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                  @Override
                  public void onComplete(@NonNull Task<QuerySnapshot> task) {
                      int count = 0;
                      if(task.isSuccessful()){
                          for(DocumentSnapshot snapshot : task.getResult())
                              count++;
                          Log.d("TotalVacancy", count+"");
                          vacancy.countVacancy(count);
                      }
                  }
              });
    }
}