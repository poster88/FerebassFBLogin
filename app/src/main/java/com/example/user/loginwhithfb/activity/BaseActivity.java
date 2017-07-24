package com.example.user.loginwhithfb.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.user.loginwhithfb.MyValueEventListener;
import com.example.user.loginwhithfb.model.UserLoginInfoTable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.ButterKnife;

/**
 * Created by POSTER on 17.07.2017.
 */

public class BaseActivity extends AppCompatActivity{

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

    protected final String USER_INFO_TABLE = "UserLoginInfoTable";
    protected final String TAG_HOME = "Products catalog";
    protected final String TAG_ACCOUNT = "My account";
    protected final String TAG_ORDER = "My orders";
    protected final String TAG_CHAT = "Company chat";
    protected final String TAG_FAVORITE = "Favorite";
    protected final String TAG_NEWS = "News";
    protected final String TAG_INFORMATION = "Information";
    protected String CURRENT_TAG = TAG_HOME;
    private boolean isUserClickedBackButton = false;

    public static DatabaseReference reference;
    public static UserLoginInfoTable userModel;

    protected Handler handler;
    protected Runnable runnable = new Runnable() {
        @Override
        public void run() {
            reference = database.getReference(USER_INFO_TABLE);
            Query query = reference.orderByChild("email").equalTo(user.getEmail());
            query.addListenerForSingleValueEvent(new MyValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot data: dataSnapshot.getChildren()){
                        userModel = data.getValue(UserLoginInfoTable.class);
                    }
                }
            });
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null){
            fragmentManager = getSupportFragmentManager();
            database = FirebaseDatabase.getInstance();
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            handler = new Handler();
            if (user != null ){
                if (!user.isAnonymous()){
                    handler.post(runnable);
                }
            }
        }
    }

    protected void setActivityForBinder(Activity activity){
        ButterKnife.bind(activity);
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

    protected void showProgressDialog(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
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

    protected void showToast(Context context, String title){
        Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
    }

    protected void startCurActivity(Context packageContext, Class<?> cls){
        startActivity(new Intent(packageContext, cls));
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

    protected void exitProgram(){
        if (!isUserClickedBackButton){
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            isUserClickedBackButton = true;
        }else {
            super.onBackPressed();
        }
        setExitTimer();
    }
}
