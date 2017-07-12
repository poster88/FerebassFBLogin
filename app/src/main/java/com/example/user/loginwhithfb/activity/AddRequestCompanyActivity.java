package com.example.user.loginwhithfb.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.model.RequestToAddClientToCompaniesTable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by POSTER on 07.07.2017.
 */

public class AddRequestCompanyActivity extends AppCompatActivity{
    @BindView(R.id.company_logo) ImageView companyLogo;
    @BindView(R.id.company_name_req) TextView companyName;
    @BindView(R.id.company_desc_req) TextView companyDescr;
    @BindView(R.id.spinner_choose_position) Spinner spinChoosePos;

    private ProgressDialog progressDialog;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_create_req_add_company);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("RequestToAddClientToCompaniesTable");
        //reference.child(key);
        //System.out.println("company name is: " + getIntent().getStringExtra("companyName"));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("onDataChange: " + dataSnapshot.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("onCancelled: " + databaseError.getMessage());
            }
        });
        generateCompany();
    }

    private void generateCompany() {
        ((Button)findViewById(R.id.generateCompany)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = new Random().nextInt(1000);
                String id = reference.push().getKey();
                RequestToAddClientToCompaniesTable reqModel = new RequestToAddClientToCompaniesTable(
                        "id", "test", "test", "company №" + number, "test", "test"
                );
                Map<String, Object> tempTable = reqModel.toMap();
                Map<String, Object> newTable = new HashMap<>();
                newTable.put(id, tempTable);
                reference.updateChildren(newTable);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return true;
    }

    @OnClick(R.id.sendRequestBtn)
    public void sendRequest(){
        showProgressDialog();
        createRequestAddUser();
    }

    private void createRequestAddUser(){

    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }



}
