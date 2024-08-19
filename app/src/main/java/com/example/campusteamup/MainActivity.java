package com.example.campusteamup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;

import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyFragments.Apply_Section;
import com.example.campusteamup.MyFragments.Hire_Section;
import com.example.campusteamup.MyFragments.Post_Vacancy;
import com.example.campusteamup.MyViewModel.RoleViewModel;

import com.example.campusteamup.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
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
}