package com.example.user.loginwhithfb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.loginwhithfb.model.UsersInfoTable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by POSTER on 27.06.2017.
 */

public class RegistrationFragment extends Fragment{
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

    private Unbinder unbinder;
    private FirebaseAuth mAuth;
    public ProgressDialog mProgressDialog;

    private FirebaseDatabase database;
    private DatabaseReference reference;

    public static final String USER_INFO_TABLE = "UserLoginInfoTable";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registaration, container, false);
        unbinder = ButterKnife.bind(this, view);
        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference(USER_INFO_TABLE);

        return view;
    }

    @OnClick({R.id.reg_back_btn, R.id.reg_next_btn})
    public void registrationAction(Button button){
        if (button.getId() == R.id.reg_next_btn){
            submitForm();
        }else {
            Toast.makeText(getContext(), "cancel!", Toast.LENGTH_SHORT).show();
        }
    }

    private void createAccount(String email, String password) {
        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(LoginFragment.TAG, "createUserWithEmail:success");
                            addUser();
                            Toast.makeText(getContext(), "Збережено", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), NavigationDrawerActivity.class));
                        } else {
                            Log.w(LoginFragment.TAG, "createUserWithEmail:failure", task.getException().fillInStackTrace());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void addUser(){
        String id = reference.push().getKey();
        UsersInfoTable usersInfoTable = new UsersInfoTable(
                name.getText().toString(), lastName.getText().toString(), surName.getText().toString(),
                "photo_url", Integer.valueOf(number.getText().toString()), email.getText().toString(),
                mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().isEmailVerified()
        );
        Map<String, Object> userLoginInfo = usersInfoTable.toMap();
        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put(id, userLoginInfo);
        reference.updateChildren(userAttributes);
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void submitForm() {
        if (!validateName()) {
            return;
        }
        if (!validateLastName()) {
            return;
        }
        if (!validateLastName()) {
            return;
        }
        if (!validateSurName()) {
            return;
        }
        if (!validateMobileNumber()){
            return;
        }
        if (!validateEmail()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }
        createAccount(email.getText().toString(), password.getText().toString());
    }

    private boolean validateEmail() {
        String curEmail = email.getText().toString().trim();
        if (curEmail.isEmpty() || !isValidEmail(curEmail)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(email);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }
        return true;
    }

    private static boolean isValidEmail(String eMail) {
        return !TextUtils.isEmpty(eMail) && android.util.Patterns.EMAIL_ADDRESS.matcher(eMail).matches();
    }

    private boolean validatePassword() {
        if (password.getText().toString().trim().isEmpty()){
            inputLayoutRepPass.setErrorEnabled(false);
            inputLayoutPass.setError(getString(R.string.err_msg_password));
            requestFocus(password);
            return false;
        }else if (repeatPass.getText().toString().trim().isEmpty()){
            inputLayoutPass.setErrorEnabled(false);
            inputLayoutRepPass.setError(getString(R.string.err_msg_rep_pass));
            requestFocus(repeatPass);
            return false;
        }else if (!password.getText().toString().equals(repeatPass.getText().toString())){
            inputLayoutRepPass.setErrorEnabled(false);
            password.setText("");
            repeatPass.setText("");
            requestFocus(password);
            inputLayoutPass.setError(getString(R.string.err_msg_check_pass));
            return false;
        }else {
            inputLayoutPass.setErrorEnabled(false);
            inputLayoutRepPass.setErrorEnabled(false);
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
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
