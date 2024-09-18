package com.example.campusteamup.DashBoard;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.campusteamup.Drawer_Items_Activity;
import com.example.campusteamup.FCM.MyFirebaseMessagingService;
import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyFragments.Apply_Section;
import com.example.campusteamup.MyFragments.Hire_Section;
import com.example.campusteamup.MyFragments.Post_Vacancy;
import com.example.campusteamup.MyModels.UserSignUpDetails;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.MyViewModel.RoleViewModel;

import com.example.campusteamup.R;
import com.example.campusteamup.UserLogin;
import com.example.campusteamup.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding activityMainBinding;
    FragmentManager fragmentManager;
    private RoleViewModel roleViewModel;
    Dialog logOutDialog;
    TextView logoutYes, logoutNo;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());


            Call_Method.lightActionBar(getWindow());  // making actionbar light so that date , battery becomes visible


        SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();




        roleViewModel = new ViewModelProvider(this).get(RoleViewModel.class);

        initializeTheLogoutDialog();
        logoutYes.setOnClickListener(v -> {
            logOutDialog.dismiss();
            FirebaseAuth.getInstance().signOut();
            Call_Method.showToast(MainActivity.this, "Logout Successfully");
            startActivity(new Intent(MainActivity.this, UserLogin.class));
            finish();
        });
        logoutNo.setOnClickListener(v -> logOutDialog.dismiss());


        setUserImageToProfileBtn();

        auth = FirebaseAuth.getInstance();
        authStateListener = firebaseAuth -> {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                setUserNameAndEmail();
                MyFirebaseMessagingService.generateFCM(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(task.isSuccessful()){
                            String fcm = task.getResult();
                            if(fcm == null )
                            {
                                Log.d("FCM","FCM Null");
                            }
                            else{
                                Log.d("FCM","Generated Successfully " + fcm);
                                MyFirebaseMessagingService.saveFcmToFirebase(fcm);
                            }
                        }

                    }
                });
            }
        };

        auth.addAuthStateListener(authStateListener);




        activityMainBinding.addRoleToGetHired.setVisibility(View.VISIBLE);

        activityMainBinding.drawerBtn.setOnClickListener(v -> activityMainBinding.drawerLayout.open());


        try{
            fragmentManager = getSupportFragmentManager();       // default fragment for HOME_ACTIVITY
            fragmentManager.beginTransaction()
                    .replace(R.id.mainFrameLayout, new Apply_Section())
                    .commitAllowingStateLoss();


            activityMainBinding.bottomNavLayout.setSelectedItemId(R.id.apply);

            activityMainBinding.bottomNavLayout.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.hire) {
                    activityMainBinding.addRoleToGetHired.setVisibility(View.VISIBLE);
                    activityMainBinding.postRoleToFindTeamMember.setVisibility(View.GONE);

                    fragmentManager.beginTransaction()
                            .replace(R.id.mainFrameLayout, new Hire_Section())
                            .commit();
                    return true;
                } else if (id == R.id.apply) {
                    activityMainBinding.addRoleToGetHired.setVisibility(View.GONE);
                    activityMainBinding.postRoleToFindTeamMember.setVisibility(View.VISIBLE);

                    fragmentManager.beginTransaction()
                            .replace(R.id.mainFrameLayout, new Apply_Section())
                            .commit();
                    return true;
                } else if (id == R.id.projects) {
                    activityMainBinding.addRoleToGetHired.setVisibility(View.GONE);
                    activityMainBinding.postRoleToFindTeamMember.setVisibility(View.GONE);

                    fragmentManager.beginTransaction()
                            .replace(R.id.mainFrameLayout, new Projects())
                            .commit();
                    return true;
                }
                return false;
            });
        }
        catch (Exception e){
            Call_Method.showToast(this , "Error he bhai");
        }



        View view = activityMainBinding.navigationView.getHeaderView(0);
        view.findViewById(R.id.cardImageLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityMainBinding.drawerLayout.close();
                startActivity(new Intent(MainActivity.this, User_Profile.class));
            }
        });


        activityMainBinding.addRoleToGetHired.setOnClickListener(v -> {
            Post_Role postRoleFragment = new Post_Role();
            postRoleFragment.show(getSupportFragmentManager(), postRoleFragment.getTag());

        });

        activityMainBinding.postRoleToFindTeamMember.setOnClickListener(v -> {
            Post_Vacancy postVacancy = new Post_Vacancy();
            postVacancy.show(getSupportFragmentManager(), postVacancy.getTag());
        });


        activityMainBinding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.teamInfo) {
                    handleDrawerItemClick("team_details", "teamDetails");
                } else if (itemId == R.id.notifications) {
                    handleDrawerItemClick("notification_details", "notificationDetails");
                } else if (itemId == R.id.logOut) {
                    logOutDialog.show();
                }
                return false;
            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public RoleViewModel get() {
        return roleViewModel;
    }

    public void setUserImageToProfileBtn() {
        try {
            View headerView = activityMainBinding.navigationView.getHeaderView(0);
            ImageView userProfile = headerView.findViewById(R.id.userProfileImage);
            ProgressBar progressBar = headerView.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);

            FirebaseUtil.databaseUserImages().get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();

                                if (snapshot != null && snapshot.exists()) {
                                    String imageUri = snapshot.getString("imageUri");

                                    if (imageUri != null && !imageUri.isEmpty())
                                        loadImage(imageUri, userProfile, progressBar);
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        } catch (Exception ignored) {

        }


    }

    public void loadImage(String imageUri, ImageView userProfile, ProgressBar progressBar) {
        Glide.with(this).load(imageUri).into(userProfile);
        progressBar.setVisibility(View.GONE);
    }

    public void initializeTheLogoutDialog() {
        logOutDialog = new Dialog(this);
        logOutDialog.setContentView(R.layout.logout_dialog);
        logoutYes = logOutDialog.findViewById(R.id.logOutYes);
        logoutNo = logOutDialog.findViewById(R.id.logOutNo);
    }

    public void handleDrawerItemClick(String intentTag, String fragId) {
        Intent intent = new Intent(MainActivity.this, Drawer_Items_Activity.class);
        intent.putExtra(intentTag, fragId);
        startActivity(intent);
    }

    public void setUserNameAndEmail() {
        View view = activityMainBinding.navigationView.getHeaderView(0);
        TextView userName = view.findViewById(R.id.userName);
        TextView userEmail = view.findViewById(R.id.userEmail);



        FirebaseUtil.currentUserDetails().get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            UserSignUpDetails userDetails = document.toObject(UserSignUpDetails.class);
                            if (userDetails != null && !userDetails.getUserName().isEmpty()) {
                                String getUserName = userDetails.getUserName();

                                editor.putString("userName",getUserName);

                                Log.d("UserDetails",getUserName);
                                userName.setText(getUserName);

                                String getUserId = userDetails.getUserId();

                                editor.putString("userId",getUserId);
                                Log.d("UserDetails",getUserId);


                            }
                            if (userDetails != null && !userDetails.getEmail().isEmpty()) {
                                String getUserEmail = userDetails.getEmail();
                                editor.putString("userEmail",getUserEmail);
                                Log.d("UserDetails",getUserEmail);
                                userEmail.setText(getUserEmail);

                            }

                            editor.apply();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }




}