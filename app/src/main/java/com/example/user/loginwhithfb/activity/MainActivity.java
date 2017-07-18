package com.example.user.loginwhithfb.activity;


import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;

import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.fragment.LoginFragment;


public class MainActivity extends BaseActivity {
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (super.user != null && !super.user.isAnonymous()){
            startActivity(new Intent(this, NavigationDrawerActivity.class));
            //finish();
        }
        if (fragmentManager == null){
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, new LoginFragment()).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
