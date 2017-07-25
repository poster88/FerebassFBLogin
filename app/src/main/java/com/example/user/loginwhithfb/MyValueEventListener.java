package com.example.user.loginwhithfb;


import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by POSTER on 19.07.2017.
 */

public abstract class MyValueEventListener implements ValueEventListener{

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
