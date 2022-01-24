package com.example.oweme.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.oweme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Checking user auth status, if not logged in then display login activity
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(Login.this, MainActivity.class));
        }else {
            setContentView(R.layout.activity_login);
        }

    }

    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // After user is logged on then navigate to main activity page
                            startActivity(new Intent(Login.this, MainActivity.class ));
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    private void updateUI(FirebaseUser user) {

    }

    public void login(View view){
        EditText inputEmail     = findViewById(R.id.ET_email);
        EditText inputPassword  = findViewById(R.id.ET_password);
        String email            = inputEmail.getText().toString();
        String password         = inputPassword.getText().toString();

        if(email.isEmpty()){
            Toast.makeText(Login.this, "Please enter your email correctly", Toast.LENGTH_SHORT).show();
        }else if(password.isEmpty()){
            Toast.makeText(Login.this, "Please enter your password", Toast.LENGTH_SHORT).show();
        }else{
            signIn(email,password);
        }
    }

    public void toNewAccount(View view){
        Intent intent = new Intent(this, SignUp.class );
        startActivity(intent);
    }
}