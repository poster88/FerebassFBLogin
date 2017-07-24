package com.example.user.loginwhithfb.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.example.user.loginwhithfb.MyChildEventListener;
import com.example.user.loginwhithfb.MyValueEventListener;
import com.example.user.loginwhithfb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by POSTER on 17.07.2017.
 */

public class BaseFragment extends Fragment {

    protected FirebaseDatabase database;
    protected FirebaseUser user;
    protected StorageReference storageRef;
    protected ProgressDialog progressDialog;

    protected final String USER_INFO_TABLE = "UserLoginInfoTable";
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

    protected void showAlertDialog(String title, String message, int icon, boolean cancelable, String positiveBtnTitle, String negativeBtnTitle, DialogInterface.OnClickListener posBtnClickListener, DialogInterface.OnClickListener negBtnClickListener) {
        AlertDialog.Builder ab = new AlertDialog.Builder(getContext());
        ab.setTitle(title);
        ab.setMessage(message);
        ab.setIcon(icon);
        ab.setCancelable(cancelable);
        ab.setPositiveButton(positiveBtnTitle, posBtnClickListener);
        ab.setNegativeButton(negativeBtnTitle, negBtnClickListener);
        ab.show();
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null){
            unbinder.unbind();
        }
        super.onDestroyView();
    }
}