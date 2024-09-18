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
    public static DocumentReference findUserWithUserId(String userId){
        return FirebaseFirestore.getInstance().collection("users").document(userId);
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
    public static DocumentReference fetchCollegeDetails(String userId){
        return FirebaseFirestore.getInstance().collection("collegeDetails").document(userId);
    }
    public static DocumentReference fetchCodingProfiles(String userId){
        return FirebaseFirestore.getInstance().collection("codingProfiles").document(userId);
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

    public static Query checkExistenceOfUser(){
        return FirebaseFirestore.getInstance().collection("users").whereEqualTo("userId",currentUserUid());
    }
    public static Query searchForVacancy(String rolesToSearch){
        return FirebaseFirestore.getInstance().collection("vacancy").orderBy("roleLookingFor").startAt(rolesToSearch).endAt(rolesToSearch +  "\uf8ff");
    }
    public static Query searchForRoles(String rolesToSearch){
        return FirebaseFirestore.getInstance().collection("roles").orderBy("roleName").startAt(rolesToSearch).endAt(rolesToSearch +  "\uf8ff");
    }


    public static DocumentReference uploadTeamDetails(){
        return FirebaseFirestore.getInstance().collection("teamDetails").document(currentUserUid());
    }
    // fetch team member details also if other added the user before

    public static CollectionReference findTeamDetails(){
        return FirebaseFirestore.getInstance().collection("teamDetails");
    }

    public static DocumentReference findingMailId(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserUid());
    }
    public static Query findImageByEmail(){
        return FirebaseFirestore.getInstance().collection("userImages");
    }
    public static DocumentReference saveFCM(String userId){
        return FirebaseFirestore.getInstance().collection("fcmtoken").document(userId);
    }
}
