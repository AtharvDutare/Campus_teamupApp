package com.example.campusteamup.MyAdapters;

import android.app.Dialog;
import android.content.Context;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyModels.UserRoleDetails;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.R;
import com.example.campusteamup.View_Profile;
import com.example.campusteamup.databinding.RoleSingleRowBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import org.w3c.dom.Document;


public class RoleListAdapter extends FirestoreRecyclerAdapter<UserRoleDetails , RoleListAdapter.RoleListViewHolder> {
    Dialog deleteDialog ;
    Context context;
    String imageOfUser  ;
    public RoleListAdapter(@NonNull FirestoreRecyclerOptions<UserRoleDetails> options , Context context) {
        super(options);
        this.context = context;
        deleteDialog = new Dialog(context);
        deleteDialog.setContentView(R.layout.delete_dialog);
    }

    @Override
    protected void onBindViewHolder(@NonNull RoleListViewHolder holder, int position, @NonNull UserRoleDetails model) {

            String currentUserUid = FirebaseUtil.currentUserUid();
            if (currentUserUid != null && currentUserUid.equals(model.getUserId())) {
                holder.binding.userName.setText("You");
                holder.binding.deleteRole.setVisibility(View.VISIBLE);
            } else if (model.getUserId() != null) {
                holder.binding.deleteRole.setVisibility(View.GONE);
                holder.binding.userName.setText(model.getUserName());
            }
            // setting role
            holder.binding.userRole.setText("Role : " + model.getRoleName());

            //set image of user

            setImageOfUser(holder.binding.imageOfUser ,model.getUserImage() , holder);

            // deleting role by user
            holder.binding.deleteRole.setOnClickListener(v -> {
                deleteDialog.show();
                handleDialogBtnClick( position );
            });

            // when no data to show because of low network ,  prevent user from view others profile
            if(model.getUserId() == null){
                holder.binding.viewProfile.setVisibility(View.GONE);
            }

            // view the profile of user
            holder.binding.viewProfile.setOnClickListener(v->{
                Intent viewProfile = new Intent(context , View_Profile.class);
                viewProfile.putExtra("userId", model.getUserId());
                viewProfile.putExtra("linkedInUrl",model.getLinkedInUrl());

                viewProfile.putExtra("userImage",imageOfUser);
                viewProfile.putExtra("userName",model.getUserName());

                context.startActivity(viewProfile);
            });

    }

    @NonNull
    @Override
    public RoleListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RoleSingleRowBinding binding = RoleSingleRowBinding.inflate(inflater,parent,false);
        return new RoleListViewHolder(binding);
    }

    public class RoleListViewHolder extends RecyclerView.ViewHolder{
        RoleSingleRowBinding binding ;
        RoleListViewHolder(@NonNull  RoleSingleRowBinding binding){
            super(binding.getRoot());
            this.binding = binding;

        }
    }
    public void handleDialogBtnClick( int position){
        TextView yes = deleteDialog.findViewById(R.id.deleteYes);

        TextView no = deleteDialog.findViewById(R.id.deleteNo);
        if(position < 0 || position >= getItemCount()){
            deleteDialog.dismiss();
            return;
        }
        yes.setOnClickListener(v->{
            getSnapshots().getSnapshot(position).getReference().delete()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            // no need to notify firebase recylerview will take care of it
                            Call_Method.showToast(context , "Role deleted");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Call_Method.showToast(context , "Something went wrong");
                    });

            deleteDialog.dismiss();
        });
        no.setOnClickListener(v->{
            deleteDialog.dismiss();
        });


    }
    public void setImageOfUser(ImageView imageView , String userImage , RoleListViewHolder holder){
        if(userImage.equals("noImage")){
            imageView.setImageResource(R.drawable.profile_icon);
        }
        else{
            Glide.with(context).load(userImage).into(imageView);
        }
    }

}
