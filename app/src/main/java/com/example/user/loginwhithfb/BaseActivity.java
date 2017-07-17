package com.example.user.loginwhithfb;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by POSTER on 17.07.2017.
 */

public class BaseActivity extends AppCompatActivity{

    protected FirebaseDatabase database;
    protected FirebaseUser user;
    private boolean isUserClickedBackButton = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

    }

    private void setExitTimer(){
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

    @Override
    public void onBackPressed() {
        //getFragmentManager().getBackStackEntryCount()
        //getSupportFragmentManager().getFragments()
        if (!isUserClickedBackButton){
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            isUserClickedBackButton = true;
        }else {
            super.onBackPressed();
        }
        setExitTimer();
    }
}
