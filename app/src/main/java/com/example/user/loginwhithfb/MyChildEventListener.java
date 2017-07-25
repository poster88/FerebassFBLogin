package com.example.user.loginwhithfb;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;

/**
 * Created by POSTER on 25.07.2017.
 */

public abstract class MyChildEventListener implements ChildEventListener{
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }
}
