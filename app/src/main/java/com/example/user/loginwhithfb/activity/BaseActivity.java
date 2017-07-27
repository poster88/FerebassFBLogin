package com.example.user.loginwhithfb.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.loginwhithfb.MyValueEventListener;
import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.model.CompaniesInfoTable;
import com.example.user.loginwhithfb.model.UserLoginInfoTable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.ButterKnife;

/**
 * Created by POSTER on 17.07.2017.
 */

public class BaseActivity extends AppCompatActivity {

    protected FirebaseDatabase database;
    protected FirebaseAuth auth;
    protected FirebaseUser user;
    protected FragmentManager fragmentManager;
    protected ProgressDialog progressDialog;
    protected FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            user = firebaseAuth.getCurrentUser();
        }
    };

    protected final String USER_INFO_TABLE = "UserLoginInfoTable";
    protected final String TAG_HOME = "Products catalog";
    protected final String TAG_ACCOUNT = "My account";
    protected final String TAG_ORDER = "My orders";
    protected final String TAG_CHAT = "Company chat";
    protected final String TAG_FAVORITE = "Favorite";
    protected final String TAG_NEWS = "News";
    protected final String TAG_INFORMATION = "Information";
    protected String CURRENT_TAG = TAG_HOME;
    private boolean isUserClickedBackButton = false;

    public static DatabaseReference refUserInfTable;
    public static UserLoginInfoTable userModel;
    public static CompaniesInfoTable companyInfoModel;

    private MyValueEventListener onUidUserDataListener = new MyValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot data: dataSnapshot.getChildren()){
                userModel = data.getValue(UserLoginInfoTable.class);
            }
        }
    };
    private OnFailureListener onFailureListenerProfileChange = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            showToast(getBaseContext(), "Fail to update user profile");
        }
    };

    protected Handler handler;
    protected Runnable runnable = new Runnable() {
        @Override
        public void run() {
            refUserInfTable = database.getReference(USER_INFO_TABLE);
            Query query = refUserInfTable.orderByChild("uID").equalTo(user.getUid());
            query.addListenerForSingleValueEvent(onUidUserDataListener);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null){
            fragmentManager = getSupportFragmentManager();
            database = FirebaseDatabase.getInstance();
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            handler = new Handler();
            checkUser();
        }
    }

    private void checkUser() {
        if (user != null){
            if (!user.isAnonymous()){
                handler.post(runnable);
                Log.d("TAG", "handler.post(runnable)");
            }
        }
    }

    protected void setActivityForBinder(Activity activity){
        ButterKnife.bind(activity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null){
            auth.removeAuthStateListener(authStateListener);
        }
    }

    protected void showProgressDialog(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(msg);
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    protected void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    protected void showToast(Context context, String title){
        Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
    }

    protected void startCurActivity(Context packageContext, Class<?> cls){
        startActivity(new Intent(packageContext, cls));
    }

    private void setExitTimer(){
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

    protected void exitProgram(){
        if (!isUserClickedBackButton){
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            isUserClickedBackButton = true;
        }else {
            super.onBackPressed();
        }
        setExitTimer();
    }

    protected void showAlertDialog(String title, String message, int icon, boolean cancelable, String positiveBtnTitle, String negativeBtnTitle, DialogInterface.OnClickListener posBtnClickListener, DialogInterface.OnClickListener negBtnClickListener) {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle(title);
        ab.setMessage(message);
        ab.setIcon(icon);
        ab.setCancelable(cancelable);
        ab.setPositiveButton(positiveBtnTitle, posBtnClickListener);
        ab.setNegativeButton(negativeBtnTitle, negBtnClickListener);
        ab.show();
    }

    protected void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    protected void updateUserNameAuth(String name) {
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
        user.updateProfile(profileChangeRequest).addOnFailureListener(onFailureListenerProfileChange);
    }

    protected boolean isValidEmail(EditText eMail, TextInputLayout inputLayoutEmail) {
        String mail = eMail.getText().toString().trim();
        if (TextUtils.isEmpty(mail)){
            inputLayoutEmail.setError(getString(R.string.err_msg_empty_email));
            requestFocus(eMail);
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(eMail);
            return false;
        }
        inputLayoutEmail.setErrorEnabled(false);
        return true;
    }

    protected boolean isValidPassword(EditText password, TextInputLayout inputLayoutPassword, EditText repPassword, TextInputLayout inputLayoutRepPassword){
        if (checkLengthPass(password, inputLayoutPassword) && checkLengthPass(repPassword, inputLayoutRepPassword)){
            if (password.getText().toString().equals(repPassword.getText().toString())){
                return true;
            }
            password.setText("");
            repPassword.setText("");
            requestFocus(password);
            inputLayoutPassword.setError(getString(R.string.err_msg_check_pass));
        }
        return false;
    }

    protected boolean checkLengthPass(EditText password, TextInputLayout inputLayoutPassword) {
        if (password.getText().toString().trim().isEmpty()){
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(password);
            return false;
        }
        if (password.getText().length() < 6){
            inputLayoutPassword.setError(getString(R.string.err_msg_password_length));
            requestFocus(password);
            return false;
        }
        inputLayoutPassword.setErrorEnabled(false);
        return true;
    }
    protected boolean isValidNumber(EditText number, TextInputLayout inputLayoutMobNum){
        if (number.getText().length() == 0){
            inputLayoutMobNum.setError(getString(R.string.err_msg_mob_number));
            requestFocus(number);
            return false;
        }
        inputLayoutMobNum.setErrorEnabled(false);
        return true;
    }

    protected boolean isValidPersonalData(EditText data, TextInputLayout inputLayoutData){
        if (data.getText().toString().trim().isEmpty()){
            inputLayoutData.setError(getString(R.string.err_msg_data));
            requestFocus(data);
            return false;
        }
        inputLayoutData.setErrorEnabled(false);
        return true;
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
