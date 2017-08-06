package com.example.user.loginwhithfb.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.user.loginwhithfb.activity.BaseActivity;
import com.example.user.loginwhithfb.activity.SearchCompanyActivity;
import com.example.user.loginwhithfb.event.UpdateCompanyUI;
import com.example.user.loginwhithfb.lisntener.MyValueEventListener;
import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.activity.ChangeNumberActivity;
import com.example.user.loginwhithfb.activity.ChangePassActivity;
import com.example.user.loginwhithfb.activity.ChangePersonalDataActivity;
import com.example.user.loginwhithfb.activity.LoginActivity;
import com.example.user.loginwhithfb.activity.NavigationDrawerActivity;
import com.example.user.loginwhithfb.activity.RegistrationActivity;
import com.example.user.loginwhithfb.event.UpdateUIEvent;
import com.example.user.loginwhithfb.eventbus.BusProvider;
import com.example.user.loginwhithfb.model.CompaniesInfoTable;
import com.example.user.loginwhithfb.model.UserLoginInfoTable;
import com.example.user.loginwhithfb.other.CircleTransform;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by POSTER on 24.06.2017.
 */

public class MyAccountFragment extends BaseFragment {
    @BindView(R.id.acc_user_photo) ImageView userImg;
    @BindView(R.id.acc_user_name) TextView userName;
    @BindView(R.id.acc_user_last_name) TextView userLastName;
    @BindView(R.id.acc_user_surname) TextView userSurname;
    @BindView(R.id.acc_user_telephone) TextView userNumber;
    @BindView(R.id.acc_user_email) TextView userEmail;
    @BindView(R.id.email_verify_status_label) TextView emailStatus;
    @BindView(R.id.acc_img_check_em_status) ImageView emailStatusImg;

    private EditText passEdit;
    private TextInputLayout inputLayoutEmail;
    private EditText setEmail;
    private DatabaseReference refUserInfTable;
    private View view;
    private View dialog;
    private View companyView;
    private String tempUid;
    private String tempEmail;
    private Uri photoUri;
    private String photoUrl;
    private LinearLayout infoCompContainer;
    private Inflater inflater;

    private OnCompleteListener onCompleteListenerSentEmailVerify = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()){
                MyAccountFragment.super.user.reload();
                MyAccountFragment.super.showToast(getContext(), "Email was sent.");
            }else {
                MyAccountFragment.super.showToast(getContext(), "Failed verify email.");
            }
        }
    };
    private OnSuccessListener<UploadTask.TaskSnapshot> onSuccessListenerAddPhoto = new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
            photoUrl = taskSnapshot.getDownloadUrl().toString();
            FirebaseStorage.getInstance().getReferenceFromUrl(NavigationDrawerActivity.userModel.getPhotoUrl())
                    .delete().addOnCompleteListener(deleteListener);
        }
    };
    private OnCompleteListener<Void> deleteListener = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()){
                NavigationDrawerActivity.userModel.setPhotoUrl(photoUrl);
                BusProvider.getInstance().post(new UpdateUIEvent());
                refUserInfTable.addListenerForSingleValueEvent(onUserKeyFinder);
            }else {
                MyAccountFragment.super.showToast(getContext(), task.getException().getMessage());
            }
        }
    };
    private MyValueEventListener onUserKeyFinder = new MyValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot data: dataSnapshot.getChildren()){
                if (MyAccountFragment.super.user.getUid().equals(data.getValue(UserLoginInfoTable.class).getuID())){
                    updateUserProfile(data.getKey());
                }
            }
        }
    };
    private void updateUserProfile(String userKey) {
        refUserInfTable.child(userKey).setValue(BaseActivity.userModel);
    }
    private OnFailureListener onFailureListenerAddPhoto = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            MyAccountFragment.super.showToast(getContext(), "Fail to add storage. " + e.getMessage());
        }
    };
    private DialogInterface.OnClickListener posBtnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            MyAccountFragment.super.user.sendEmailVerification().addOnCompleteListener(onCompleteListenerSentEmailVerify);
        }
    };
    private DialogInterface.OnClickListener negBtnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };
    private DialogInterface.OnClickListener onRegistrationListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            MyAccountFragment.super.startCurActivity(getContext(), RegistrationActivity.class);
        }
    };
    private OnCompleteListener deleteUserListener = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            MyAccountFragment.super.hideProgressDialog();
            if (task.isSuccessful()){
                MyAccountFragment.super.showToast(getContext(), "User account deleted.");
                deleteUserFromDB();
                MyAccountFragment.super.startCurActivity(getContext(), LoginActivity.class);
            }else {
                MyAccountFragment.super.showAlertDialogOneBtn("Fail to delete account!", task.getException().getMessage(), "OK", negBtnClickListener);
            }
        }
    };
    private MyValueEventListener deleteUserAccListener = new MyValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot data: dataSnapshot.getChildren()){
                refUserInfTable.child(data.getKey()).removeValue();
            }
        }
    };
    private void deleteUserFromDB() {
        Query query = refUserInfTable.orderByChild("uID").equalTo(tempUid);
        query.addListenerForSingleValueEvent(deleteUserAccListener);
        //TODO: delete all users ref link
    }
    private OnCompleteListener credentialComplete = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()){
                MyAccountFragment.super.user.delete().addOnCompleteListener(deleteUserListener);
            }else {
                MyAccountFragment.super.hideProgressDialog();
                MyAccountFragment.super.showAlertDialogOneBtn("Password check fail!", task.getException().getMessage(), "OK", negBtnClickListener);
            }
        }
    };
    private DialogInterface.OnClickListener setCheckCredential = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (passEdit.getText().length() != 0){
                MyAccountFragment.super.hideSoftKeyboard(getActivity());
                reAuthUser(MyAccountFragment.super.user.getEmail(), passEdit.getText().toString(), credentialComplete);
            }else {
                MyAccountFragment.super.showAlertDialogOneBtn("Password verify fail!", "Password field is empty", "OK", negBtnClickListener);
            }
        }
    };

    private DialogInterface.OnClickListener setCheckCredentialEmail = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (passEdit.getText().length() != 0){
                MyAccountFragment.super.hideSoftKeyboard(getActivity());
                reAuthUser(MyAccountFragment.super.user.getEmail(), passEdit.getText().toString(), credentialCompleteEmail);
            }else {
                MyAccountFragment.super.showAlertDialogOneBtn("Password verify fail!", "Password field is empty", "OK", negBtnClickListener);
            }
        }
    };
    private OnCompleteListener credentialCompleteEmail = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()){
                showDialogChangeEmail();
            }else {
                MyAccountFragment.super.hideProgressDialog();
                MyAccountFragment.super.showAlertDialogOneBtn("Password check fail!", task.getException().getMessage(), "OK", negBtnClickListener);
            }
        }
    };

    private void showDialogChangeEmail() {
        dialog = this.getLayoutInflater(getArguments()).inflate(R.layout.email_layout, null);
        setEmail = ButterKnife.findById(dialog, R.id.change_email);
        inputLayoutEmail = ButterKnife.findById(dialog, R.id.layout_change_email);
        MyAccountFragment.super.hideSoftKeyboard(getActivity());
        setEmail.setText(BaseActivity.userModel.getEmail());
        super.showAlertDialogWithView("Change your email", dialog, android.R.drawable.ic_dialog_alert,
                false, "Check", "Cancel", onChangeEmail, negBtnClickListener);
    }

    private DialogInterface.OnClickListener onChangeEmail = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            tempEmail = setEmail.getText().toString();
            checkEmail();
        }
    };

    private void checkEmail() {
        if (MyAccountFragment.super.isValidEmail(setEmail, inputLayoutEmail)){
            if (!tempEmail.equals(MyAccountFragment.super.user.getEmail())){
                MyAccountFragment.super.showProgressDialog("Changing ...");
                MyAccountFragment.super.user.updateEmail(setEmail.getText().toString()).addOnCompleteListener(onUpdateEmail);
                return;
            }
        }
        hideSoftKeyboard(getActivity());
        super.showToast(getContext(), "Check up your email.");
    }

    private OnCompleteListener onUpdateEmail = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            MyAccountFragment.super.hideProgressDialog();
            if (task.isSuccessful()){
                BaseActivity.userModel.setEmail(tempEmail);
                changeEmailInDB();
            }else {
                showToast(getContext(), "Failed to update Email. " + task.getException().getMessage());
            }
        }
    };

    private void changeEmailInDB() {
        BaseActivity.userRef.addListenerForSingleValueEvent(onChangeEmailListener);
        super.showToast(getContext(), "Email is updated!");
    }

    private MyValueEventListener onChangeEmailListener = new MyValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot data: dataSnapshot.getChildren()){
                updateUserProfile(data.getKey());
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_account, container, false);
        setFragmentForBinder(this, view);
        setHasOptionsMenu(true);
        refUserInfTable = super.database.getReference(USER_INFO_TABLE);
        return view;
    }

    @Subscribe
    public void updateUI(UpdateUIEvent event){
        if (NavigationDrawerActivity.userModel != null){
            userName.setText(NavigationDrawerActivity.userModel.getName());
            userLastName.setText(NavigationDrawerActivity.userModel.getLastName());
            userSurname.setText(NavigationDrawerActivity.userModel.getSurname());
            userNumber.setText(String.valueOf(NavigationDrawerActivity.userModel.getMobileNumber()));
            userEmail.setText(NavigationDrawerActivity.userModel.getEmail());
            checkCurUser(super.user);
        }
    }

    @Produce
    public UpdateUIEvent update(){
        return new UpdateUIEvent();
    }

    @Subscribe
    public void updateCompanyUserInfo(UpdateCompanyUI event){
        RelativeLayout labelContainer = (RelativeLayout)view.findViewById(R.id.acc_company_label_container);
        RelativeLayout infoCompanyLayout = (RelativeLayout)view.findViewById(R.id.layout_info_company);
        if (NavigationDrawerActivity.companiesInfoTable != null){
            if (labelContainer.getVisibility() == View.VISIBLE){
                labelContainer.setVisibility(View.GONE);
                infoCompanyLayout.setVisibility(View.VISIBLE);
            }
            initCompanyWidgetsData(infoCompanyLayout);
        }else {
            labelContainer.setVisibility(View.VISIBLE);
            infoCompanyLayout.setVisibility(View.GONE);
        }
    }

    private void initCompanyWidgetsData(View view) {
        ((TextView)view.findViewById(R.id.info_company_name)).setText(NavigationDrawerActivity.companiesInfoTable.getCompanyName());
        ((TextView)view.findViewById(R.id.info_company_desc)).setText(NavigationDrawerActivity.companiesInfoTable.getCompanyDescr());
        ((TextView)view.findViewById(R.id.info_key_product)).setText(createStringText(NavigationDrawerActivity.companiesInfoTable.getCompanyProducts()));
        ((TextView)view.findViewById(R.id.info_my_position)).setText(getUserPositionInCompany(NavigationDrawerActivity.companiesInfoTable.getPositions()));
        if (!NavigationDrawerActivity.companiesInfoTable.getCompanyLogoUri().toString().equals("default_uri")){
            loadImage(Uri.parse(NavigationDrawerActivity.companiesInfoTable.getCompanyLogoUri()), ((ImageView)view.findViewById(R.id.info_company_logo_img)));
        }
    }

    @Produce
    public UpdateCompanyUI updateCompanyUI(){
        return new UpdateCompanyUI();
    }

    private String getUserPositionInCompany(Object positions){
        String position = "Request in progress";
        Map<String, Object> data = (Map<String, Object>) positions;
        for (Map.Entry<String, Object> pos: data.entrySet()) {
            if (pos.getValue().toString().contains(user.getUid())){
                position = pos.getKey();
                break;
            }
        }
        return position;
    }

    private void checkCurUser(FirebaseUser user) {
        if (!user.isAnonymous()){
            if (!NavigationDrawerActivity.userModel.getPhotoUrl().equals("default_uri")){
                loadUserPhoto(Uri.parse(NavigationDrawerActivity.userModel.getPhotoUrl()), userImg);
            }
            if (user.isEmailVerified()){
                emailStatusImg.setImageResource(R.drawable.mail);
            }
        }
    }

    private void loadUserPhoto(Uri uri, ImageView view) {
        Glide.with(this).load(uri).crossFade().thumbnail(0.5f).bitmapTransform(new CircleTransform(getContext()))
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(view);
    }

    private void loadImage(Uri uri, ImageView view){
        Glide.with(this).load(uri).crossFade().thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(view);
    }

    private String createStringText(Object o){
        ArrayList<String> keys = ((ArrayList<String>) o);
        StringBuilder builder = new StringBuilder();
        for (String value : keys) builder.append(value).append(", ");
        if (keys.size()> 0) builder.deleteCharAt(builder.length() - 2);
        return builder.toString();
    }

    private void verifyEmail(){
        if (!super.user.isEmailVerified()){
            super.showAlertDialog("Email verification", "Do you want to verify your email?", android.R.drawable.ic_dialog_alert,
                    false, "Send", "Cancel", posBtnClickListener, negBtnClickListener);
            return;
        }
        MyAccountFragment.super.showToast(getContext(), "Your email already verified");
    }

    @OnClick({R.id.acc_card_view_person, R.id.acc_card_view_person_number, R.id.acc_card_view_person_email,
            R.id.acc_card_view_mail_check, R.id.acc_card_view_person_company})
    public void pickActionBtn(CardView view){
        if (!super.user.isAnonymous()){
            pickActivity(view.getId());
            return;
        }
        super.showAlertDialog("Information", "Pls, register first.", android.R.drawable.ic_menu_info_details,
                false, "Register now", "Cancel", onRegistrationListener, negBtnClickListener);
    }

    private void pickActivity(int id){
        if (id == R.id.acc_card_view_person){
            super.startCurActivity(getContext(), ChangePersonalDataActivity.class);
        }else if (id == R.id.acc_card_view_person_number){
            super.startCurActivity(getContext(), ChangeNumberActivity.class);
        }else if (id == R.id.acc_card_view_person_email){
            changeUserEmail();
        }else if (id == R.id.acc_card_view_mail_check){
            verifyEmail();
        }else if (id == R.id.acc_card_view_person_company){
            super.startCurActivity(getContext(), SearchCompanyActivity.class);
        }
    }

    @OnClick(R.id.acc_user_photo)
    public void editPhoto(){
        requestPermissions();
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
            }else {
                openFilePicker();
            }
        }else {
            openFilePicker();
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openFilePicker();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK && data != null){
            photoUri = data.getData();
            loadUserPhoto(photoUri, userImg);
            savePhotoInStorage();
        }
    }

    private void savePhotoInStorage() {
        StorageReference onlineStoragePhotoRef = super.refUsersPhoto.child(photoUri.getLastPathSegment());
        onlineStoragePhotoRef.putFile(photoUri)
                .addOnSuccessListener(onSuccessListenerAddPhoto)
                .addOnFailureListener(onFailureListenerAddPhoto);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_account, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!super.user.isAnonymous()){
            if (item.getItemId() == R.id.action_change_password){
                MyAccountFragment.super.startCurActivity(getContext(), ChangePassActivity.class);
            }else if (item.getItemId() == R.id.action_delete_account){
                deleteUser();
            }
        }else {
            MyAccountFragment.super.showToast(getContext(), "Please create a user, to use this menu");
        }
        return true;
    }

    private void deleteUser() {
        tempUid = MyAccountFragment.super.user.getUid();
        dialog = this.getLayoutInflater(getArguments()).inflate(R.layout.password_layout, null);
        passEdit = ButterKnife.findById(dialog, R.id.set_line_password);
        super.showAlertDialogWithView("Check up your credentials", dialog, android.R.drawable.ic_dialog_alert,
                false, "Check", "Cancel", setCheckCredential, negBtnClickListener);
    }

    private void changeUserEmail() {
        dialog = this.getLayoutInflater(getArguments()).inflate(R.layout.password_layout, null);
        passEdit = ButterKnife.findById(dialog, R.id.set_line_password);
        super.showAlertDialogWithView("Check up your credentials", dialog, android.R.drawable.ic_dialog_alert,
                false, "Check", "Cancel", setCheckCredentialEmail, negBtnClickListener);
    }

    private void reAuthUser(String email, String pass, OnCompleteListener listener){
        AuthCredential credential = EmailAuthProvider.getCredential(email, pass);
        super.user.reauthenticate(credential).addOnCompleteListener(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (super.user != null && !user.isAnonymous()){
            BusProvider.getInstance().register(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (user != null && !user.isAnonymous()){
            BusProvider.getInstance().unregister(this);
        }
    }
}


