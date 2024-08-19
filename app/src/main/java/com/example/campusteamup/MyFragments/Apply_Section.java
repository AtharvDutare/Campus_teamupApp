package com.example.campusteamup.MyFragments;

import android.app.DownloadManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.campusteamup.MyAdapters.VacancyListAdapter;
import com.example.campusteamup.MyModels.UserRoleDetails;
import com.example.campusteamup.MyModels.VacancyModel;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.R;
import com.example.campusteamup.databinding.FragmentApplySectionBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.Query;

public class Apply_Section extends Fragment {


                VacancyListAdapter vacancyListAdapter;

                FragmentApplySectionBinding binding;

                  public Apply_Section() {

                  }


                 @Override
                  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

                    binding = FragmentApplySectionBinding.inflate(inflater);
                    CollectionReference allVacancyData = FirebaseUtil.allVacancy();

                    FirestoreRecyclerOptions<VacancyModel>options = new FirestoreRecyclerOptions.Builder<VacancyModel>()
                            .setQuery(allVacancyData , VacancyModel.class)
                                    .build();

                    binding.vacancyListRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    vacancyListAdapter = new VacancyListAdapter(options , requireContext());
                    binding.vacancyListRecyclerView.addItemDecoration(new DividerItemDecoration(binding.searchLayout.getContext() , DividerItemDecoration.VERTICAL));
                    binding.vacancyListRecyclerView.setAdapter(vacancyListAdapter);
                    return binding.getRoot();
                }

                @Override
                public void onStart() {
                    super.onStart();
                    vacancyListAdapter.startListening();
                }

                @Override
                public void onResume() {
                    super.onResume();
                    vacancyListAdapter.startListening();
                    vacancyListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onStop() {
                    super.onStop();
                    vacancyListAdapter.stopListening();
                }
}