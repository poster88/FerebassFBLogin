package com.example.user.loginwhithfb.activity;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;


import com.example.user.loginwhithfb.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import butterknife.BindView;
import butterknife.OnClick;


public class LoginActivity extends BaseActivity {
    @BindView(R.id.emailEdit) EditText emailEdit;
    @BindView(R.id.passEdit) EditText passEdit;

    private OnCompleteListener onCompleteListenerSignIn = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()){
                LoginActivity.super.startCurActivity(LoginActivity.this, NavigationDrawerActivity.class);
                finish();
            }else {
                LoginActivity.super.showToast(LoginActivity.this, "Authentication failed. Try again");
            }
            LoginActivity.super.hideProgressDialog();
        }
    };

    private OnCompleteListener onCompleteListenerSentPass = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()) {
                LoginActivity.super.showToast(LoginActivity.this, "Password sent to your email");
            }else {
                LoginActivity.super.showToast(LoginActivity.this, "Fail to sent : " + task.getException());
            }
        }
    };

    private OnCompleteListener onCompleteListenerAnonSignIn = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (!task.isSuccessful()){
                LoginActivity.super.showToast(LoginActivity.this, "Exception : " + task.getException());
            }else {
                LoginActivity.super.startCurActivity(LoginActivity.this, NavigationDrawerActivity.class);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        super.setActivityForBinder(this);
        checkUserData();
    }

    private void checkUserData(){
        if (super.user != null && !super.user.isAnonymous()){
            LoginActivity.super.startCurActivity(LoginActivity.this, NavigationDrawerActivity.class);
            finish();
        }
    }

    private boolean validateForm(String email, String password) {
        boolean validation = true;
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            emailEdit.setError("Required");
            validation = false;
        } else {
            emailEdit.setError(null);
        }
        return validation;
    }

    private void signIn(String email, String password) {
        if (!validateForm(email, password)) {
            return;
        }
        super.showProgressDialog("Loading...");
        super.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(onCompleteListenerSignIn);
    }

    @OnClick(R.id.skipImgBtn)
    public void anonymouslySingIn(){
        super.auth.signInAnonymously().addOnCompleteListener(onCompleteListenerAnonSignIn);
    }

    @OnClick({R.id.sign_in_btn, R.id.registration_btn})
    public void pickAction(TextView textView){
        if (textView.getId() == R.id.sign_in_btn){
            signIn(emailEdit.getText().toString(), passEdit.getText().toString());
        }else {
            LoginActivity.super.startCurActivity(LoginActivity.this, RegistrationActivity.class);
        }
    }

    @OnClick(R.id.sent_pass_on_email)
    public void sendPassResetEmail() {
        String emailAddress = auth.getCurrentUser().getEmail();
        auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(onCompleteListenerSentPass);
    }
}