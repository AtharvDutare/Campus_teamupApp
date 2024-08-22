package com.example.campusteamup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyUtil.Google_SignIn_Methods;
import com.example.campusteamup.databinding.ActivityUserLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class UserLogin extends AppCompatActivity {
    ActivityUserLoginBinding userLoginBinding;
    FirebaseAuth firebaseAuth;
    GoogleSignInOptions gso;
    GoogleSignInClient googleSignInClient;
    ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userLoginBinding = ActivityUserLoginBinding.inflate(getLayoutInflater());
        setContentView(userLoginBinding.getRoot());



            firebaseAuth = FirebaseAuth.getInstance();
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail().build();
            googleSignInClient  = GoogleSignIn.getClient(this,gso);




        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == RESULT_OK){
                try {
                Intent data = result.getData();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                    GoogleSignInAccount account = task.getResult(ApiException.class);

                    Google_SignIn_Methods.authenticateWithFirebase(firebaseAuth , account.getIdToken() , this );
                        startActivity(new Intent(UserLogin.this , MainActivity.class));
                        finish();




                } catch (ApiException e) {
                    Log.w("TAG", "Google sign in failed", e);
                }
            }
        });

        try{
            userLoginBinding.loginWithGoogle.setOnClickListener(v -> {
                Intent intent = googleSignInClient.getSignInIntent();
                resultLauncher.launch(intent);
            });
        }
        catch (Exception e){
            Call_Method.showToast(UserLogin.this , "SignIn click exception");
        }


        Call_Method.lightActionBar(getWindow());

        showProgressBar(false);
        userLoginBinding.signUpButton.setOnClickListener(v->{
            startActivity(new Intent(UserLogin.this,UserSignUp.class));
            finish();
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