package com.example.user.loginwhithfb.application;

import com.example.user.loginwhithfb.eventbus.BusProvider;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by POSTER on 29.07.2017.
 */

public class MyApplication extends android.app.Application {

    public static int value;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        BusProvider.getInstance().register(this);
        System.out.println("from app");
    }
}
