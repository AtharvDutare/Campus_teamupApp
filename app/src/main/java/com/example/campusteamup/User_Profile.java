package com.example.campusteamup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.PackageManager;
import android.os.Build;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.Manifest;

import com.bumptech.glide.Glide;
import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyFragments.Coding_Profile_Details;
import com.example.campusteamup.MyFragments.College_Details;
import com.example.campusteamup.MyFragments.Personal_Details;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.databinding.ActivityUserProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class User_Profile extends AppCompatActivity {

    Dialog logOutDialog;
    ActivityUserProfileBinding binding;
    private static final String TAG = "User_Profile";
    private static final String PERSONAL_DETAILS_FRAGMENT_TAG = "PersonalDetails";
    private static final String COLLEGE_DETAILS_FRAGMENT_TAG = "CollegeDetails";
    public static final String CODING_PROFILE_FRAGMENT_TAG = "CodingProfileDetails";
    public ActivityResultLauncher<String> requestPermissionLauncher;
    Uri selectedImage ;
    public ActivityResultLauncher<Intent> pickImageLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




            binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            Call_Method.lightActionBar(getWindow());

            setImageOfUser();

            logOutDialog = new Dialog(this);
            logOutDialog.setContentView(R.layout.logout_dialog);




        FragmentManager fragmentManager = getSupportFragmentManager();


        TextView logOutYes = logOutDialog.findViewById(R.id.logOutYes);
        TextView logOutNo = logOutDialog.findViewById(R.id.logOutNo);

        binding.logOutButton.setOnClickListener(v->{
            logOutDialog.show();
        });
        logOutYes.setOnClickListener(v->{
            logOutDialog.dismiss();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(User_Profile.this,UserLogin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        logOutNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutDialog.dismiss();
            }
        });
        binding.backToHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.personalDetailsBtn.setOnClickListener(v->{
            binding.personalDetailsBtn.setBackground(AppCompatResources.getDrawable(this , R.drawable.rounded_btn));
            setUpFragment(PERSONAL_DETAILS_FRAGMENT_TAG,fragmentManager,new Personal_Details());
        });

        binding.collegeDetailsBtn.setOnClickListener(v -> {
            setUpFragment(COLLEGE_DETAILS_FRAGMENT_TAG,fragmentManager,new College_Details());
        });

        binding.codingProfileBtn.setOnClickListener(v -> {
            setUpFragment(CODING_PROFILE_FRAGMENT_TAG,fragmentManager,new Coding_Profile_Details());
        });


        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
           if(result.getResultCode() == RESULT_OK && result.getData() != null){
                selectedImage = result.getData().getData();
               if(selectedImage != null){
                   binding.imageOfUser.setImageURI(selectedImage);
                   binding.uploadImage.setVisibility(View.VISIBLE);
               }

           }
           else {
               Call_Method.showToast(this , "No image selected");
           }
        });
        binding.imageOfUser.setOnClickListener(v -> {
            openGallery();
        });
        // send image to Firebase Firestore and Firebase Storage
        
        binding.uploadImage.setOnClickListener(v -> {
            if(selectedImage != null){
                uploadImageToStorage(selectedImage);
                binding.uploadImage.setVisibility(View.GONE);
            }

        });
    }
    public void setUpFragment(String tag , FragmentManager fragmentManager,Fragment fragmentToLoaded){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(tag.equals(PERSONAL_DETAILS_FRAGMENT_TAG)){

            handleUiForDetailsBtn(PERSONAL_DETAILS_FRAGMENT_TAG);

        }
        else if(tag.equals(COLLEGE_DETAILS_FRAGMENT_TAG))
        {
            handleUiForDetailsBtn(COLLEGE_DETAILS_FRAGMENT_TAG);

        }
        else if(tag.equals(CODING_PROFILE_FRAGMENT_TAG)){

            handleUiForDetailsBtn(CODING_PROFILE_FRAGMENT_TAG);

        }
        fragmentTransaction.replace(R.id.profileFrameLayout,fragmentToLoaded);
        fragmentTransaction.commit();
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }
    public void saveImageUriToFirestore(String imageUri){
        Map<String , Object>map = new HashMap<>();
        map.put("imageUri" , imageUri);
        FirebaseUtil.databaseUserImages().set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }
    public void uploadImageToStorage(Uri image){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageReference.child("images/" + System.currentTimeMillis());
        binding.imageOfUser.setImageURI(null);

        imageRef.putFile(image)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        binding.progressBar.setVisibility(View.VISIBLE);

                    }
                })
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isSuccessful()){
                                        saveImageUriToFirestore(task.getResult().toString());
                                        Call_Method.showToast(User_Profile.this , "Image uploaded");
                                        setImageOfUser();
                                    }

                                    else
                                        Call_Method.showToast(User_Profile.this , "Error");
                                }
                            });
                        }
                        else {
                            binding.imageOfUser.setImageResource(R.drawable.camera_icon);
                            Call_Method.showToast(User_Profile.this , "Upload failed");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Call_Method.showToast(User_Profile.this , e.toString());
                    }
                });
    }

    public void setImageOfUser(){
        binding.progressBar.setVisibility(View.VISIBLE);

         FirebaseUtil.databaseUserImages().get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                String imageUri = document.getString("imageUri");
                                if(imageUri != null && !imageUri.isEmpty()){
                                    loadImage(imageUri);
                                }

                            }
                            else {
                                binding.progressBar.setVisibility(View.GONE);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Call_Method.showToast(User_Profile.this , "Image not fetched");
                    }
                });

    }
    public void loadImage(String uri){
        Glide.with(this)
                .load(uri)
                .into(binding.imageOfUser);
        binding.progressBar.setVisibility(View.GONE);
    }
    public void handleUiForDetailsBtn(String tag){
        if(tag.equals(PERSONAL_DETAILS_FRAGMENT_TAG)){
            binding.personalDetailsBtn.setBackground(AppCompatResources.getDrawable(this , R.drawable.rounded_btn));
            binding.codingProfileBtn.setBackground(AppCompatResources.getDrawable(this , R.drawable.rounded_corner_edittext));
            binding.collegeDetailsBtn.setBackground(AppCompatResources.getDrawable(this , R.drawable.rounded_corner_edittext));

            binding.personalTextview.setTypeface(null, Typeface.BOLD);
            binding.codingTextview.setTypeface(null);
            binding.collegeTextview.setTypeface(null);
        }
        else if(tag.equals(COLLEGE_DETAILS_FRAGMENT_TAG)){
            binding.collegeDetailsBtn.setBackground(AppCompatResources.getDrawable(this , R.drawable.rounded_btn));
            binding.personalDetailsBtn.setBackground(AppCompatResources.getDrawable(this , R.drawable.rounded_corner_edittext));
            binding.codingProfileBtn.setBackground(AppCompatResources.getDrawable(this , R.drawable.rounded_corner_edittext));

            binding.personalTextview.setTypeface(null);
            binding.codingTextview.setTypeface(null);
            binding.collegeTextview.setTypeface(null,Typeface.BOLD);
        }
        else if(tag.equals(CODING_PROFILE_FRAGMENT_TAG)){
            binding.codingProfileBtn.setBackground(AppCompatResources.getDrawable(this , R.drawable.rounded_btn));
            binding.personalDetailsBtn.setBackground(AppCompatResources.getDrawable(this , R.drawable.rounded_corner_edittext));
            binding.collegeDetailsBtn.setBackground(AppCompatResources.getDrawable(this , R.drawable.rounded_corner_edittext));

            binding.personalTextview.setTypeface(null);
            binding.collegeTextview.setTypeface(null);
            binding.codingTextview.setTypeface(null,Typeface.BOLD);
        }
    }
}