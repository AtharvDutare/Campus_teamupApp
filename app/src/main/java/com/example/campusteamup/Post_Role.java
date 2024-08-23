package com.example.campusteamup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyCallBacks.CountRoles;
import com.example.campusteamup.MyCallBacks.UserSignUpDetailsCallback;
import com.example.campusteamup.MyModels.UserRoleDetails;
import com.example.campusteamup.MyModels.UserSignUpDetails;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.MyViewModel.RoleViewModel;
import com.example.campusteamup.databinding.FragmentPostRoleBinding;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class Post_Role extends BottomSheetDialogFragment {
    String[] listOfRole = {"Android App Developer", "IOS App Developer", "Flutter Developer", "Web Frontend", "Web Backend", "UI/UX Designer"};
    ArrayAdapter<String> roleListAdapter;
    FragmentPostRoleBinding fragmentPostRoleBinding;
    private String selectedRole;
    private RoleViewModel roleViewModel;
    int totalRoles = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentPostRoleBinding = FragmentPostRoleBinding.inflate(inflater);

        setUpRoleSpinner(fragmentPostRoleBinding.getRoot());
        showProgressBar(false);

        roleViewModel = new ViewModelProvider(requireActivity()).get(RoleViewModel.class);

        fragmentPostRoleBinding.roleListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = listOfRole[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRole = null;
            }
        });

        fragmentPostRoleBinding.postRoleButton.setOnClickListener(v -> {
            handlePosting();
        });

        return fragmentPostRoleBinding.getRoot();
    }
    public void handlePosting()
    {
        String linkedInUrl = fragmentPostRoleBinding.linkedInUrl.getText().toString();
        String codingUrl = fragmentPostRoleBinding.codingUrl.getText().toString();

        if (areUrlValid(linkedInUrl, codingUrl) && isRoleSelected(selectedRole)) {
            DocumentReference documentReference = FirebaseUtil.currentUserDetails();
            Log.d("PostRole", "Document Reference: " + documentReference.getPath());
            showProgressBar(true);
            fetchUserSignUpDetailsAndPostRole(documentReference, linkedInUrl, codingUrl, userSignUpDetails -> {
                if (userSignUpDetails != null) {
                    UserRoleDetails userRoleDetails = new UserRoleDetails(selectedRole, linkedInUrl, codingUrl, FirebaseUtil.currentUserUid(), userSignUpDetails);

                        checkTotalRoles(new CountRoles() {
                            @Override
                            public void totalRoles(int count) {
                                if(count >= 2){
                                    Call_Method.showToast(requireContext() , "You can only post 2 Roles");
                                    showProgressBar(false);
                                }

                                else
                                {
                                    uploadRoleData(userRoleDetails);
                                }

                            }

                            @Override
                            public void countVacancy(int count) {
                                // nothing to do
                            }
                        });



                } else {
                    showProgressBar(false);
                    Call_Method.showToast(requireContext(), "Failed to fetch user details");
                }
            });
        } else if (!isRoleSelected(selectedRole)) {
            fragmentPostRoleBinding.roleListSpinner.requestFocus();
        }
    }
    public void checkTotalRoles(CountRoles countRoles){

        FirebaseUtil.allRolesPostedByUser().get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            int count = 0;
                            for(DocumentSnapshot snapshot : task.getResult())
                                count++;

                           countRoles.totalRoles(count);
                        }
                    }
                });
    }
    public void setUpRoleSpinner(View view) {
        Spinner roleListSpinner = view.findViewById(R.id.roleListSpinner);
        roleListAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, listOfRole);
        roleListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleListSpinner.setAdapter(roleListAdapter);
    }

    public boolean isRoleSelected(String role) {
        return role != null;
    }

    public boolean areUrlValid(String linkedIn, String coding) {
        boolean isValid = linkedIn != null && !linkedIn.trim().isEmpty() && Patterns.WEB_URL.matcher(linkedIn).matches();
        if (!isValid) {
            fragmentPostRoleBinding.linkedInUrl.requestFocus();
            fragmentPostRoleBinding.linkedInUrl.setError("Enter valid URL");
            return false;
        }
        return true;
    }

    public void fetchUserSignUpDetailsAndPostRole(DocumentReference documentReference, String linkedInUrl, String codingUrl, UserSignUpDetailsCallback callback) {
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            UserSignUpDetails userSignUpDetails = documentSnapshot.toObject(UserSignUpDetails.class);
            callback.onCallback(userSignUpDetails);
        }).addOnFailureListener(e -> {
            Log.e("PostRole Error", e.getMessage());
            Call_Method.showToast(requireContext(), "Something went wrong");
            callback.onCallback(null); // Return null in case of failure
        });
    }

    public void uploadRoleData(UserRoleDetails details) {
        FirebaseUtil.sendRoleDetails().add(details)
                .addOnCompleteListener(task -> {
                    showProgressBar(false);
                    if (task.isSuccessful()) {
                        Call_Method.showToast(requireContext(), "Role posted successfully");
                        roleViewModel.setRolePosted(true);
                        dismiss();
                    } else {
                        Call_Method.showToast(requireContext(), "Something went wrong");
                    }
                })
                .addOnFailureListener(e -> {
                    showProgressBar(false);
                    Call_Method.showToast(requireContext(), "Something went wrong");
                });
    }

    public void showProgressBar(boolean command) {
        if (command) {
            fragmentPostRoleBinding.postRoleButton.setVisibility(View.GONE);
            fragmentPostRoleBinding.progressBar.setVisibility(View.VISIBLE);
        } else {
            fragmentPostRoleBinding.postRoleButton.setVisibility(View.VISIBLE);
            fragmentPostRoleBinding.progressBar.setVisibility(View.GONE);
        }
    }
}
