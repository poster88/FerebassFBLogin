package com.example.user.loginwhithfb.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
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
                ChangePassActivity.super.showToast(ChangePassActivity.this, "Reathenticate  fail! " + task.getException());
                ChangePassActivity.super.hideProgressDialog();
            }
        }
    };
    private OnCompleteListener<Void> onCompleteListenerChangePass = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
                ChangePassActivity.super.showToast(ChangePassActivity.this, "Password updated! " + task.getException().getMessage());
                //user.reload();
            }else{
                ChangePassActivity.super.showToast(ChangePassActivity.this, "Fail update password! " + task.getException().getMessage());
            }
            ChangePassActivity.super.hideProgressDialog();
        }
    };
    private OnCompleteListener onCompleteListenerResPasByEmail = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()) {
                Snackbar.make(findViewById(R.id.activity_change_pass), "Email sent!", Snackbar.LENGTH_LONG).show();
            } else {
                Log.d("ERROR SEND PASS", " " + task.getException());
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.setActivityForBinder(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
            return true;
        }
        if (id == R.id.accept_changes_menu_btn){
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
        //добавити діалогове вікно
        super.auth.sendPasswordResetEmail(super.user.getEmail())
                .addOnCompleteListener(onCompleteListenerResPasByEmail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_accept_changes, menu);
        return true;
    }

    /*private boolean validatePassword(String newPass, String repPass) {
        //TODO: перемістити в баз клас валідацію
        if (newPass.trim().isEmpty()){
            inputLayoutRepNewPass.setErrorEnabled(false);
            inputLayoutNewPass.setError(getString(R.string.err_msg_password));
            requestFocus(setNewPassword);
            return false;
        }
        if (repPass.trim().isEmpty()){
            inputLayoutNewPass.setErrorEnabled(false);
            inputLayoutRepNewPass.setError(getString(R.string.err_msg_password));
            requestFocus(setRepPassword);
            return false;
        }
        if (newPass.trim().length() < 6){
            inputLayoutRepNewPass.setErrorEnabled(false);
            inputLayoutNewPass.setError(getString(R.string.err_msg_password_length));
            requestFocus(setNewPassword);
            return false;
        }
        if (repPass.trim().length() < 6){
            inputLayoutNewPass.setErrorEnabled(false);
            inputLayoutRepNewPass.setError(getString(R.string.err_msg_password_length));
            requestFocus(setRepPassword);
            return false;
        }
        if (!newPass.equals(repPass)) {
            inputLayoutRepNewPass.setErrorEnabled(false);
            setNewPassword.setText("");
            setRepPassword.setText("");
            requestFocus(setNewPassword);
            inputLayoutNewPass.setError(getString(R.string.err_msg_check_pass));
            return false;
        }
        inputLayoutNewPass.setErrorEnabled(false);
        inputLayoutRepNewPass.setErrorEnabled(false);
        return true;
    }*/
}
