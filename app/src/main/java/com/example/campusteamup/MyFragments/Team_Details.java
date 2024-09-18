package com.example.campusteamup.MyFragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyCallBacks.FindEmail;
import com.example.campusteamup.MyModels.Team_Details_List_Model;
import com.example.campusteamup.MyModels.UserSignUpDetails;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.R;
import com.example.campusteamup.MyModels.Team_Members_Model;
import com.example.campusteamup.databinding.FragmentTeamDetailsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Team_Details extends Fragment {
    FragmentTeamDetailsBinding binding ;
    List<EditText> listOfMemberDetails ;
    String currentUserEmail , currentUserName ;

    Handler handler;
    public Team_Details() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTeamDetailsBinding.inflate(inflater);

        SharedPreferences preferences = requireContext().getSharedPreferences("USER_DETAILS", Context.MODE_PRIVATE);

        currentUserEmail = preferences.getString("userEmail","");
        currentUserName = preferences.getString("userName","");

        listOfMemberDetails = new ArrayList<>();

        // i am adding current user as a member by default

        addCurrentUserAsMember();

        fetchTeamMemberDetails();


        binding.updateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String name , email;

                     List<Team_Members_Model> totalMember = new ArrayList<>();

                     // change logic for this loop
                     int i;
                    for(  i = 0;i<listOfMemberDetails.size();i += 2){
                        name = listOfMemberDetails.get(i).getText().toString();
                        email = listOfMemberDetails.get(i+1).getText().toString();
                        if(isNull(name)){
                            listOfMemberDetails.get(i).setError("Required");
                            break;
                        }
                        if(isNull(email)){
                            listOfMemberDetails.get(i+1).setError("Required");
                            break;
                        }

                        totalMember.add(new Team_Members_Model(name , email));
                    }
                     // if i reached end means that no null or empty data found
                    if(i == listOfMemberDetails.size())
                    sendAllMemberDetailsToDatabase(totalMember);
                    else return;
            }
        });

        binding.addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.removeMember.setVisibility(View.VISIBLE); // user have member to delete

                addMember();

            }
        });
        binding.removeMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.addMember.setVisibility(View.VISIBLE);

                int childCount = binding.memberList.getChildCount();


                Log.d("Team","Total child "+ childCount);

                if(childCount == 2){
                    binding.removeMember.setVisibility(View.GONE);
                }

                if(childCount > 0){
                    binding.memberList.removeViewAt(childCount-1);
                    listOfMemberDetails.remove(listOfMemberDetails.size()-1);
                    listOfMemberDetails.remove(listOfMemberDetails.size()-1);

                    if(listOfMemberDetails.size() == 1){
                        Log.d("Team","size is 1 so add member visible and remove member gone");
                        binding.addMember.setVisibility(View.VISIBLE);
                        binding.removeMember.setVisibility(View.GONE);
                    }


                }
                else{
                    Call_Method.showToast(requireContext() , "Please Add Member");
                }
            }
        });

        return binding.getRoot();
    }

    public void addMember(){
        binding.updateDetails.setVisibility(View.VISIBLE);

        binding.removeMember.setVisibility(View.VISIBLE);

        View view = getLayoutInflater().inflate(R.layout.add_member,null , false);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0,10,0,10);
        view.setLayoutParams(layoutParams);

        EditText memberName = view.findViewById(R.id.memberName);
        EditText memberEmail = view.findViewById(R.id.memberEmail);

        listOfMemberDetails.add(memberName);
        listOfMemberDetails.add(memberEmail);

        //check only maximum of 8 member can be in a team
         if(listOfMemberDetails.size() == 16)
             binding.addMember.setVisibility(View.GONE);

        binding.memberList.addView(view);
    }
    public void sendAllMemberDetailsToDatabase(List<Team_Members_Model> totalMember){
        Team_Details_List_Model listModel = new Team_Details_List_Model(totalMember);
        FirebaseUtil.uploadTeamDetails().set(listModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      if(task.isSuccessful()) {
                          Call_Method.showToast(requireContext(),"Updated Successfully");
                      }
                    }
                });
    }

    public boolean isNull(String data){
        return data == null || data.trim().isEmpty();
    }
    public void fetchTeamMemberDetails(){
        //  if user is already in team showing the team details that user is in



                    FirebaseUtil.findTeamDetails().get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        QuerySnapshot querySnapshot = task.getResult();
                                        boolean found = false;
                                        if(querySnapshot == null || querySnapshot.isEmpty()){
                                            Log.d("Null","Query is null");
                                            return;
                                        }
                                        List<Team_Members_Model>totalMember = new ArrayList<>();
                                        for(DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()){
                                            Team_Details_List_Model listModel = documentSnapshot.toObject(Team_Details_List_Model.class);
                                            if(listModel != null){
                                                totalMember = listModel.getTotalMember();
                                                if(totalMember != null){
                                                    for(Team_Members_Model member : totalMember){
                                                        if(member.getEmail().equals(currentUserEmail)){
                                                            found = true;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                            if (found)break;
                                        }
                                        binding.progressBar.setVisibility(View.INVISIBLE);
                                        if(found)
                                        {
                                            Log.d("AddTeamDetails","Member Details Found");
                                           showTeamDetailsOfCurrentUser(totalMember);
                                        }
                                        else{
                                            Log.d("AddTeamDetails","No Member Details Found");
                                        }
                                    }
                                }
                            });
                }



    public void showTeamDetailsOfCurrentUser(List<Team_Members_Model>totalMember){

//        binding.memberList.removeAllViews();

        for (Team_Members_Model member : totalMember){
            Log.d("AddTeamDetails","Member fetched");


            if (member.getEmail().equals(currentUserEmail)) {
                continue;
            }
            addMember();
            listOfMemberDetails.get(listOfMemberDetails.size()-1).setText(member.getEmail());
            listOfMemberDetails.get(listOfMemberDetails.size()-2).setText(member.getName());
        }

    }
    public void addCurrentUserAsMember(){
        addMember();
        listOfMemberDetails.get(0).setText("You");
        listOfMemberDetails.get(1).setText(currentUserEmail);
        listOfMemberDetails.get(0).setInputType(InputType.TYPE_NULL);
        listOfMemberDetails.get(1).setInputType(InputType.TYPE_NULL);
        binding.removeMember.setVisibility(View.GONE);
    }

}