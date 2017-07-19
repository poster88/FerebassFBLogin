package com.example.user.loginwhithfb.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.model.CompaniesInfoTable;
import com.example.user.loginwhithfb.model.RequestToAddClientToCompaniesTable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private DatabaseReference refCompany;
    private String companyKey;
    private CompaniesInfoTable companiesInfoModel;

    private final String URL_COMPANY_INFO_TABLE = "https://fir-projectdb.firebaseio.com/CompaniesInfoTable";
    private final String URL_REQ_TO_ADD_CLIENT_TABLE = "https://fir-projectdb.firebaseio.com/RequestToAddClientToCompaniesTable";
    private final String COMPANY_ID = "companyId";
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_create_req_add_company);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        database = FirebaseDatabase.getInstance();
        try {
            companyKey = getIntent().getStringExtra(COMPANY_ID);
            refCompany = database.getReferenceFromUrl(URL_COMPANY_INFO_TABLE + "/" + companyKey);
        }catch (Exception e){
            e.printStackTrace();
        }
        refCompany.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                companiesInfoModel = dataSnapshot.getValue(CompaniesInfoTable.class);
                innitDataToWidgets();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("onCancelled: " + databaseError.getMessage());
            }
        });
    }

    private void innitDataToWidgets() {
        companyName.setText(companiesInfoModel.getCompanyName());
        companyDescr.setText(companiesInfoModel.getCompanyDescr());
        Map<String, Object> data = (Map<String, Object>) companiesInfoModel.getPositions();
        List<String> listPositions = new ArrayList<>(data.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listPositions);
        spinChoosePos.setAdapter(adapter);
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
        DatabaseReference refRequestTable = database.getReferenceFromUrl(URL_REQ_TO_ADD_CLIENT_TABLE);
        refRequestTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (checkUsersRequestExist(dataSnapshot)){
                    createRequestAddUser();
                }else {
                    Toast.makeText(getBaseContext(), "Request is already sent", Toast.LENGTH_SHORT).show();
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
                onBackPressed();
            }
        });
        super.onBackPressed();
    }

    private boolean checkUsersRequestExist(DataSnapshot dataSnapshot) {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        for (DataSnapshot data: dataSnapshot.getChildren()){
            String clientUid = data.getValue(RequestToAddClientToCompaniesTable.class).getClientUid();
            String companyId = data.getValue(RequestToAddClientToCompaniesTable.class).getCompanyId();
            if (userUid.equals(clientUid) && companyKey.equals(companyId)){
                return false;
            }
        }
        return true;
    }

    private void createRequestAddUser(){
        DatabaseReference reference = database.getReferenceFromUrl(URL_REQ_TO_ADD_CLIENT_TABLE);
        String id = reference.push().getKey();
        RequestToAddClientToCompaniesTable request = new RequestToAddClientToCompaniesTable(
                id, new Date().toString(), FirebaseAuth.getInstance().getCurrentUser().getUid(), companyKey, null, "in process"
        );
        Map<String, Object> newTable = new HashMap<>();
        Map<String, Object> tempMap = request.toMap();
        newTable.put(id, tempMap);
        reference.updateChildren(newTable);
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
