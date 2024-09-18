package com.example.campusteamup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyCallBacks.FindEmail;
import com.example.campusteamup.MyModels.Team_Details_List_Model;
import com.example.campusteamup.MyModels.Team_Members_Model;
import com.example.campusteamup.MyModels.UserSignUpDetails;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.databinding.ActivityViewDetailsAndApplyBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewDetailsAndApplyBinding.inflate(getLayoutInflater());

        imageListOfMember = new ArrayList<>();

        allEmailAndImage = new HashMap<>();


        setContentView(binding.getRoot());
        setToolbarAndActionBar();
        findUserIdWhoPosted();
        fetchTeamDetailsByEmailId();
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

}