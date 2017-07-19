package com.example.user.loginwhithfb.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.user.loginwhithfb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by POSTER on 17.07.2017.
 */

public class BaseActivity extends AppCompatActivity{

    private boolean isUserClickedBackButton = false;

    protected FirebaseDatabase database;
    protected FirebaseAuth auth;
    protected FirebaseUser user;
    protected FragmentManager fragmentManager;
    protected ProgressDialog progressDialog;
    protected FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            user = firebaseAuth.getCurrentUser();
        }
    };

    protected final String TAG_HOME = "Products catalog";
    protected final String TAG_ACCOUNT = "My account";
    protected final String TAG_ORDER = "My orders";
    protected final String TAG_CHAT = "Company chat";
    protected final String TAG_FAVORITE = "Favorite";
    protected final String TAG_NEWS = "News";
    protected final String TAG_INFORMATION = "Information";
    protected String CURRENT_TAG = TAG_HOME;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null){
            fragmentManager = getSupportFragmentManager();
            database = FirebaseDatabase.getInstance();
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
        }
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
        if (!isUserClickedBackButton){
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            isUserClickedBackButton = true;
        }else {
            super.onBackPressed();
        }
        setExitTimer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null){
            auth.removeAuthStateListener(authStateListener);
        }
    }

    protected void showProgressDialog(Context context, String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(msg);
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    protected void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
