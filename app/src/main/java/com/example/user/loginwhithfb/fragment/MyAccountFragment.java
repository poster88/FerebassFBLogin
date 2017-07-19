package com.example.user.loginwhithfb.fragment;

import android.Manifest;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by POSTER on 24.06.2017.
 */

public class MyAccountFragment extends BaseFragment {
    @BindView(R.id.acc_user_photo) ImageView userImg;
    @BindView(R.id.email_verify_status_img) ImageView verEmailStatusImg;

    private Unbinder unbinder;
    private Uri photoUri;
    private String photoUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        unbinder = ButterKnife.bind(this, view);
        super.storageRef = FirebaseStorage.getInstance().getReference(USERS_IMAGES);
        checkCurUser(super.user);
        return view;
    }

    private void checkCurUser(FirebaseUser user) {
        if (!user.isAnonymous()){
            loadUserPhoto();
        }
        if (user.isEmailVerified()){
            verEmailStatusImg.setImageResource(R.drawable.ic_fiber_manual_record_green_24dp);
        }
    }

    private void loadUserPhoto() {
        Glide.with(this).load(user.getPhotoUrl()).crossFade().thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(getContext()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(userImg);
    }

    @OnClick(R.id.verify_email_btn)
    public void verifyEmail(){
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    user.reload();
                    //TODO: update user: додати в базу дані!
                }else {
                    Toast.makeText(getContext(), "Failed verify email : " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @OnClick({R.id.company_info, R.id.user_telephone, R.id.change_user_pass_btn, R.id.company_search})
    public void pickActionBtn(TextView textView){
        if (!user.isAnonymous()){
            pickActivity(textView.getId());
        }
    }

    private void pickActivity(int id){
        if (id == R.id.company_info){
            startActivity(new Intent(getContext(), CompanyInfoActivity.class));
        }else if (id == R.id.user_telephone){
            startActivity(new Intent(getContext(), ChangeNumberActivity.class));
        }else if (id == R.id.change_user_pass_btn){
            startActivity(new Intent(getContext(), ChangePassActivity.class));
        }else {
            startActivity(new Intent(getContext(), SearchCompanyActivity.class));
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
            Glide.with(this).load(photoUri).crossFade().thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(getContext()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(userImg);
            //TODO: винести в окремий потік?
            savePhotoInStorage();
        }
    }

    private void savePhotoInStorage() {
        StorageReference onlineStoragePhotoRef = storageRef.child(photoUri.getLastPathSegment());
        onlineStoragePhotoRef.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                photoUrl = taskSnapshot.getDownloadUrl().toString();
                UploadPhotoModel model = new UploadPhotoModel(user.getUid(), photoUrl);
                database.getReference("UsersPhoto").push().setValue(model);
                changeUserProfile(photoUrl);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Fail to add storage", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeUserProfile(String url) {
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse(url)).build();
        user.updateProfile(profileChangeRequest).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Fail to update user profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    protected void showProgressDialog() {
        super.showProgressDialog();
    }

    @Override
    protected void hideProgressDialog() {
        super.hideProgressDialog();
    }
}


