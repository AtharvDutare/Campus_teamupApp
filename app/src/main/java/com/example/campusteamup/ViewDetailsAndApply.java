package com.example.campusteamup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyCallBacks.FindEmail;
import com.example.campusteamup.MyModels.Request_Role_Model;
import com.example.campusteamup.MyModels.Team_Details_List_Model;
import com.example.campusteamup.MyModels.Team_Members_Model;
import com.example.campusteamup.MyModels.UserSignUpDetails;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.databinding.ActivityViewDetailsAndApplyBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ViewDetailsAndApply extends AppCompatActivity {

    ActivityViewDetailsAndApplyBinding binding;
    String userPostedId ;
    List<ImageView> imageListOfMember;

    Map<String , ImageView>allEmailAndImage;
    boolean isCurrentMemeberIsInTeam = false;
    String requestedTo , requestedBy, requestedByName ,requestedByImage , requestedByEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewDetailsAndApplyBinding.inflate(getLayoutInflater());


        imageListOfMember = new ArrayList<>();

        allEmailAndImage = new HashMap<>();

        requestedTo = getIntent().getStringExtra("postedBy");

        SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", Context.MODE_PRIVATE);

        requestedBy = sharedPreferences.getString("userId","");
        requestedByName = sharedPreferences.getString("userName","");
        requestedByImage = sharedPreferences.getString("userImage","");
        requestedByEmail = sharedPreferences.getString("userEmail","");



        if(Network_Monitoring.isNetworkAvailable(this)){
            checkIfUserAlreadyApplied(requestedTo , requestedBy);
        }
        else{
            binding.progressBar.setVisibility(View.VISIBLE);
            Call_Method.showToast(this , "No Network Connection");
        }


        Dialog requestDialog = new Dialog(this);
        requestDialog.setContentView(R.layout.request_role);
        TextView sendRequest = requestDialog.findViewById(R.id.sendRequest);
        TextView doNotSendRequest = requestDialog.findViewById(R.id.doNotSendRequest);

        sendRequest.setOnClickListener(v -> {
            requestDialog.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

            if(Network_Monitoring.isNetworkAvailable(ViewDetailsAndApply.this))
            saveRequestToFirebase(requestDialog);
            else {
                Call_Method.showToast(ViewDetailsAndApply.this , "No Network Connection");
                showProgressBar(false , requestDialog);
            }
        });
        doNotSendRequest.setOnClickListener(v -> requestDialog.dismiss());

        setContentView(binding.getRoot());
        setToolbarAndActionBar();
        if(Network_Monitoring.isNetworkAvailable(this)){
            binding.progressBar.setVisibility(View.VISIBLE);
            findUserIdWhoPosted();
            fetchTeamDetailsByEmailId();
        }
        else {
            Call_Method.showToast(this , "No Network Connection");
        }

        binding.requestToJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    requestDialog.show();
            }
        });

    }


    public void setToolbarAndActionBar() {
        Call_Method.lightActionBar(getWindow());
        setSupportActionBar(binding.toolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }
    public void findUserIdWhoPosted(){
        if (getIntent() != null){
            userPostedId = getIntent().getStringExtra("postedBy");
        }
    }
    public void findUserEmailWhoPosted(FindEmail callback){
        FirebaseUtil.findUserWithUserId(userPostedId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document != null && document.exists()){
                                UserSignUpDetails details = document.toObject(UserSignUpDetails.class);
                                if(details != null){
                                    callback.onEmailReceived(details.getEmail());
                                }
                                else {
                                    callback.onEmailReceived(null);
                                    Call_Method.showToast(ViewDetailsAndApply.this , "User Who Posted Role Doesn't Exists");
                                }
                            }
                        }
                    }
                });
    }
    public void fetchTeamDetailsByEmailId(){

        findUserEmailWhoPosted(new FindEmail() {
            @Override
            public void onEmailReceived(String email) {
                if(email != null){
                    FirebaseUtil.findTeamDetails().get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if(querySnapshot != null && !querySnapshot.isEmpty()){
                                            List<Team_Members_Model>totalMember = new ArrayList<>();
                                            boolean found = false;
                                            for(DocumentSnapshot document : querySnapshot.getDocuments()){
                                                Team_Details_List_Model listModel = document.toObject(Team_Details_List_Model.class);
                                                if(listModel != null){
                                                    totalMember = listModel.getTotalMember();
                                                    if(totalMember != null){
                                                        for(Team_Members_Model model : totalMember){
                                                            if(model.getEmail().equals(email)){
                                                                found = true;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                                if(found)break;
                                            }
                                            if(found){
                                                addAllMembers(totalMember);
                                            }
                                            else{
                                                Call_Method.showToast(ViewDetailsAndApply.this , "No Team Details Found");
                                            }
                                        }
                                    }
                                }
                            });
                }
            }
        });

        binding.progressBar.setVisibility(View.GONE);

    }
    public void addAllMembers(List<Team_Members_Model>model){


        for(Team_Members_Model member : model){
            addMember(member.getName() , member.getEmail());
        }
        if(allEmailAndImage.size() > 0)
            showTeamMemberImage();
        else
            Call_Method.showToast(ViewDetailsAndApply.this , "No Team Details Found");

    }
    public void addMember(String name , String email){
        View view = LayoutInflater.from(ViewDetailsAndApply.this).inflate(R.layout.show_member , null , false);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0,10,0,10);
        view.setLayoutParams(layoutParams);
        TextView textView = view.findViewById(R.id.memberName);
        textView.setText(name);
        ImageView imageView = view.findViewById(R.id.teamMemberProfile);

        // if user is already in team
        if(email.equals(requestedByEmail)){
            binding.requestToJoin.setVisibility(View.GONE);
            binding.message.setText("Your Team");
        }


        allEmailAndImage.put(email , imageView);

        binding.memberList.addView(view);
    }
    public void showTeamMemberImage(){

        FirebaseUtil.findImageByEmail()
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if(querySnapshot != null && !querySnapshot.isEmpty()){
                                for(DocumentSnapshot snapshot : querySnapshot.getDocuments()){
                                    String email =  snapshot.getString("email");
                                    if(email != null && allEmailAndImage.containsKey(email)){
                                        String imageUri = snapshot.getString("imageUri");
                                        if(imageUri != null){
                                            Glide.with(ViewDetailsAndApply.this).load(imageUri).into(allEmailAndImage.get(email));
                                        }
                                        else {
                                            allEmailAndImage.get(email).setImageResource(R.drawable.profile_icon);
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
    }
    public void saveRequestToFirebase(Dialog requestDialog){


        Request_Role_Model requestRoleModel = new Request_Role_Model(requestedBy ,  requestedTo , requestedByName , requestedByImage, requestedByEmail);

        FirebaseUtil.saveRequest(requestedTo).add(requestRoleModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                      if(task.isSuccessful()){
                          showProgressBar(false , requestDialog);
                          sendNotification(requestedTo);
                          Call_Method.showToast(ViewDetailsAndApply.this , "Request Sent");
                          binding.requestToJoin.setVisibility(View.GONE);
                      }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showProgressBar(false , requestDialog);
                        Call_Method.showToast(ViewDetailsAndApply.this , "Something Went Wrong \n Please Try Again Later");
                    }
                });
    }
    public void showProgressBar(boolean command  ,Dialog requestDialog){
        ProgressBar progressBar = requestDialog.findViewById(R.id.progressBar);

        if(command){
            progressBar.setVisibility(View.VISIBLE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            requestDialog.dismiss();
        }
    }
    public void checkIfUserAlreadyApplied(String postedBy  , String currentUser){
        binding.progressBar.setVisibility(View.VISIBLE);
        // first fetch all the applicant who send request to this vacancy

        FirebaseUtil.allRequestToVacancy(postedBy).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
              if(task.isSuccessful()){
                  QuerySnapshot querySnapshot = task.getResult();

                  if(querySnapshot != null){
                      for(DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()){

                          Request_Role_Model model = documentSnapshot.toObject(Request_Role_Model.class);
                          if (model != null && model.getRequestedTo().equals(currentUser)){
                              binding.requestToJoin.setVisibility(View.GONE);
                              binding.message.setText("Your Team");
                          }
                          else if(model != null && model.getRequestedBy().equals(currentUser)){
                              // only one time request can be sent
                              binding.requestToJoin.setVisibility(View.GONE);
                              binding.message.setText("You have already applied");
                          }
                      }
                  }

              }
            }
        });
    }
    public void sendNotification(String personWhoPosted){
        fetchFCMWhoPostedRole(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()){
                    String otherUserFCM = task.getResult();
                    NotificationHelper.sendNotification(otherUserFCM , requestedByName + " request to Join Your Team",requestedByName , requestedBy , requestedByImage , ViewDetailsAndApply.this);
                }
            }
        },personWhoPosted);
    }
    public void fetchFCMWhoPostedRole(OnCompleteListener<String>listener , String personWhoPosted){
        FirebaseUtil.saveFCM(personWhoPosted)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();

                        if (task.isSuccessful() && task.getResult() != null){
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot != null){
                                String otherUserFCM = snapshot.getString("fcm_TOKEN");
                                taskCompletionSource.setResult(otherUserFCM);

                                Log.d("FCM","Other user FCM fetched " + otherUserFCM);
                            }
                            else {
                                Log.d("FCM","FCM snapshot is null");
                                taskCompletionSource.setException(new Exception("FCM snapsot is null"));
                            }
                        }
                        else {
                            Log.d("FCM","Task fail to get other user FCM");
                            taskCompletionSource.setException(new Exception("Task fail to get other user FCM"));
                        }
                        listener.onComplete(taskCompletionSource.getTask());
                    }
                });
    }
}