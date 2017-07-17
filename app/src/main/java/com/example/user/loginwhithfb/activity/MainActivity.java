package com.example.user.loginwhithfb.activity;


import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.user.loginwhithfb.fragment.LoginFragment;
import com.example.user.loginwhithfb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private boolean isUserClickedBackButton = false;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkUser();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !user.isAnonymous() /*&& !checkUser()*/){
            startActivity(new Intent(this, NavigationDrawerActivity.class));
        }else{
            if (fragmentManager == null){
                fragmentManager = getSupportFragmentManager();
            }
            fragmentManager.beginTransaction().replace(R.id.container, new LoginFragment()).commit();
        }
    }

    private boolean checkUser() {
        Intent intent = getIntent();
        if (intent.getBooleanExtra("OnBackPressed", false)){
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!isUserClickedBackButton){
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            isUserClickedBackButton = true;
        }else {
            System.exit(0);
        }
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                isUserClickedBackButton = false;
            }
        }.start();
    }
}
