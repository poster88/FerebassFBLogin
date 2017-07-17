package com.example.user.loginwhithfb.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by POSTER on 21.06.2017.
 */

public class LoginFragment extends Fragment{
    @BindView(R.id.emailEdit) EditText emailEdit;
    @BindView(R.id.passEdit) EditText passEdit;
    @BindView(R.id.currentUser) TextView currentUser;

    private Unbinder unbinder;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    public ProgressDialog mProgressDialog;
    public static final String TAG = "MY_LOGS";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, view);
        auth = FirebaseAuth.getInstance();

        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        if (user1 != null){
            Log.d(TAG, "user is already logged");
        }else {
            Log.d(TAG, "no user");
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Log.d(TAG, "user loggin in: " + '\n' +
                            "user name : " + user.getDisplayName() + '\n' +
                            "user email : " + user.getEmail() + '\n' +
                            "user photo url : " + user.getPhotoUrl() + '\n' +
                            "user provider : " + user.getProviderId() + '\n' +
                            "user Uid : " + user.getUid() + '\n' +
                            "user email verified status : " + user.isEmailVerified()
                    );
                    if (!user.isAnonymous()){
                        currentUser.setText("Welcome " + user.getEmail());
                    }
                }else {
                    Log.d(TAG, "user loggin out.");
                    currentUser.setText("");
                }
            }
        };

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
                    Log.d(TAG, "Exeption : " + task.getException());
                }else {
                    Log.d(TAG, "signInAnonymously:success");
                    getActivity().startActivity(new Intent(getActivity(), NavigationDrawerActivity.class));
                }
            }
        });
    }

    @OnClick(R.id.sentPassOnEmail)
    public void sendPassResetEmail(){
        //TODO: add validate to send pass on email!
        String emailAddress = auth.getCurrentUser().getEmail();
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(getView().findViewById(R.id.activity_change_pass), "Email sent!", Snackbar.LENGTH_LONG).show();
                        }else{
                            Log.d("ERROR SEND PASS", " " + task.getException());
                        }
                    }
                });
    }

    private void signIn(String email, final String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            getActivity().startActivity(new Intent(getActivity(), NavigationDrawerActivity.class));
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }


    private boolean validateForm() {
        boolean valid = true;

        String email = emailEdit.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEdit.setError("Required.");
            valid = false;
        } else {
            emailEdit.setError(null);
        }

        String password = emailEdit.getText().toString();
        if (TextUtils.isEmpty(password)) {
            emailEdit.setError("Required.");
            valid = false;
        } else {
            emailEdit.setError(null);
        }
        return valid;
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

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (auth != null){
            auth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
