package com.example.user.loginwhithfb.activity;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.widget.Button;
import android.widget.EditText;

import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.model.UserLoginInfoTable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by POSTER on 18.07.2017.
 */

public class RegistrationActivity extends BaseActivity{
    @BindView(R.id.reg_name) EditText name;
    @BindView(R.id.reg_last_name) EditText lastName;
    @BindView(R.id.reg_sur_name) EditText surName;
    @BindView(R.id.reg_mob_num) EditText number;
    @BindView(R.id.reg_email) EditText email;
    @BindView(R.id.reg_password) EditText password;
    @BindView(R.id.reg_rep_pass) EditText repeatPass;

    @BindView(R.id.layout_name) TextInputLayout inputLayoutName;
    @BindView(R.id.layout_last_name) TextInputLayout inputLayoutLastName;
    @BindView(R.id.layout_sur_name) TextInputLayout inputLayoutSurName;
    @BindView(R.id.layout_mob_num) TextInputLayout inputLayoutMobNum;
    @BindView(R.id.layout_email) TextInputLayout inputLayoutEmail;
    @BindView(R.id.layout_password) TextInputLayout inputLayoutPass;
    @BindView(R.id.layout_rep_pass) TextInputLayout inputLayoutRepPass;

    private DatabaseReference reference;
    private UserLoginInfoTable usersInfoTable;
    private OnCompleteListener onCompleteListenerCreateUser = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                addUser();
                RegistrationActivity.super.showToast(RegistrationActivity.this, "User account is created");
                RegistrationActivity.super.startCurActivity(RegistrationActivity.this, NavigationDrawerActivity.class);
                finish();
            }else {
                inputLayoutEmail.setError(task.getException().getMessage());
            }
            hideProgressDialog();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registaration);
        super.setActivityForBinder(this);
        reference = super.database.getReference(USER_INFO_TABLE);
    }

    private void addUser(){
        String id = reference.push().getKey();
        setDataToConstructor();
        Map<String, Object> userLoginInfo = usersInfoTable.toMap();
        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put(id, userLoginInfo);
        reference.updateChildren(userAttributes);
        super.updateUserNameAuth(name.getText().toString());
        super.handler.post(super.runnable);
    }

    private void setDataToConstructor() {
        usersInfoTable = new UserLoginInfoTable(
                name.getText().toString(), lastName.getText().toString(), surName.getText().toString(),
                "default_uri", Integer.valueOf(number.getText().toString()), email.getText().toString(),
                super.auth.getCurrentUser().getUid(), "default_uri");
    }

    private void createAccount(String email, String password) {
        super.showProgressDialog("Wait...");
        super.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(onCompleteListenerCreateUser);
    }

    private void submitForm() {
        if (!super.isValidPersonalData(name, inputLayoutName) || !super.isValidPersonalData(lastName, inputLayoutLastName) ||
                !super.isValidPersonalData(surName, inputLayoutSurName) || !super.isValidNumber(number, inputLayoutMobNum) ||
                !super.isValidEmail(email, inputLayoutEmail) || !super.isValidPassword(password, inputLayoutPass, repeatPass, inputLayoutRepPass)) {
            return;
        }
        createAccount(email.getText().toString(), password.getText().toString());
    }

    @OnClick({R.id.reg_back_btn, R.id.reg_next_btn})
    public void pickAction(Button button){
        if (button.getId() == R.id.reg_next_btn){
            submitForm();
        }else {
            RegistrationActivity.super.startCurActivity(RegistrationActivity.this, LoginActivity.class);
            finish();
        }
    }
}
