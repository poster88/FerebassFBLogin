package com.example.user.loginwhithfb.activity;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.lisntener.MyValueEventListener;
import com.example.user.loginwhithfb.model.CompaniesInfoTable;
import com.example.user.loginwhithfb.model.RequestToAddClientToCompaniesTable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by POSTER on 07.07.2017.
 */

public class AddRequestCompanyActivity extends BaseActivity{
    @BindView(R.id.company_logo) ImageView companyLogo;
    @BindView(R.id.company_name_req) TextView companyName;
    @BindView(R.id.company_desc_req) TextView companyDescr;
    @BindView(R.id.company_key_pr) TextView keyProducts;
    @BindView(R.id.spinner_choose_position) Spinner spinChoosePos;

    private DatabaseReference refCompany;
    private String companyKey;
    private CompaniesInfoTable companiesInfoModel;
    private List<String> listPositions;

    private final String URL_COMPANY_INFO_TABLE = "https://fir-projectdb.firebaseio.com/CompaniesInfoTable";
    private final String URL_REQ_TO_ADD_CLIENT_TABLE = "https://fir-projectdb.firebaseio.com/RequestToAddClientToCompaniesTable";
    private final String COMPANY_ID = "companyId";
    private MyValueEventListener onDataSetListener = new MyValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            companiesInfoModel = dataSnapshot.getValue(CompaniesInfoTable.class);
            innitDataToWidgets();
        }
    };
    private MyValueEventListener onRequestCreated = new MyValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            AddRequestCompanyActivity.super.hideProgressDialog();
            if (checkUsersRequestExist(dataSnapshot)){
                createRequestAddUser();
                AddRequestCompanyActivity.super.showToast(getBaseContext(), "Request created.");
                finish();
            }else {
                AddRequestCompanyActivity.super.showToast(getBaseContext(), "Request is already sent");
            }
        }
    };
    private DialogInterface.OnClickListener negOnclickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

    private DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            DatabaseReference refRequestTable = AddRequestCompanyActivity.super.database.getReferenceFromUrl(URL_REQ_TO_ADD_CLIENT_TABLE);
            refRequestTable.addListenerForSingleValueEvent(onRequestCreated);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_create_req_add_company);
        super.onCreate(savedInstanceState);
        super.setActivityForBinder(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            companyKey = getIntent().getStringExtra(COMPANY_ID);
            refCompany = super.database.getReferenceFromUrl(URL_COMPANY_INFO_TABLE + "/" + companyKey);
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
        refCompany.addListenerForSingleValueEvent(onDataSetListener);
    }

    private void innitDataToWidgets() {
        companyName.setText(companiesInfoModel.getCompanyName());
        companyDescr.setText(companiesInfoModel.getCompanyDescr());
        keyProducts.setText(super.createStringText(companiesInfoModel.getCompanyProducts()));
        Map<String, Object> data = (Map<String, Object>) companiesInfoModel.getPositions();
        listPositions = new ArrayList<>(data.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listPositions);
        spinChoosePos.setAdapter(adapter);
        if (!companiesInfoModel.getCompanyLogoUri().toString().equals("default_uri")){
            super.loadImage(Uri.parse(companiesInfoModel.getCompanyLogoUri()), companyLogo);
        }
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
        super.showProgressDialog("Creating request...");
        super.showAlertDialog("Send request", "Send request to a new company?", android.R.drawable.ic_menu_edit,
                false, "Send", "Cancel", posListener, negOnclickListener);

    }

    private boolean checkUsersRequestExist(DataSnapshot dataSnapshot) {
        String userUid = super.user.getUid();
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
                id, new Date().toString(), super.user.getUid(), companyKey, "in process", listPositions.get(spinChoosePos.getSelectedItemPosition()));
        Map<String, Object> newTable = new HashMap<>();
        Map<String, Object> tempMap = request.toMap();
        newTable.put(id, tempMap);
        reference.updateChildren(newTable);
    }
}
