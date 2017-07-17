package com.example.user.loginwhithfb.activity;


import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.user.loginwhithfb.BaseActivity;
import com.example.user.loginwhithfb.fragment.LoginFragment;
import com.example.user.loginwhithfb.R;


public class MainActivity extends BaseActivity {
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (super.user != null && !super.user.isAnonymous()){
            startActivity(new Intent(this, NavigationDrawerActivity.class));
        }else{
            if (fragmentManager == null){
                fragmentManager = getSupportFragmentManager();
            }
            fragmentManager.beginTransaction().replace(R.id.container, new LoginFragment()).commit();
        }
    }
}
