package com.example.campusteamup.MyUtil;


import android.content.Context;
import androidx.annotation.NonNull;
import com.example.campusteamup.Method_Helper.Call_Method;
import com.example.campusteamup.MyCallBacks.CheckUserExits;
import com.example.campusteamup.MyModels.UserSignUpDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Google_SignIn_Methods {
    public static void authenticateWithFirebase(FirebaseAuth firebaseAuth , String idToken , Context context ){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken , null);

            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser user = firebaseAuth.getCurrentUser();

                                if (user != null) {
                                    newUserOrExisting(new CheckUserExits() {
                                        @Override
                                        public void checkUserExits(boolean isExists) {
                                            if (isExists) {
                                                Call_Method.showToast(context, "Welcome back");

                                            } else {
                                                setEmailAndNameOfUser(user , context);
                                                Call_Method.showToast(context, "Sign up successfully");
                                            }

                                        }
                                    });
                                }
                                else {
                                    Call_Method.showToast(context , "null user ");
                                }


                            } else {
                                Call_Method.showToast(context, "Network error");
                            }
                        }
                    });



    }
    public static void setEmailAndNameOfUser(FirebaseUser userDetails , Context context){
            UserSignUpDetails userSignUpDetails = new UserSignUpDetails(userDetails.getEmail(), Timestamp.now(), FirebaseUtil.currentUserUid(),userDetails.getDisplayName());

            FirebaseUtil.currentUserDetails().set(userSignUpDetails);


    }
    public static void newUserOrExisting(CheckUserExits exists){
        FirebaseUtil.checkExistenceOfUser().get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int count = 0;
                        for(DocumentSnapshot snapshot : task.getResult())
                            count++;

                        exists.checkUserExits(count != 0);
                    }
                });
    }
}
