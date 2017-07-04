package com.example.user.loginwhithfb;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by POSTER on 04.07.2017.
 */

public class ChangePassActivity extends AppCompatActivity{
    @BindView(R.id.set_new_password) EditText setNewPassword;
    @BindView(R.id.set_acc_rep_pass) EditText setRepPassword;
    @BindView(R.id.set_old_password) EditText setOldPassword;
    @BindView(R.id.layout_acc_password) TextInputLayout inputLayoutNewPass;
    @BindView(R.id.layout_acc_rep_pass) TextInputLayout inputLayoutRepNewPass;
    @BindView(R.id.layout_acc_old_password) TextInputLayout inputLayoutOldPass;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }
        if (id == R.id.change_pass_in_account){
            if (validatePassword(setNewPassword.getText().toString(), setRepPassword.getText().toString())){
                showProgressDialog();
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), setOldPassword.getText().toString());
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("PASS CHANGE: ", "reauthenticate complite");
                                    //TODO : update user pass
                                    user.updatePassword(setNewPassword.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("PASS CHANGE: ", "Пароль пользователя успешно обновлен");
                                                        //TODO : update user pass
                                                        hideProgressDialog();
                                                        Snackbar.make(findViewById(R.id.activity_change_pass), "Password updated!", Snackbar.LENGTH_LONG).show();
                                                        //user.reload();
                                                    }else{
                                                        Log.d("PASS CHANGE: ", "Пароль update fail! " + task.getException());
                                                    }
                                                }
                                            });
                                }else{
                                    Log.d("PASS CHANGE: ", "reauthenticate  fail! " + task.getException());
                                }
                            }
                        });
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.forgotPassLayout)
    public void sendPassResetEmail(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = auth.getCurrentUser().getEmail();
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(findViewById(R.id.activity_change_pass), "Email sent!", Snackbar.LENGTH_LONG).show();
                        }else{
                            Log.d("ERROR SEND PASS", " " + task.getException());
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_change_pass, menu);
        return true;
    }

    private boolean validatePassword(String newPass, String repPass) {
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
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
