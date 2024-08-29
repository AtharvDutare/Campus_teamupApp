package com.example.campusteamup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

public class MainActivity extends AppCompatActivity  {
    ActivityMainBinding activityMainBinding ;
    FragmentManager fragmentManager;
   private RoleViewModel roleViewModel;
   Dialog logOutDialog ;
   TextView logoutYes , logoutNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         activityMainBinding  = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        roleViewModel = new ViewModelProvider(this).get(RoleViewModel.class);

        initializeTheLogoutDialog();
         logoutYes.setOnClickListener(v -> {
             logOutDialog.dismiss();
             FirebaseAuth.getInstance().signOut();
             Call_Method.showToast(MainActivity.this , "Logout Successfully");
             startActivity(new Intent(MainActivity.this , UserLogin.class));
             finish();

         });
         logoutNo.setOnClickListener(v -> logOutDialog.dismiss());

        setUserImageToProfileBtn();


        Call_Method.lightActionBar(getWindow());  // making actionbar light so that date , battery becomes visible

        activityMainBinding.addRoleToGetHired.setVisibility(View.VISIBLE);

        activityMainBinding.drawerBtn.setOnClickListener(v -> activityMainBinding.drawerLayout.open());


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



//        activityMainBinding.profileButton.setOnClickListener(v->{
//            startActivity(new Intent(MainActivity.this,User_Profile.class));
//        });

       View view = activityMainBinding.navigationView.getHeaderView(0);
        TextView email = view.findViewById(R.id.userEmail);
        email.setText("ayush@gmail.com");//for testing purpose

       view.findViewById(R.id.cardImageLayout).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               activityMainBinding.drawerLayout.close();
               Call_Method.showToast(MainActivity.this ,"ImageClicked");


               startActivity(new Intent(MainActivity.this,User_Profile.class));
           }
       });



        activityMainBinding.addRoleToGetHired.setOnClickListener(v->{
            Post_Role postRoleFragment = new Post_Role();
            postRoleFragment.show(getSupportFragmentManager(),postRoleFragment.getTag());

        });

        activityMainBinding.postRoleToFindTeamMember.setOnClickListener(v -> {
            Post_Vacancy postVacancy = new Post_Vacancy();
            postVacancy.show(getSupportFragmentManager(),postVacancy.getTag());
        });

        activityMainBinding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if(itemId == R.id.teamInfo){
                    handleDrawerItemClick("team_details","teamDetails");
                }
                else if(itemId == R.id.notifications)
                {
                    handleDrawerItemClick("notification_details","notificationDetails");
                }
                else if(itemId == R.id.logOut){
                    logOutDialog.show();
                }
                return false;
            }
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

                                View headerView = activityMainBinding.navigationView.getHeaderView(0);
                                ImageView userProfile = headerView.findViewById(R.id.userProfileImage);

                                if(snapshot != null && snapshot.exists()){
                                    String imageUri = snapshot.getString("imageUri");

                                    if(imageUri != null && !imageUri.isEmpty())
                                        loadImage(imageUri , userProfile);
                                    else {
                                        userProfile.setImageResource(R.drawable.profile_icon);
                                    }
                                }
                                else{
                                    userProfile.setImageResource(R.drawable.profile_icon);
                                }
                            }
                        }
                    });
        }
        catch (Exception ignored){

        }


    }
    public void loadImage(String imageUri , ImageView userProfile){

        Glide.with(this).load(imageUri).into(userProfile);
    }
   public void initializeTheLogoutDialog(){
       logOutDialog = new Dialog(this);
       logOutDialog.setContentView(R.layout.logout_dialog);
       logoutYes = logOutDialog.findViewById(R.id.logOutYes);
       logoutNo = logOutDialog.findViewById(R.id.logOutNo);
   }
   public void handleDrawerItemClick(String intentTag ,String fragId ){
        Intent intent = new Intent(MainActivity.this,Drawer_Items_Activity.class);
        intent.putExtra(intentTag,fragId);
        startActivity(intent);
   }

}