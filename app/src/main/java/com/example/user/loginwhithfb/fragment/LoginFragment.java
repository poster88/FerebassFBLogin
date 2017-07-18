package com.example.user.loginwhithfb.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.activity.NavigationDrawerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by POSTER on 21.06.2017.
 */

public class LoginFragment extends BaseFragment{
    @BindView(R.id.emailEdit) EditText emailEdit;
    @BindView(R.id.passEdit) EditText passEdit;
    @BindView(R.id.currentUser) TextView currentUser;

    private Unbinder unbinder;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, view);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        return view;
    }

    @OnClick({R.id.signInBtn, R.id.registrationBtn})
    public void pickAction(TextView textView){
        if (textView.getId() == R.id.signInBtn){
            signIn(emailEdit.getText().toString(), passEdit.getText().toString());
        }else {
            getFragmentManager().beginTransaction().replace(R.id.container, new RegistrationFragment()).commit();
        }
    }

    @OnClick(R.id.skipImgBtn)
    public void anonymouslySingIn(){
        auth.signInAnonymously().addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()){
                    Toast.makeText(getContext(), "Exception : " + task.getException(), Toast.LENGTH_SHORT).show();
                }else {
                    getActivity().startActivity(new Intent(getContext(), NavigationDrawerActivity.class));
                }
            }
        });
    }

    @OnClick(R.id.sentPassOnEmail)
    public void sendPassResetEmail(){
        String emailAddress = auth.getCurrentUser().getEmail();
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(getView().findViewById(R.id.activity_change_pass), "Email sent!", Snackbar.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getContext(), "Fail to sent : " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn(String email, final String password) {
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            getActivity().startActivity(new Intent(getContext(), NavigationDrawerActivity.class));
                        } else {
                            Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;
        String email = emailEdit.getText().toString();
        String password = emailEdit.getText().toString();

        if (TextUtils.isEmpty(email)) {
            emailEdit.setError("Required");
            valid = false;
        } else {
            emailEdit.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            emailEdit.setError("Required");
            valid = false;
        } else {
            emailEdit.setError(null);
        }
        return valid;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
