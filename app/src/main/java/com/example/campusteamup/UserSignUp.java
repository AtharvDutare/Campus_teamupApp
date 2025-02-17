package com.example.campusteamup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import com.example.campusteamup.DashBoard.MainActivity;
import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyModels.UserSignUpDetails;
import com.example.campusteamup.MyUtil.FirebaseUtil;
import com.example.campusteamup.MyUtil.Google_SignIn_Methods;
import com.example.campusteamup.databinding.ActivityUserSignUpBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;

public class UserSignUp extends AppCompatActivity {
ActivityUserSignUpBinding signUpBinding ;
FirebaseAuth firebaseAuth;
GoogleSignInOptions gso ;
FirebaseDatabase firebaseDatabase;
GoogleSignInClient googleSignInClient;
ActivityResultLauncher<Intent> resultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpBinding = ActivityUserSignUpBinding.inflate(getLayoutInflater());
        setContentView(signUpBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail().build();
        googleSignInClient  = GoogleSignIn.getClient(this,gso);


        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            if(result.getResultCode() == RESULT_OK){
                Intent data = result.getData();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);

                    Google_SignIn_Methods.authenticateWithFirebase(firebaseAuth , account.getIdToken() , this );
                        startActivity(new Intent(UserSignUp.this, MainActivity.class));
                        finish();



                } catch (ApiException e) {
                    Log.w("TAG", "Google sign in failed", e);
                }
            }
            else {
                Call_Method.showToast(this , "Google Sign-in is in Development \n Sorry for Inconvenience Caused");
                Log.d("Result","Result not ok");
            }
        });
        signUpBinding.signInGoogle.setOnClickListener(v->{
            Intent signInIntent = googleSignInClient.getSignInIntent();
            resultLauncher.launch(signInIntent);
        });

        showProgressBar(false);
        Call_Method.lightActionBar(getWindow());

        signUpBinding.signUpButton.setOnClickListener(v -> {
            String email = signUpBinding.signUpEmail.getEditText().getText().toString();
            String password = signUpBinding.signUpPassword.getEditText().getText().toString();
            String userName = signUpBinding.userName.getEditText().getText().toString();
            if(isEmailAndPasswordCorrect(email , password,userName)){
                showProgressBar(true);
                signUpUser(email , password,userName);
            }
        });
        signUpBinding.loginButton.setOnClickListener(v->{
            startActivity(new Intent(UserSignUp.this,UserLogin.class));
            finish();
        });

    }
    public void signUpUser(String email , String password,String userName){
        firebaseAuth.createUserWithEmailAndPassword(email , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        showProgressBar(false);
                        if(task.isSuccessful()){
                            Call_Method.showToast(UserSignUp.this , "Sign Up Successfully");

                            //sending user details to FirebaseFireStore

                            UserSignUpDetails userSignUpDetails = new UserSignUpDetails(email, Timestamp.now(), FirebaseUtil.currentUserUid(),userName);
                            FirebaseUtil.currentUserDetails().set(userSignUpDetails);


                            startActivity(new Intent(UserSignUp.this , MainActivity.class));
                            finish();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showProgressBar(false);
                        if(e instanceof FirebaseAuthUserCollisionException){
                            Call_Method.showToast(UserSignUp.this,"Account with this email already exists. Please sign in");
                        }
                        else if(e instanceof FirebaseNetworkException){
                            Call_Method.showToast(UserSignUp.this , "Network Error");
                        }
                        else{
                            Call_Method.showToast(UserSignUp.this,"Sign Up failed");
                        }
                    }
                });
    }
    public boolean isEmailAndPasswordCorrect(String email , String password,String userName){
        if(email.isEmpty()){
            signUpBinding.signUpEmail.setError("Enter email");
            signUpBinding.signUpEmail.requestFocus();
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            signUpBinding.signUpEmail.setError("Enter valid email");
            return false;
        }
        if(password.isEmpty()){
            signUpBinding.signUpPassword.requestFocus();
            signUpBinding.signUpPassword.setError("Enter password");
            return false;
        }
        if(password.length() < 6 )
        {
            signUpBinding.signUpPassword.requestFocus();
            signUpBinding.signUpPassword.setError("Password must be more than 6 character");
            return false;
        }
        if(userName.isEmpty()){
            signUpBinding.userName.requestFocus();
            signUpBinding.userName.setError("Username required");
        }
        return true;
    }
    public void showProgressBar(boolean flag){
        if(flag)
            signUpBinding.progressBar.setVisibility(View.VISIBLE);
        else
            signUpBinding.progressBar.setVisibility(View.GONE);
    }

}