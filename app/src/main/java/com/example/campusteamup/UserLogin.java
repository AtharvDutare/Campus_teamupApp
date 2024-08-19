package com.example.campusteamup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
import android.os.Build;
import android.view.View;

import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.databinding.ActivityUserLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

public class UserLogin extends AppCompatActivity {
    ActivityUserLoginBinding userLoginBinding;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userLoginBinding = ActivityUserLoginBinding.inflate(getLayoutInflater());
        setContentView(userLoginBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        Call_Method.lightActionBar(getWindow());

        showProgressBar(false);
        userLoginBinding.signUpButton.setOnClickListener(v->{
            startActivity(new Intent(UserLogin.this,UserSignUp.class));
        });

        userLoginBinding.loginButton.setOnClickListener(v->{
            String email = userLoginBinding.loginEmail.getEditText().getText().toString();
            String password = userLoginBinding.loginPassword.getEditText().getText().toString();
            if(isEmailAndPasswordCorrect(email , password)){
              showProgressBar(true);
                loginUser(email , password);
            }
        });

    }
    public boolean isEmailAndPasswordCorrect(String email , String password){
        if(email.isEmpty()){
            Call_Method.showToast(UserLogin.this,"Please enter email");
            userLoginBinding.loginEmail.requestFocus();
            userLoginBinding.loginEmail.setError("Email required");
            return false;
        }

        if(password.isEmpty()){
            Call_Method.showToast(UserLogin.this,"Password must be greater than 6 character");
            return false;
        }
        return true;
    }
    public void loginUser(String email , String password){
        firebaseAuth.signInWithEmailAndPassword(email , password)
                .addOnCompleteListener(task->{
                    showProgressBar(false);
                    if(task.isSuccessful()){
                        Call_Method.showToast(UserLogin.this,"Login Sucessfull");
                        startActivity(new Intent(UserLogin.this , MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(e->{
                    showProgressBar(false);
                    Call_Method.showToast(UserLogin.this , "Account with email does not exists");
                });
    }
    public void showProgressBar(boolean flag){
        if(flag)
            userLoginBinding.progressBar.setVisibility(View.VISIBLE);
        else
            userLoginBinding.progressBar.setVisibility(View.GONE);
    }
}