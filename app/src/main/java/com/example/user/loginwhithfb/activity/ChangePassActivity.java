package com.example.user.loginwhithfb.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.user.loginwhithfb.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by POSTER on 04.07.2017.
 */

public class ChangePassActivity extends BaseActivity{
    @BindView(R.id.set_new_password) EditText setNewPassword;
    @BindView(R.id.set_acc_rep_pass) EditText setRepPassword;
    @BindView(R.id.set_old_password) EditText setOldPassword;
    @BindView(R.id.layout_acc_password) TextInputLayout inputLayoutNewPass;
    @BindView(R.id.layout_acc_rep_pass) TextInputLayout inputLayoutRepNewPass;
    @BindView(R.id.layout_acc_old_password) TextInputLayout inputLayoutOldPass;

    private OnCompleteListener<Void> onCompleteListenerReAuth = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()){
                updateUserPass();
            }else {
                ChangePassActivity.super.showToast(ChangePassActivity.this, "Reathenticate  fail! " + task.getException().getMessage());
                ChangePassActivity.super.hideProgressDialog();
            }
        }
    };
    private OnCompleteListener onCompleteListenerChangePass = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task task) {
            ChangePassActivity.super.hideProgressDialog();
            if (task.isSuccessful()) {
                user.reload();
                ChangePassActivity.super.showToast(getBaseContext(), "Password updated!");
                finish();
            }else{
                ChangePassActivity.super.showToast(ChangePassActivity.this, "Fail update password! " + task.getException().getMessage());
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        super.setActivityForBinder(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        if (item.getItemId() == R.id.accept_changes_menu_btn){
            checkCredentials();
        }
        return true;
    }

    private void checkCredentials() {
        if (super.checkLengthPass(setOldPassword, inputLayoutOldPass)){
            if (super.isValidPassword(setNewPassword, inputLayoutNewPass, setRepPassword, inputLayoutRepNewPass)){
                super.showProgressDialog("Wait...");
                AuthCredential credential = EmailAuthProvider.getCredential(super.user.getEmail(), setOldPassword.getText().toString());
                super.user.reauthenticate(credential).addOnCompleteListener(onCompleteListenerReAuth);
            }
        }
    }

    private void updateUserPass() {
        super.user.updatePassword(setNewPassword.getText().toString()).addOnCompleteListener(onCompleteListenerChangePass);
    }

    @OnClick(R.id.forgotPassLayout)
    public void sendPassResetEmail(){
        super.startCurActivity(this, SentPassToEmailActivity.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_accept_changes, menu);
        return true;
    }
}
