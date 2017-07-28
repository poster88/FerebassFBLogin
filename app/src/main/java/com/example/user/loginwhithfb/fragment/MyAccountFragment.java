package com.example.user.loginwhithfb.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.user.loginwhithfb.MyValueEventListener;
import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.activity.ChangeNumberActivity;
import com.example.user.loginwhithfb.activity.ChangePassActivity;
import com.example.user.loginwhithfb.activity.ChangePersonalDataActivity;
import com.example.user.loginwhithfb.activity.LoginActivity;
import com.example.user.loginwhithfb.activity.RegistrationActivity;
import com.example.user.loginwhithfb.model.UploadPhotoModel;
import com.example.user.loginwhithfb.model.UserLoginInfoTable;
import com.example.user.loginwhithfb.other.CircleTransform;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by POSTER on 24.06.2017.
 */

public class MyAccountFragment extends BaseFragment {
    @BindView(R.id.acc_user_photo) ImageView userImg;
    @BindView(R.id.email_verify_status_img) ImageView verEmailStatusImg;
    @BindView(R.id.acc_user_name) TextView userName;
    @BindView(R.id.acc_user_last_name) TextView userLastName;
    @BindView(R.id.acc_user_surname) TextView userSurname;
    private EditText passEdit;
    private View dialog;
    TextView textView;
    TextView textViewLastName;

    private DatabaseReference refUserInfTable;
    final String USER_INFO_TABLE = "UserLoginInfoTable";
    private UserLoginInfoTable userModel;
    private Handler handler;
    private View view;
    private String tempUid;

    private MyValueEventListener myValueEventListener = new MyValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot data: dataSnapshot.getChildren()){
                userModel = data.getValue(UserLoginInfoTable.class);
                TextView textView = (TextView)view.findViewById(R.id.acc_user_name);
                textView.setText(userModel.getName());
                //textViewLastName.setText(userModel.getLastName());
                //userName.setText(userModel.getName());
                //userLastName.setText(userModel.getLastName());
                //userSurname.setText(userModel.getSurname());
            }
        }
    };
    private Runnable getData = new Runnable() {
        @Override
        public void run() {

        }
    };

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
        refUserInfTable = super.database.getReference(USER_INFO_TABLE);
        Query query = refUserInfTable.orderByChild("uID").equalTo(tempUid);
        query.addListenerForSingleValueEvent(deleteUserAccListener);
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
                reAuthUser(MyAccountFragment.super.user.getEmail(), passEdit.getText().toString());
            }else {
                MyAccountFragment.super.showAlertDialogOneBtn("Password verify fail!", "Password field is empty", "OK", negBtnClickListener);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_account, container, false);
        setFragmentForBinder(this, view);
        setHasOptionsMenu(true);
        handler = new Handler();
        if (savedInstanceState == null){
            //handler.post(getData);
        }
        //checkCurUser(super.user);

        textView = (TextView)view.findViewById(R.id.acc_user_name);
        textViewLastName = (TextView)view.findViewById(R.id.acc_user_last_name);
        DatabaseReference ref = super.database.getReference(USER_INFO_TABLE);

        refUserInfTable = MyAccountFragment.super.database.getReference(USER_INFO_TABLE);
        Query query = refUserInfTable.orderByChild("uID").equalTo(MyAccountFragment.super.user.getUid());
        query.addListenerForSingleValueEvent(myValueEventListener);

        /*ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    UserLoginInfoTable userLoginInfoTable = data.getValue(UserLoginInfoTable.class);
                    textView.setText(userLoginInfoTable.getName());
                    textViewLastName.setText(userLoginInfoTable.getLastName());
                    ((TextView)view.findViewById(R.id.acc_user_surname)).setText(userLoginInfoTable.getSurname());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        //ref.addChildEventListener(this);
        return view;
    }

   /* @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        for (DataSnapshot data: dataSnapshot.getChildren()){
            if (data.getKey().equals("name")){
                textView.setText(data.getValue().toString());
                break;
            }
        }
    }*/

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
            MyAccountFragment.super.startCurActivity(getContext(), ChangePersonalDataActivity.class);
        }else if (id == R.id.acc_card_view_person_number){
            MyAccountFragment.super.startCurActivity(getContext(), ChangeNumberActivity.class);
        }else if (id == R.id.acc_card_view_person_email){
            //TODO: create method email edit;
        }else if (id == R.id.acc_card_view_mail_check){
            verifyEmail();
        }else if (id == R.id.acc_card_view_person_company){
            changeCompany();
        }
    }

    private void changeCompany(){
        System.out.println("changeCompany");
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

    private void reAuthUser(String email, String pass){
        AuthCredential credential = EmailAuthProvider.getCredential(email, pass);
        super.user.reauthenticate(credential).addOnCompleteListener(credentialComplete);
    }
}


