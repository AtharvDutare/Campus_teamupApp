package com.example.campusteamup.MyAdapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyModels.VacancyModel;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.R;
import com.example.campusteamup.ViewDetailsAndApply;
import com.example.campusteamup.databinding.VacancySingleRowBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

public class VacancyListAdapter extends FirestoreRecyclerAdapter<VacancyModel ,VacancyListAdapter.VacancyListViewHolder > {

    Context context ;
    Dialog dialog ;
    public VacancyListAdapter(@NonNull FirestoreRecyclerOptions<VacancyModel> options , Context context) {
        super(options);
        this.context = context;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.delete_dialog);
    }

    @Override
    protected void onBindViewHolder(@NonNull VacancyListViewHolder holder, int position, @NonNull VacancyModel model) {

        if(model.getPostedBy().equals(FirebaseUtil.currentUserUid())){
            holder.binding.teamName.setText("Team Name : " + model.getTeamName());
            holder.binding.deleteVacancy.setVisibility(View.VISIBLE);
        }

        else {
            holder.binding.teamName.setText("Team Name : "+ model.getTeamName());
            holder.binding.deleteVacancy.setVisibility(View.GONE);
        }

        holder.binding.roleLookingFor.setText("Looking for  : "+model.getRoleLookingFor());
        holder.binding.hackathonName.setText("Hackathon   : " + model.getHackathonName());

        holder.binding.deleteVacancy.setOnClickListener(v -> {
            dialog.show();
            handleDeleteRole(position);
        });

        holder.binding.apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , ViewDetailsAndApply.class);
                intent.putExtra("postedBy",model.getPostedBy());
                context.startActivity(intent);
            }
        });

    }

    @NonNull
    @Override
    public VacancyListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // here attack to root as true  try
        VacancySingleRowBinding binding = VacancySingleRowBinding.inflate(inflater , parent , false);

        return new VacancyListViewHolder(binding);
    }

    public class VacancyListViewHolder extends RecyclerView.ViewHolder{
        VacancySingleRowBinding binding;
        public VacancyListViewHolder(VacancySingleRowBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    public void handleDeleteRole(int position){
        TextView deleteYes = dialog.findViewById(R.id.deleteYes);
        TextView deleteNo = dialog.findViewById(R.id.deleteNo);
        if(position < 0 || position >= getItemCount()){
            dialog.dismiss();
            return;
        }
        deleteYes.setOnClickListener(v -> {
            getSnapshots().getSnapshot(position).getReference().delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Call_Method.showToast(context , "Deleted Successfully");
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                       Call_Method.showToast(context , "Something went wrong");
                    });
            dialog.dismiss();
        });
        deleteNo.setOnClickListener(v->{
            dialog.dismiss();
        });
    }
}
