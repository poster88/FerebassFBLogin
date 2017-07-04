package com.example.user.loginwhithfb.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.user.loginwhithfb.ChangePassActivity;
import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.SearchCompanyActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        ImageView verEmailStatusImg = ButterKnife.findById(view, R.id.email_verify_status_img);

        unbinder = ButterKnife.bind(this, view);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            if (user.isAnonymous()){
                userImg.setImageResource(R.drawable.ic_person_black_24dp);
                verEmailStatusImg.setImageResource(R.drawable.ic_fiber_manual_record_reg_24dp);
            }else {
                Glide.with(this).load(user.getPhotoUrl()).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(userImg);
                if (!user.isEmailVerified()){
                    verEmailStatusImg.setImageResource(R.drawable.ic_fiber_manual_record_reg_24dp);
                }else {
                    verEmailStatusImg.setImageResource(R.drawable.ic_fiber_manual_record_green_24dp);
                }
            }
        }
        Log.d("USER ID", " " + user.getToken(true));
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

    @OnClick(R.id.change_user_pass_btn)
    public void changeUserPass(){
        startActivity(new Intent(getActivity(), ChangePassActivity.class));
    }

    @OnClick(R.id.company_search)
    public void companySearch(){
        startActivity(new Intent(getActivity(), SearchCompanyActivity.class));
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
