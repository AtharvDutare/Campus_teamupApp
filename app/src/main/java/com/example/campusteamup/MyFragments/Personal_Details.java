package com.example.campusteamup.MyFragments;

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


public class Personal_Details extends Fragment {

    FragmentPersonalDetailsBinding binding ;
    String []genderOptions = {"Male","Female"};
    ArrayAdapter<String> genderAdapter ;
    private String selectedGender ,name , dateOfBirth;

    public Personal_Details() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPersonalDetailsBinding.inflate(inflater);

        setGenderOptions();
        notAllowToInput();
        showProgressBar(true);

        setPersonalDetails();


        binding.genderInput.setEnabled(false);
        binding.genderInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGender = genderOptions[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGender = null;
            }
        });




        binding.editPersonalDetailsBtn.setOnClickListener(v->{

            binding.nameInput.requestFocus();
            allowUserToEdit();
            binding.dobInput.setOnClickListener(v1->showDatePickerDialog());

            binding.genderInput.setEnabled(true);




        });

        // user click on update

        binding.updatePersonalDetailsBtn.setOnClickListener(v->{

             name = binding.nameInput.getText().toString().trim();

            notAllowToInput();
            binding.editPersonalDetailsBtn.setVisibility(View.VISIBLE);
            binding.updatePersonalDetailsBtn.setVisibility(View.INVISIBLE);
            binding.genderInput.setEnabled(false);

            setLightColor();
            showProgressBar(true);

            updateDataToDatabase(name , selectedGender , dateOfBirth);

        });
        return binding.getRoot();
    }
    public void allowUserToEdit(){
        allowToInput();
        binding.updatePersonalDetailsBtn.setVisibility(View.VISIBLE);
        binding.editPersonalDetailsBtn.setVisibility(View.INVISIBLE);
    }

    public void allowToInput(){
        binding.nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        binding.dobInput.setInputType(InputType.TYPE_CLASS_TEXT);
    }
    public void notAllowToInput(){
        binding.nameInput.setInputType(InputType.TYPE_NULL);
        binding.dobInput.setInputType(InputType.TYPE_NULL);
    }
    public void setGenderOptions(){
        genderAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item,genderOptions);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.genderInput.setAdapter(genderAdapter);
    }
    public void showDatePickerDialog(){
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;

                    binding.dobInput.setText(date);
                    dateOfBirth = binding.dobInput.getText().toString();
                }, year, month, day);
        datePickerDialog.show();
    }
    public void setLightColor(){
        binding.nameInput.setTextColor(ContextCompat.getColor(requireContext(),R.color.lightBlack));
        binding.dobInput.setTextColor(ContextCompat.getColor(requireContext(),R.color.lightBlack));
    }
    public void updateDataToDatabase(String name , String gender , String dob){
        FirebaseUtil.fetchPersonalDetails().set(new Personal_Details_Model(name, gender, dob))
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
                        Call_Method.showToast(requireContext(),"Something went wrong ! \n Please try again later");
                    }
                });
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
    public void setPersonalDetails(){
        FirebaseUtil.fetchPersonalDetails().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Personal_Details_Model personalDetailsModel = documentSnapshot.toObject(Personal_Details_Model.class);
                showProgressBar(false);
                if (personalDetailsModel != null){
                    dateOfBirth = personalDetailsModel.getDob();
                    binding.dobInput.setText(personalDetailsModel.getDob());
                    binding.nameInput.setText(personalDetailsModel.getName());
                    setGenderFromDatabase(personalDetailsModel.getGender());
                    setLightColor();
                    showProgressBar(false);
                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showProgressBar(false);
                        Call_Method.showToast(requireContext(),"Failed to load personal details");
                    }
                });
    }
    private void setGenderFromDatabase(String gender) {
        for (int i = 0; i < genderOptions.length; i++) {
            if (genderOptions[i].equals(gender)) {
                binding.genderInput.setSelection(i);
                break;
            }
        }
    }
}