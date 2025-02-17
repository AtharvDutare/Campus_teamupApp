package com.example.campusteamup.MyFragments;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyAdapters.RoleListAdapter;
import com.example.campusteamup.MyModels.UserRoleDetails;
import com.example.campusteamup.MyModels.VacancyModel;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.MyViewModel.RoleViewModel;
import com.example.campusteamup.Network_Monitoring;
import com.example.campusteamup.databinding.FragmentHireSectionBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

public class Hire_Section extends Fragment {
    RoleListAdapter roleListAdapter ;
    FragmentHireSectionBinding binding;
    RoleViewModel roleViewModel ;
    Handler handler ;
    Runnable searchRunnable;
    CollectionReference allRoleData;
    public Hire_Section() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHireSectionBinding.inflate(inflater);


             if(Network_Monitoring.isNetworkAvailable(requireContext())){
                allRoleData  = FirebaseUtil.getAllUserRoles();
             }
             else{
                 return binding.getRoot();
             }


            FirestoreRecyclerOptions<UserRoleDetails> option = new FirestoreRecyclerOptions.Builder<UserRoleDetails>()
                    .setQuery(allRoleData, UserRoleDetails.class)
                    .build();


            binding.roleRecyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
            roleListAdapter = new RoleListAdapter(option , requireActivity());
            binding.roleRecyclerView.addItemDecoration(new DividerItemDecoration(binding.getRoot().getContext(), DividerItemDecoration.VERTICAL));
            binding.roleRecyclerView.setAdapter(roleListAdapter);
            roleListAdapter.startListening();



            roleListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    roleListAdapter.startListening();
                    binding.shimmerLayout.stopShimmerAnimation();
                    binding.shimmerLayout.setVisibility(View.GONE);
                    binding.roleRecyclerView.setVisibility(View.VISIBLE);
                }
            });






        //handing search query



        roleViewModel = new ViewModelProvider(requireActivity()).get(RoleViewModel.class);
        roleViewModel.getRolePosted().observe(getViewLifecycleOwner(), posted -> {
            if (posted) {
                Log.d("Hire_Section", "Role posted, notifying adapter.");
                roleListAdapter.notifyDataSetChanged();
                roleViewModel.setRolePosted(false);
            }
        });


        binding.searchRole.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                handler.removeCallbacks(searchRunnable);

                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        performSearch(query);
                    }
                };

                handler.postDelayed(searchRunnable , 1000);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

            Log.d("Hire_Section", "Adapter starts listening.");

        if(Network_Monitoring.isNetworkAvailable(requireContext()) && roleListAdapter != null){
            roleListAdapter.startListening();
        }
        else{
            binding.shimmerLayout.startShimmerAnimation();
            Call_Method.showToast(requireContext() , "No Network Connection");
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        if(Network_Monitoring.isNetworkAvailable(requireContext())&& roleListAdapter != null){
            roleListAdapter.stopListening();
            binding.shimmerLayout.startShimmerAnimation();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        if(Network_Monitoring.isNetworkAvailable(requireContext()) && roleListAdapter != null){
            roleListAdapter.startListening();
            roleListAdapter.notifyDataSetChanged();
            binding.shimmerLayout.startShimmerAnimation();

        }


    }

    public void updateResults(String roleToSearch){
        if(roleToSearch == null || roleToSearch.isEmpty())
            return;
        Query searchQuery =  FirebaseUtil.searchForRoles(roleToSearch);
        FirestoreRecyclerOptions<UserRoleDetails>options = new FirestoreRecyclerOptions.Builder<UserRoleDetails>()
                .setQuery(searchQuery , UserRoleDetails.class)
                .build();
        roleListAdapter.updateOptions(options);
        roleListAdapter.notifyDataSetChanged();
    }

    private void performSearch(String query) {
        updateResults(query);
    }
}