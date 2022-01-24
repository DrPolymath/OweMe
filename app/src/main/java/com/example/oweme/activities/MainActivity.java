package com.example.oweme.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.oweme.R;
import com.example.oweme.models.User;
import com.example.oweme.utils.FirebaseCloudService;
import com.example.oweme.utils.FirebaseService;
import com.example.oweme.utils.SharedViewModel;
import com.example.oweme.models.Bill;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // UTILITY
    private AppBarConfiguration appBarConfiguration;
    private FirebaseAuth mAuth;
    private SharedViewModel sharedViewModel;
    private FirebaseService firebaseService;
    private FirebaseCloudService firebaseCloudService;

    // USER DATA
    private User user;
    private String token;



    // DATA
    public ArrayList<Bill> billList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Checking current user authentication
        mAuth = FirebaseAuth.getInstance();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener((task) -> {
                    if(task.isSuccessful()){
                        this.token = task.getResult();
                        Log.d("MainActivity", this.token);
                    }else {
                        String notoken="Token not found";
                        Log.d("MainAcitivty", notoken);
                    }
                });
        Log.d("MainActivity", mAuth.getCurrentUser().getUid());
        firebaseCloudService = new FirebaseCloudService();
        firebaseCloudService.setUserToken();


        // [START toolbar configuration]
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
            // Customizing toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setOverflowIcon(getDrawable(R.drawable.ic_user));
        NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.NHFMain);
        NavController navController = host.getNavController();
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        // [END toolbar configuration]

//        binding.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        // [START loading data]
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

    }

//    public void sendNotificationToCreditor(View v){
//        String title = "OweMe";
//        String message = "Your friend want to settle your payment";
//        FirebaseNotificationSender firebaseNotificationSender = new FirebaseNotificationSender("dlLeY0ijQ22Jt0X-5YSZpC:APA91bFSECSLWrvydUMw38Y5oOU3f714QFrWp1huwELJs4LL1g3u5fxC792JVb8H09cuXoXHvBqGoFfkLtE-Pkd_7dgkn2sXG5EinHWZIyfwPolTrMfU-lOp_1_GfwWjATME5aye1dEa",title,message
//        ,getApplicationContext(),MainActivity.this);
//        firebaseNotificationSender.SendNotification();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_LogOut) {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, Login.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.NHFMain).navigateUp();
    }


}