package com.example.campusteamup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;


import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyFragments.Apply_Section;
import com.example.campusteamup.MyFragments.Hire_Section;
import com.example.campusteamup.MyFragments.Post_Vacancy;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.MyViewModel.RoleViewModel;

import com.example.campusteamup.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class MainActivity extends AppCompatActivity  {
    ActivityMainBinding activityMainBinding ;

    Spinner roleListSpinner;
    FragmentManager fragmentManager;
   private RoleViewModel roleViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         activityMainBinding  = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        roleViewModel = new ViewModelProvider(this).get(RoleViewModel.class);


        setUserImageToProfileBtn();


        Call_Method.lightActionBar(getWindow());  // making actionbar light so that date , battery becomes visible
        activityMainBinding.addRoleToGetHired.setVisibility(View.VISIBLE);
         fragmentManager = getSupportFragmentManager();       // default fragment for HOME_ACTIVITY
         fragmentManager.beginTransaction()
                .replace(R.id.mainFrameLayout,new Apply_Section())
                .commit();



        activityMainBinding.bottomNavLayout.setSelectedItemId(R.id.apply);

        activityMainBinding.bottomNavLayout.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.hire){
                activityMainBinding.addRoleToGetHired.setVisibility(View.VISIBLE);
                activityMainBinding.postRoleToFindTeamMember.setVisibility(View.GONE);

                fragmentManager.beginTransaction()
                        .replace(R.id.mainFrameLayout,new Hire_Section())
                        .commit();
                return true;
            }
            else if (id == R.id.apply){
                activityMainBinding.addRoleToGetHired.setVisibility(View.GONE);
                activityMainBinding.postRoleToFindTeamMember.setVisibility(View.VISIBLE);

                fragmentManager.beginTransaction()
                        .replace(R.id.mainFrameLayout,new Apply_Section())
                        .commit();
                return true;
            }
            else if(id == R.id.projects){
                activityMainBinding.addRoleToGetHired.setVisibility(View.GONE);
                activityMainBinding.postRoleToFindTeamMember.setVisibility(View.GONE);

                fragmentManager.beginTransaction()
                        .replace(R.id.mainFrameLayout,new Projects())
                        .commit();
                return true;
            }
            return false;
        });



        activityMainBinding.profileButton.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this,User_Profile.class));
        });



        activityMainBinding.addRoleToGetHired.setOnClickListener(v->{
            Post_Role postRoleFragment = new Post_Role();
            postRoleFragment.show(getSupportFragmentManager(),postRoleFragment.getTag());

        });

        activityMainBinding.postRoleToFindTeamMember.setOnClickListener(v -> {
            Post_Vacancy postVacancy = new Post_Vacancy();
            postVacancy.show(getSupportFragmentManager(),postVacancy.getTag());
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.bottom_nav_view, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    public RoleViewModel get(){
        return roleViewModel;
    }
    public  void setUserImageToProfileBtn(){
        try{


        FirebaseUtil.databaseUserImages().get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot snapshot = task.getResult();
                            if(snapshot != null && snapshot.exists()){
                                String imageUri = snapshot.getString("imageUri");
                                if(imageUri != null && !imageUri.isEmpty())
                                    loadImage(imageUri);
                                else {
                                    activityMainBinding.profileButton.setImageResource(R.drawable.profile_icon);
                                }
                            }
                            else{
                                activityMainBinding.profileButton.setImageResource(R.drawable.profile_icon);
                            }
                        }
                    }
                });
        }
        catch (Exception e){
        }
    }
    public void loadImage(String imageUri){
        Glide.with(this).load(imageUri).into(activityMainBinding.profileButton);
    }

}