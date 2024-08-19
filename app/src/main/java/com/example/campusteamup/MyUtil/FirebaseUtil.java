package com.example.campusteamup.MyUtil;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Document;

public class FirebaseUtil {
    public static String currentUserUid(){
        return FirebaseAuth.getInstance().getUid();
    }
    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserUid());
    }
    public static CollectionReference sendRoleDetails(){
        return FirebaseFirestore.getInstance().collection("roles");
    }
    public static CollectionReference getAllUserRoles(){
        return FirebaseFirestore.getInstance().collection("roles");
    }

    public static DocumentReference fetchPersonalDetails(){
        return FirebaseFirestore.getInstance().collection("personalDetails").document(currentUserUid());
    }
    public static DocumentReference fetchCollegeDetails(){
        return FirebaseFirestore.getInstance().collection("collegeDetails").document(currentUserUid());
    }
    public static DocumentReference fetchCodingProfiles(){
        return FirebaseFirestore.getInstance().collection("codingProfiles").document(currentUserUid());
    }
    public static DocumentReference databaseUserImages(){
        return FirebaseFirestore.getInstance().collection("userImages").document(currentUserUid());
    }
    public static DocumentReference differentUserImages(String userId){
        return FirebaseFirestore.getInstance().collection("userImages").document(userId);
    }
    public static Query allRolesPostedByUser(){
        return  FirebaseFirestore.getInstance().collection("roles").whereEqualTo("userId",currentUserUid());
    }

    public static  CollectionReference allVacancy(){
        return FirebaseFirestore.getInstance().collection("vacancy");
    }
    public static Query allVacancyPostedByUser(){
        return FirebaseFirestore.getInstance().collection("vacancy").whereEqualTo("postedBy" , currentUserUid());
    }

}
