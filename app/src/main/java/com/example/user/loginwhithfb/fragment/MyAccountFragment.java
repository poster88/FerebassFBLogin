package com.example.user.loginwhithfb.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by POSTER on 24.06.2017.
 */

public class MyAccountFragment extends Fragment{
    @BindView(R.id.acc_user_photo) ImageView userImg;

    private Unbinder unbinder;
    private FirebaseUser user;
    private StorageReference storageRef;
    private FirebaseDatabase database;

    private ProgressDialog progressDialog;
    private Uri photoUri;
    private String photoUrl;

    private final static String USERS_IMAGES = "users_images";
    private static final int PHOTO_REQUEST = 9002;
    private static final int REQUEST_READ_PERMISSION = 9003;
    private static final int RESULT_OK = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        ImageView verEmailStatusImg = ButterKnife.findById(view, R.id.email_verify_status_img);
        unbinder = ButterKnife.bind(this, view);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference(USERS_IMAGES);
        if (user != null){
            if (user.isAnonymous()){
                userImg.setImageResource(R.drawable.ic_person_black_24dp);
                verEmailStatusImg.setImageResource(R.drawable.ic_fiber_manual_record_reg_24dp);
            }else {
                Glide.with(this).load(user.getPhotoUrl())
                        .crossFade()
                        .thumbnail(0.5f)
                        .bitmapTransform(new CircleTransform(getContext()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(userImg);
                if (!user.isEmailVerified()){
                    verEmailStatusImg.setImageResource(R.drawable.ic_fiber_manual_record_reg_24dp);
                }else {
                    verEmailStatusImg.setImageResource(R.drawable.ic_fiber_manual_record_green_24dp);
                }
            }
        }
        return view;
    }

    @OnClick(R.id.verify_email_btn)
    public void verifyEmail(){
        Log.d("EMAIL VERIFY: ", "entered method");
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d("EMAIL VERIFY: ", "Email send");
                            user.reload();
                            //TODO: update user: додати в базу дані!
                        }else {
                            Log.d("EMAIL VERIFY: ", "Email sending failed. " + task.getException());
                        }
                    }
                });
    }
    @OnClick(R.id.company_info)
    public void showCompanyInfo(){
        Log.d("SHOW COM.INFO: ", "entered method");
        startActivity(new Intent(getActivity(), CompanyInfoActivity.class));
    }

    @OnClick(R.id.user_telephone)
    public void changeUserNumber(){
        startActivity(new Intent(getActivity(), ChangeNumberActivity.class));
    }

    @OnClick(R.id.change_user_pass_btn)
    public void changeUserPass(){
        startActivity(new Intent(getActivity(), ChangePassActivity.class));
    }

    @OnClick(R.id.company_search)
    public void companySearch(){
        startActivity(new Intent(getActivity(), SearchCompanyActivity.class));
    }

    @OnClick(R.id.acc_change_photo)
    public void editPhoto(){
        requestPermissions();
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
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
            }else {
                Toast.makeText(getActivity(), "Cannot pick file from storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK && data != null){
            photoUri = data.getData();
            Glide.with(this).load(photoUri)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(getContext()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(userImg);
            //Glide.with(getActivity()).load(photoUri).into(userImg);
            savePhotoInStorage();
        }
    }

    private void savePhotoInStorage() {
        showProgressDialog();
        StorageReference onlineStoragePhotoRef = storageRef.child(photoUri.getLastPathSegment());
        onlineStoragePhotoRef.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                photoUrl = taskSnapshot.getDownloadUrl().toString();
                UploadPhotoModel model = new UploadPhotoModel(user.getUid(), photoUrl);
                database.getReference("UsersPhoto").push().setValue(model);
                changeUserProfile(photoUrl);
                hideProgressDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();
                Toast.makeText(getContext(), "Fail to add storage", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeUserProfile(String url) {
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(url))
                .build();
        user.updateProfile(profileChangeRequest)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Fail to update user profile", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("loading...");
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}


