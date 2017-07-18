package com.example.user.loginwhithfb.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.loginwhithfb.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends BaseActivity {
    @BindView(R.id.emailEdit) EditText emailEdit;
    @BindView(R.id.passEdit) EditText passEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        checkUserData();
    }

    private void checkUserData(){
        if (super.user != null && !super.user.isAnonymous()){
            startActivity(new Intent(this, NavigationDrawerActivity.class));
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
       showProgressDialog(this, "Loading...");
        super.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(LoginActivity.this, NavigationDrawerActivity.class));
                }else {
                    Toast.makeText(LoginActivity.this, "Authentication failed. Try again", Toast.LENGTH_SHORT).show();
                }
                hideProgressDialog();
            }
        });
    }

    @OnClick(R.id.skipImgBtn)
    public void anonymouslySingIn(){
        auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Exception : " + task.getException(), Toast.LENGTH_SHORT).show();
                }else {
                    startActivity(new Intent(LoginActivity.this, NavigationDrawerActivity.class));
                }
            }
        });
    }

    @OnClick({R.id.sign_in_btn, R.id.registration_btn})
    public void pickAction(TextView textView){
        if (textView.getId() == R.id.sign_in_btn){
            signIn(emailEdit.getText().toString(), passEdit.getText().toString());
        }else {
            startActivity(new Intent(this, RegistrationActivity.class));
        }
    }

    @OnClick(R.id.sent_pass_on_email)
    public void sendPassResetEmail() {
        String emailAddress = auth.getCurrentUser().getEmail();
        auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "A new password sent to your email", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Fail to sent : " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void showProgressDialog(Context context, String msg) {
        super.showProgressDialog(context, msg);
    }

    @Override
    protected void hideProgressDialog() {
        super.hideProgressDialog();
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
