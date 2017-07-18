package com.example.user.loginwhithfb.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.model.UserLoginInfoTable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    private static final String USER_INFO_TABLE = "UserLoginInfoTable";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registaration);
        ButterKnife.bind(this);
        reference = database.getReference(USER_INFO_TABLE);
    }

    private void addUser(){
        String id = reference.push().getKey();
        UserLoginInfoTable usersInfoTable = new UserLoginInfoTable(
                name.getText().toString(), lastName.getText().toString(), surName.getText().toString(),
                "photo_url", Integer.valueOf(number.getText().toString()), email.getText().toString(),
                super.auth.getCurrentUser().getUid(), "some id"
        );
        Map<String, Object> userLoginInfo = usersInfoTable.toMap();
        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put(id, userLoginInfo);
        reference.updateChildren(userAttributes);
    }

    private void createAccount(String email, String password) {
        showProgressDialog(this, "Wait...");
        super.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    addUser();
                    Toast.makeText(RegistrationActivity.this, "User account is created", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistrationActivity.this, NavigationDrawerActivity.class));
                }else {
                    Toast.makeText(RegistrationActivity.this, "Failed to create user account", Toast.LENGTH_SHORT).show();
                }
                hideProgressDialog();
            }
        });
    }

    private boolean validateEmail() {
        String curEmail = email.getText().toString().trim();
        if (curEmail.isEmpty() || !isValidEmail(curEmail)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(email);
            return false;
        }
        inputLayoutEmail.setErrorEnabled(false);
        return true;
    }

    private boolean isValidEmail(String eMail) {
        return !TextUtils.isEmpty(eMail) && android.util.Patterns.EMAIL_ADDRESS.matcher(eMail).matches();
    }

    private boolean validatePassword() {
        if (!checkIfEmpty() || !checkLength()){
            return false;
        }
        if (!password.getText().toString().equals(repeatPass.getText().toString())){
            inputLayoutRepPass.setErrorEnabled(false);
            password.setText("");
            repeatPass.setText("");
            requestFocus(password);
            inputLayoutPass.setError(getString(R.string.err_msg_check_pass));
            return false;
        }
        inputLayoutPass.setErrorEnabled(false);
        inputLayoutRepPass.setErrorEnabled(false);
        return true;
    }

    private boolean checkLength() {
        if (password.getText().length() < 6) {
            inputLayoutRepPass.setErrorEnabled(false);
            inputLayoutPass.setError(getString(R.string.err_msg_password_length));
            requestFocus(password);
            return false;
        } else if (repeatPass.getText().length() < 6) {
            inputLayoutPass.setErrorEnabled(false);
            inputLayoutRepPass.setError(getString(R.string.err_msg_password_length));
            requestFocus(repeatPass);
            return false;
        }
        return true;
    }

    private boolean checkIfEmpty() {
        if (password.getText().toString().trim().isEmpty()) {
            inputLayoutRepPass.setErrorEnabled(false);
            inputLayoutPass.setError(getString(R.string.err_msg_password));
            requestFocus(password);
            return false;
        } else if (repeatPass.getText().toString().trim().isEmpty()) {
            inputLayoutPass.setErrorEnabled(false);
            inputLayoutRepPass.setError(getString(R.string.err_msg_rep_pass));
            requestFocus(repeatPass);
            return false;
        }
        return true;
    }

    private boolean validateMobileNumber() {
        if (number.getText().toString().trim().isEmpty()){
            inputLayoutMobNum.setError(getString(R.string.err_msg_mob_number));
            requestFocus(number);
            return false;
        }else {
            inputLayoutMobNum.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateSurName() {
        if (surName.getText().toString().trim().isEmpty()) {
            inputLayoutSurName.setError(getString(R.string.err_msg_sur_name));
            requestFocus(surName);
            return false;
        } else {
            inputLayoutSurName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateLastName() {
        if (lastName.getText().toString().trim().isEmpty()) {
            inputLayoutLastName.setError(getString(R.string.err_msg_last_name));
            requestFocus(lastName);
            return false;
        } else {
            inputLayoutLastName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateName() {
        if (name.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(name);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void submitForm() {
        if (!validateName() || !validateLastName() || !validateSurName() ||!validateMobileNumber() || !validateEmail() || !validatePassword()) {
            return;
        }
        createAccount(email.getText().toString(), password.getText().toString());
    }

    @OnClick({R.id.reg_back_btn, R.id.reg_next_btn})
    public void pickAction(Button button){
        if (button.getId() == R.id.reg_next_btn){
            submitForm();
        }else {
            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void showProgressDialog(Context context, String msg) {
        super.showProgressDialog(context, msg);
    }

    @Override
    protected void hideProgressDialog() {
        super.hideProgressDialog();
    }
}
