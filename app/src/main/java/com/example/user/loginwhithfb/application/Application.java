package com.example.user.loginwhithfb.application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by POSTER on 29.07.2017.
 */

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
