package com.example.oweme.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.oweme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    // [START declare_auth]
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    // [END declare_auth]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        EditText inputEmail         = findViewById(R.id.ET_email);
        EditText inputpassword      = findViewById(R.id.ET_password);
        EditText inputconfirmpass   = findViewById(R.id.ET_confirmPassword);
        EditText inputphonenumber   = findViewById(R.id.ET_phoneNumber);
        Button btnSignUp            = findViewById(R.id.BTN_createNewAccount);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth   = FirebaseAuth.getInstance();
        db      = FirebaseFirestore.getInstance();
        // [END initialize_auth]

        btnSignUp.setOnClickListener(v -> {
            String stremail     = inputEmail.getText().toString();
            String strpassword  = inputpassword.getText().toString();
            String strconfirm   = inputconfirmpass.getText().toString();
            String strphonenumber = inputphonenumber.getText().toString();

            if(strpassword.equals(strconfirm)){
                createAccount(stremail, strpassword,strphonenumber);
            }
        });



    }

    private void createAccount(String email, String password, String phonenumber) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Adding user information into database
                            Map<String,Object> userinfo = new HashMap<>();
                            userinfo.put("phonenumber", phonenumber);

                            DocumentReference documentReference = db.collection("users").document(user.getUid());
                            documentReference.set(userinfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG,"User info has been added");
                                    startActivity(new Intent(SignUp.this,
                                            Login.class ));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Database add failure");
                                }
                            });
                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }

    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Email sent
                    }
                });
        // [END send_email_verification]
    }

    private void reload() { }

    private void updateUI(FirebaseUser user) {

    }

}