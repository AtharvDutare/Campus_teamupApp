package com.example.campusteamup.MyFragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.campusteamup.MyAdapters.Request_Adapter;
import com.example.campusteamup.MyModels.Request_Role_Model;
import com.example.campusteamup.MyModels.Team_Details_List_Model;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.R;
import com.example.campusteamup.databinding.FragmentNotificationsSectionsBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import okhttp3.internal.cache.DiskLruCache;


public class Notifications_Section extends Fragment {
    String currentUserId ;
    Request_Adapter requestAdapter;
    RecyclerView requestRecyclerView;
    FragmentNotificationsSectionsBinding binding;


    public Notifications_Section() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsSectionsBinding.inflate(inflater);

        currentUserId = requireActivity().getSharedPreferences("USER_DETAILS", Context.MODE_PRIVATE).getString("userId","");

        //fetching all the request made to user vacancy post
        fetchAllRequestAndShow();


        return binding.getRoot();
    }
    public void fetchAllRequestAndShow(){
        Query query = FirebaseUtil.allRequestToVacancy(currentUserId);

        showMessageWhenNoRequest(query);
        FirestoreRecyclerOptions<Request_Role_Model>options = new FirestoreRecyclerOptions.Builder<Request_Role_Model>()
                .setQuery(query , Request_Role_Model.class)
                .build();



        requestAdapter = new Request_Adapter(options , requireContext());
        binding.notificationRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.notificationRecyclerView.addItemDecoration(new DividerItemDecoration(binding.getRoot().getContext(), DividerItemDecoration.VERTICAL));
        binding.notificationRecyclerView.setAdapter(requestAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        requestAdapter.startListening();
    }
    public void showMessageWhenNoRequest(Query query){
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value == null || value.isEmpty()){
                    binding.noRequestMessage.setVisibility(View.VISIBLE);
                }
                else {
                    binding.noRequestMessage.setVisibility(View.GONE);
                }
            }
        });
    }
}