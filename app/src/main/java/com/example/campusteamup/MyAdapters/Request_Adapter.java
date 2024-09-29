package com.example.campusteamup.MyAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyModels.Request_Role_Model;
import com.example.campusteamup.MyModels.Team_Details_List_Model;
import com.example.campusteamup.MyModels.Team_Members_Model;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.Network_Monitoring;
import com.example.campusteamup.R;
import com.example.campusteamup.databinding.AcceptRejectRequestBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.CacheRequest;
import java.util.ArrayList;
import java.util.List;

public class Request_Adapter extends FirestoreRecyclerAdapter<Request_Role_Model , Request_Adapter.RequestViewHolder> {

    Context context;
    public Request_Adapter(@NonNull FirestoreRecyclerOptions<Request_Role_Model> options  , Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull Request_Role_Model model) {
        holder.binding.title.setText(model.getUserName() +" wants to Join your Team");

        if(model.getUserImage() != null)
        Glide.with(context).load(model.getUserImage()).into(holder.binding.imageOfUser);
        else holder.binding.imageOfUser.setImageResource(R.drawable.profile_icon);

        holder.binding.acceptBtn.setOnClickListener(v -> {
            holder.binding.progressBar.setVisibility(View.VISIBLE);
            if(Network_Monitoring.isNetworkAvailable(context)){
                checkTeamSize(new OnCompleteListener<List<Team_Members_Model>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Team_Members_Model>> task) {
                        if(task.isSuccessful()){
                            List<Team_Members_Model>totalMember = task.getResult();
                            if(totalMember.size() < 8){
                                if(Network_Monitoring.isNetworkAvailable(context)){
                                    addMemberToUserTeam(position  , model.getUserName(), model.getUserEmail() , totalMember);
                                }
                                else{
                                    Call_Method.showToast(context , "No Network Connection");
                                }


                            }
                            else {
                                Call_Method.showToast(context , "Your Team is Full \n Cannot Accept Request");
                            }
                        }
                        holder.binding.progressBar.setVisibility(View.GONE);
                    }
                },model.getRequestedTo());
            }

            else{
                Call_Method.showToast(context , "No Network Connection");
                holder.binding.progressBar.setVisibility(View.GONE);
            }


        });
        holder.binding.rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Network_Monitoring.isNetworkAvailable(context)){
                    getSnapshots().getSnapshot(position).getReference().delete();
                    notifyItemRemoved(position);
                }

                else
                    Call_Method.showToast(context,"No Network Connection");
            }
        });

    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AcceptRejectRequestBinding binding = AcceptRejectRequestBinding.inflate(inflater , null , false);
        return new RequestViewHolder(binding);
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder {

        AcceptRejectRequestBinding binding;
        public RequestViewHolder(@NonNull AcceptRejectRequestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    public void checkTeamSize(OnCompleteListener<List<Team_Members_Model>>listener , String currentUser) {
        FirebaseFirestore.getInstance().collection("teamDetails").document(currentUser)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        List<Team_Members_Model>totalMember = new ArrayList<>();
                        if(task.isSuccessful()){
                            DocumentSnapshot snapshot = task.getResult();
                            Team_Details_List_Model teamDetailsListModel = snapshot.toObject(Team_Details_List_Model.class);
                            if(teamDetailsListModel != null){
                                totalMember = teamDetailsListModel.getTotalMember();
                            }
                        }
                        Log.d("TotalMember","Total Member are : "  + totalMember.size());

                        TaskCompletionSource<List<Team_Members_Model>>taskCompletionSource = new TaskCompletionSource<>();
                        taskCompletionSource.setResult(totalMember);
                        listener.onComplete(taskCompletionSource.getTask());

                    }
                });
    }
    public void addMemberToUserTeam(int position , String userName , String userEmail , List<Team_Members_Model>totalMember ){


        Log.d("TotalMember","Team was "+totalMember.size());
        Team_Members_Model model = new Team_Members_Model(userName ,userEmail);
        totalMember.add(model);

        Team_Details_List_Model teamDetailsListModel = new Team_Details_List_Model(totalMember);

        Log.d("TotalMember","Team is "+totalMember.size());

        FirebaseUtil.uploadTeamDetails().set(teamDetailsListModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Call_Method.showToast(context,userName + " Added to Your Team");
                            // now make sure to delete the request notification
                            if(Network_Monitoring.isNetworkAvailable(context))
                            getSnapshots().getSnapshot(position).getReference().delete();
                            else {
                                Call_Method.showToast(context , "No Network Connection");
                            }
                        }
                    }
                });

    }
}
