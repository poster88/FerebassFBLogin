package com.example.user.loginwhithfb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private boolean isUserClickedBackButton = false;
    private FirebaseUser user;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showProgressDialog();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            startActivity(new Intent(getApplicationContext(), NavigationDrawerActivity.class));
        }else{
            if (fragmentManager == null){
                fragmentManager = getSupportFragmentManager();
            }

            fragmentManager.beginTransaction().replace(R.id.container, new LoginFragment()).commit();
        }
        hideProgressDialog();
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isUserClickedBackButton){
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            isUserClickedBackButton = true;
        }else {
            super.onBackPressed();
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
