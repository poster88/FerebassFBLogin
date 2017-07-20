package com.example.user.loginwhithfb.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.user.loginwhithfb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by POSTER on 17.07.2017.
 */

public class BaseFragment extends Fragment{

    protected FirebaseDatabase database;
    protected FirebaseUser user;
    protected StorageReference storageRef;
    protected ProgressDialog progressDialog;

    protected final static String USERS_IMAGES = "users_images";
    protected final static String REF_USER_PHOTO = "UsersPhoto";
    protected final static int PHOTO_REQUEST = 9002;
    protected final static int REQUEST_READ_PERMISSION = 9003;
    protected final static int RESULT_OK = -1;
    protected Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void showProgressDialog() {
        if (progressDialog == null && !progressDialog.isShowing()) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    protected void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    protected void setFragmentForBinder(Object target, View source){
        unbinder = ButterKnife.bind(target, source);
    }

    protected void showToast(Context context, String title){
        Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
    }

    protected void startCurActivity(Context packageContext, Class<?> cls){
        startActivity(new Intent(packageContext, cls));
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null){
            unbinder.unbind();
        }
        super.onDestroyView();
    }
}