package com.example.user.loginwhithfb.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.user.loginwhithfb.BaseAlertDialog;
import com.example.user.loginwhithfb.activity.ChangeNumberActivity;
import com.example.user.loginwhithfb.activity.ChangePassActivity;
import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.activity.CompanyInfoActivity;
import com.example.user.loginwhithfb.activity.SearchCompanyActivity;
import com.example.user.loginwhithfb.model.UploadPhotoModel;
import com.example.user.loginwhithfb.other.CircleTransform;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by POSTER on 24.06.2017.
 */

public class MyAccountFragment extends BaseFragment {
    @BindView(R.id.acc_user_photo) ImageView userImg;
    @BindView(R.id.email_verify_status_img) ImageView verEmailStatusImg;

    private Uri photoUri;
    private String photoUrl;

    private OnCompleteListener onCompleteListenerSentEmailVerify = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()){
                MyAccountFragment.super.user.reload();
            }else {
                MyAccountFragment.super.showToast(getContext(), "Failed verify email : ");
            }
        }
    };
    private OnSuccessListener<UploadTask.TaskSnapshot> onSuccessListenerAddPhoto = new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            photoUrl = taskSnapshot.getDownloadUrl().toString();
            UploadPhotoModel model = new UploadPhotoModel(user.getUid(), photoUrl);
            MyAccountFragment.super.database.getReference(REF_USER_PHOTO).push().setValue(model);
            changeUserProfile(photoUrl);
        }
    };
    private OnFailureListener onFailureListenerProfileChange = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            MyAccountFragment.super.showToast(getContext(), "Fail to update user profile");
        }
    };
    private OnFailureListener onFailureListenerAddPhoto = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            MyAccountFragment.super.showToast(getContext(), "Fail to add storage");
        }
    };
    private DialogInterface.OnClickListener posBtnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            MyAccountFragment.super.user.sendEmailVerification()
                    .addOnCompleteListener(onCompleteListenerSentEmailVerify);
            MyAccountFragment.super.showToast(getContext(), "Email was sent");
        }
    };
    private DialogInterface.OnClickListener negBtnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        setFragmentForBinder(this, view);
        super.storageRef = FirebaseStorage.getInstance().getReference(USERS_IMAGES);
        checkCurUser(super.user);
        return view;
    }

    private void checkCurUser(FirebaseUser user) {
        if (!user.isAnonymous()){
            loadUserPhoto(user.getPhotoUrl(), userImg);
            if (user.isEmailVerified()){
                verEmailStatusImg.setImageResource(R.drawable.ic_fiber_manual_record_green_24dp);
            }
        }
    }

    private void loadUserPhoto(Uri uri, ImageView view) {
        Glide.with(this).load(uri).crossFade().thumbnail(0.5f).bitmapTransform(new CircleTransform(getContext()))
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(view);
    }

    @OnClick(R.id.verify_email_btn)
    public void verifyEmail(){
        if (!super.user.isEmailVerified()){
            showAlertDialog("Email verification", "Do you want to verify your email?",
                    android.R.drawable.ic_dialog_alert, false, "Send", "Cancel");
            return;
        }
        MyAccountFragment.super.showToast(getContext(), "Your email already verified");
    }

    private void showAlertDialog(String title, String message, int icon, boolean cancelable, String positiveBtnTitle, String negativeBtnTitle) {
        AlertDialog.Builder ab = new AlertDialog.Builder(getContext());
        ab.setTitle(title);
        ab.setMessage(message);
        ab.setIcon(icon);
        ab.setCancelable(cancelable);
        ab.setPositiveButton(positiveBtnTitle, posBtnClickListener);
        ab.setNegativeButton(negativeBtnTitle, negBtnClickListener);
        ab.show();
    }

    @OnClick({R.id.company_info, R.id.user_telephone, R.id.change_user_pass_btn, R.id.company_search})
    public void pickActionBtn(TextView textView){
        if (!super.user.isAnonymous()){
            pickActivity(textView.getId());
        }
    }

    private void pickActivity(int id){
        if (id == R.id.company_info){
            MyAccountFragment.super.startCurActivity(getContext(), CompanyInfoActivity.class);
        }else if (id == R.id.user_telephone){
            MyAccountFragment.super.startCurActivity(getContext(), ChangeNumberActivity.class);
        }else if (id == R.id.change_user_pass_btn){
            MyAccountFragment.super.startCurActivity(getContext(), ChangePassActivity.class);
        }else {
            MyAccountFragment.super.startCurActivity(getContext(), SearchCompanyActivity.class);
        }
    }

    @OnClick(R.id.acc_change_photo)
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
        StorageReference onlineStoragePhotoRef = super.storageRef.child(photoUri.getLastPathSegment());
        onlineStoragePhotoRef.putFile(photoUri)
                .addOnSuccessListener(onSuccessListenerAddPhoto)
                .addOnFailureListener(onFailureListenerAddPhoto);
    }

    private void changeUserProfile(String url) {
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse(url)).build();
        super.user.updateProfile(profileChangeRequest).addOnFailureListener(onFailureListenerProfileChange);
    }
}


