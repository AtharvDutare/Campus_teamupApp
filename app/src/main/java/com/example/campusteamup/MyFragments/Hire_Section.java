package com.example.campusteamup.MyFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyAdapters.RoleListAdapter;
import com.example.campusteamup.MyModels.UserRoleDetails;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.MyViewModel.RoleViewModel;
import com.example.campusteamup.R;
import com.example.campusteamup.databinding.FragmentHireSectionBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;

public class Hire_Section extends Fragment {
    RoleListAdapter roleListAdapter ;
    FragmentHireSectionBinding binding;
    RoleViewModel roleViewModel ;
    public Hire_Section() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHireSectionBinding.inflate(inflater);
        CollectionReference allRoleData = FirebaseUtil.getAllUserRoles();

        FirestoreRecyclerOptions<UserRoleDetails> option = new FirestoreRecyclerOptions.Builder<UserRoleDetails>()
                .setQuery(allRoleData, UserRoleDetails.class)
                .build();

        binding.roleRecyclerView.setLayoutManager(new LinearLayoutManager(binding.searchByName.getContext()));
        roleListAdapter = new RoleListAdapter(option , requireActivity());
        binding.roleRecyclerView.addItemDecoration(new DividerItemDecoration(binding.searchByName.getContext(), DividerItemDecoration.VERTICAL));
        binding.roleRecyclerView.setAdapter(roleListAdapter);

        roleViewModel = new ViewModelProvider(requireActivity()).get(RoleViewModel.class);
        roleViewModel.getRolePosted().observe(getViewLifecycleOwner(), posted -> {
            if (posted) {
                Log.d("Hire_Section", "Role posted, notifying adapter.");
                roleListAdapter.notifyDataSetChanged();
                roleViewModel.setRolePosted(false);
            }
        });

        Log.d("Hire_Section", "onCreateView complete.");
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

            Log.d("Hire_Section", "Adapter starts listening.");
            roleListAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();

            Log.d("Hire_Section", "Adapter stops listening.");
            roleListAdapter.stopListening();

    }

    @Override
    public void onResume() {
        super.onResume();
        roleListAdapter.startListening();
        roleListAdapter.notifyDataSetChanged();
    }
}