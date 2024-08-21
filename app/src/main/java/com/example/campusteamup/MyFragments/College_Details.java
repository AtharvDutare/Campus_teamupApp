package com.example.campusteamup.MyFragments;

import android.os.Bundle;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.telecom.Call;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyModels.College_Details_Model;
import com.example.campusteamup.MyModels.Personal_Details_Model;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.R;
import com.example.campusteamup.databinding.FragmentPersonalDetailsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.net.CacheRequest;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyUtil.FirebaseUtil;

import com.example.campusteamup.databinding.FragmentCollegeDetailsBinding;

public class College_Details extends Fragment {
    FragmentCollegeDetailsBinding binding ;
    String [] branchOptions = {"CSE","IT","ECE","AIML","DS","Civil","Mechanical"};
    ArrayAdapter<String> branchAdapter;
    String [] yearOptions = {"1st year" , "2nd year" , "3rd year","4th year"};
    ArrayAdapter<String> yearAdapter;
    private String selectedBranch , selectedYear , selectedCourse;
    String currentUserId;
    public College_Details() {
        // Required empty public constructor
        currentUserId  = FirebaseUtil.currentUserUid();
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCollegeDetailsBinding.inflate(inflater);

        setCollegeDetails();


        enableSpinner(false);
        setUpAdapters();
        showProgressBar(true);
        selectedCourse = binding.courseInput.getText().toString(); // Only Btech

        binding.editPersonalDetailsBtn.setOnClickListener(v -> {
            binding.updatePersonalDetailsBtn.setVisibility(View.VISIBLE);
            enableSpinner(true);
        });

        binding.updatePersonalDetailsBtn.setOnClickListener(v -> {
            showProgressBar(true);
            updateDataToDatabase();
        });
        binding.yearInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = yearOptions[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedYear = yearOptions[0];
            }
        });
        binding.branchInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBranch = branchOptions[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedBranch = branchOptions[0];
            }
        });

        return binding.getRoot();
    }
    public void enableSpinner(boolean command){
        if(command){
            binding.branchInput.setEnabled(true);
            binding.yearInput.setEnabled(true);
        }
        else{
            binding.branchInput.setEnabled(false);
            binding.yearInput.setEnabled(false);
        }

    }
    public void setUpAdapters(){
        branchAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item,branchOptions);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.branchInput.setAdapter(branchAdapter);


        yearAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item,yearOptions);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.yearInput.setAdapter(yearAdapter);
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
    public void updateDataToDatabase(){

        FirebaseUtil.fetchCollegeDetails(currentUserId).set(new College_Details_Model(selectedYear,selectedBranch,selectedCourse))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        showProgressBar(false);
                        if(task.isSuccessful()){
                            Call_Method.showToast(requireContext(),"Updated successfully");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showProgressBar(false);
                        Call_Method.showToast(requireContext(),"Something went wrong ! \n Please try again later");
                    }
                });
    }
    public void setCollegeDetails(){

        FirebaseUtil.fetchCollegeDetails(currentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot != null){
                    showProgressBar(false);
                    College_Details_Model model = documentSnapshot.toObject(College_Details_Model.class);
                         if(model != null && model.getBranch()!= null){
                             setBranch(model.getBranch());
                         }
                         else {
                             setBranch("");
                         }
                        if(model != null && model.getYear() != null)
                        setYear(model.getYear());
                        else
                            setYear("");
                }
                else
                    Call_Method.showToast(requireContext(),"No college details found");

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Call_Method.showToast(requireContext(),"Something went wrong");
                    }
                });
    }
    public void setBranch(String branch){
        if(branch.isEmpty())
            return ;
        for(int i = 0;i<branchOptions.length;i++){
            if(branchOptions[i].equals(branch)){
                binding.branchInput.setSelection(i);
                return;
            }
        }
    }
    public void setYear(String year){
        if(year.isEmpty())
            return;
        for(int i = 0;i<yearOptions.length;i++){
            if(yearOptions[i].equals(year)){
                binding.yearInput.setSelection(i);
                return;
            }
        }
    }
}