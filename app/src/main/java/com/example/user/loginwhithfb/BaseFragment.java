package com.example.user.loginwhithfb;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by POSTER on 17.07.2017.
 */

public class BaseFragment extends Fragment{
    protected FirebaseDatabase database;
    protected FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        return super.onCreateView(inflater, container, savedInstanceState);


    }
}
