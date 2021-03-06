package com.example.user.loginwhithfb.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.example.user.loginwhithfb.R;
import com.google.firebase.database.DatabaseReference;

import butterknife.OnClick;

/**
 * Created by POSTER on 12.07.2017.
 */

public class CompanyInfoActivity extends BaseActivity{

    private DatabaseReference reference;
    private String userUid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info);
        super.setActivityForBinder(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        reference = super.database.getReference(COMP_INF_TABLE);
        if (!user.isAnonymous()){
            userUid = user.getUid();
        }
    }

    @OnClick(R.id.company_info_back_btn)
    public void backPressBtn(){
        onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
