package com.example.oweme.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.oweme.models.Bill;
import com.example.oweme.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseCloudService {
    private static final String TAG = "FirebaseCloudService";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseMessaging firebaseMessaging;
    private User user = new User();

    public FirebaseCloudService() {
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
        this.firebaseMessaging = FirebaseMessaging.getInstance();
    }

    public void setUserToken(){
        firebaseMessaging.getToken().addOnCompleteListener((task -> {
            if(task.isSuccessful()){
                Map<String,Object> token = new HashMap<>();
                token.put("deviceToken", task.getResult());

                DocumentReference documentReference = db.collection("users").document(mAuth.getCurrentUser().getUid());
                documentReference.set(token, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Device token successfully saved");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Device token not found. Hence, it is not saved to cloud");
                    }
                });

            }else{
                Log.d(TAG, "The token cannot be retrieved. Hence, it is not saved to firestore");
            }
        }));
    }

}
