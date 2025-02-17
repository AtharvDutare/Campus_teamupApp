package com.example.campusteamup.MyFragments;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyAdapters.VacancyListAdapter;
import com.example.campusteamup.MyModels.VacancyModel;
import com.example.campusteamup.MyUtil.FirebaseUtil;

import com.example.campusteamup.Network_Monitoring;
import com.example.campusteamup.R;
import com.example.campusteamup.databinding.FragmentApplySectionBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;


public class Apply_Section extends Fragment {


    VacancyListAdapter vacancyListAdapter;

    FragmentApplySectionBinding binding;
    Handler handler;
    Runnable searchRunnable;
    CollectionReference allVacancyData;

    public Apply_Section() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentApplySectionBinding.inflate(inflater);

        if(Network_Monitoring.isNetworkAvailable(requireContext())){
            allVacancyData  = FirebaseUtil.getAllUserRoles();
        }
        else{
            return binding.getRoot();
        }

         allVacancyData = FirebaseUtil.allVacancy();

        FirestoreRecyclerOptions<VacancyModel> options = new FirestoreRecyclerOptions.Builder<VacancyModel>()
                .setQuery(allVacancyData, VacancyModel.class)
                .build();
        handler = new Handler();
        binding.vacancyListRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        vacancyListAdapter = new VacancyListAdapter(options, requireContext());
        binding.vacancyListRecyclerView.addItemDecoration(new DividerItemDecoration(binding.getRoot().getContext(), DividerItemDecoration.VERTICAL));
        binding.vacancyListRecyclerView.setAdapter(vacancyListAdapter);
        vacancyListAdapter.startListening();

        handleShimmerEffect();

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

                handler.postDelayed(searchRunnable, 1000);
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

        if(Network_Monitoring.isNetworkAvailable(requireContext()) && vacancyListAdapter != null){
            vacancyListAdapter.startListening();
        }
        else{
            Call_Method.showToast(requireContext() , "No Network Connection");
            binding.shimmerLayout.startShimmerAnimation();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Network_Monitoring.isNetworkAvailable(requireContext()) && vacancyListAdapter != null){
            vacancyListAdapter.startListening();
            vacancyListAdapter.notifyDataSetChanged();
            binding.shimmerLayout.startShimmerAnimation();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(Network_Monitoring.isNetworkAvailable(requireContext())&& vacancyListAdapter != null){
            vacancyListAdapter.stopListening();
            binding.shimmerLayout.startShimmerAnimation();
        }
    }

    public void updateResults(String roleToSearch) {
        if (roleToSearch == null || roleToSearch.isEmpty())
            return;
        Query searchQuery = FirebaseUtil.searchForVacancy(roleToSearch);
        FirestoreRecyclerOptions<VacancyModel> options = new FirestoreRecyclerOptions.Builder<VacancyModel>()
                .setQuery(searchQuery, VacancyModel.class)
                .build();
        vacancyListAdapter.updateOptions(options);
        vacancyListAdapter.notifyDataSetChanged();
    }

    private void performSearch(String query) {
        updateResults(query);
    }

    public void handleShimmerEffect() {
        vacancyListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                        vacancyListAdapter.startListening();
                        binding.shimmerLayout.stopShimmerAnimation();
                        binding.shimmerLayout.setVisibility(View.GONE);
                        binding.vacancyListRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

}