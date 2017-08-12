package com.example.user.loginwhithfb.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.lisntener.MyValueEventListener;
import com.example.user.loginwhithfb.model.CompaniesInfoTable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by POSTER on 04.07.2017.
 */

public class SearchCompanyActivity extends BaseActivity implements AdapterView.OnItemClickListener, MaterialSearchView.SearchViewListener {
    @BindView(R.id.search_view) MaterialSearchView searchView;
    @BindView(R.id.toolbar_search) Toolbar toolbar;
    @BindView(R.id.companyList) ListView companyList;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    private Map<String, String> companyData;
    private DatabaseReference reference;
    private final String COMPANY_INFO_TABLE = "CompaniesInfoTable";
    private MyValueEventListener dataListener = new MyValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot data: dataSnapshot.getChildren()) {
                String companyId = data.getValue(CompaniesInfoTable.class).getcompanyId();
                String companyName = data.getValue(CompaniesInfoTable.class).getCompanyName();
                if (!userModel.getCompanyUid().equals(companyId)){
                    companyData.put(companyId, companyName);
                }
            }
            ArrayAdapter adapter = new ArrayAdapter(SearchCompanyActivity.this, android.R.layout.simple_list_item_1, companyData.values().toArray());
            companyList.setAdapter(adapter);
            isProgressDialogHide();
        }
    };

    private MaterialSearchView.OnQueryTextListener queryTextListener = new MaterialSearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (newText != null && !newText.isEmpty()){
                List<String> firstFound = new ArrayList<>();
                for (String str: companyData.values()) {
                    if(str.trim().toLowerCase().contains(newText.trim().toLowerCase())){
                        firstFound.add(str);
                    }
                }
                ArrayAdapter adapter = new ArrayAdapter(SearchCompanyActivity.this, android.R.layout.simple_list_item_1, firstFound);
                companyList.setAdapter(adapter);
            }else {
                ArrayAdapter adapter = new ArrayAdapter(SearchCompanyActivity.this, android.R.layout.simple_list_item_1, companyData.values().toArray());
                companyList.setAdapter(adapter);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_company);
        super.setActivityForBinder(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        reference = database.getReference(COMPANY_INFO_TABLE);
        innitCompanyList();
        setListeners();
    }

    private void setListeners() {
        companyList.setOnItemClickListener(this);
        searchView.setOnSearchViewListener(this);
        searchView.setOnQueryTextListener(queryTextListener);
    }

    private void innitCompanyList() {
        isProgressDialogShowing();
        companyData = new HashMap<>();
        reference.addListenerForSingleValueEvent(dataListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_item, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
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

    public boolean isProgressDialogShowing() {
        progressBar.setVisibility(View.VISIBLE);
        return true;
    }

    public boolean isProgressDialogHide() {
        progressBar.setVisibility(View.GONE);
        companyList.setVisibility(View.VISIBLE);
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<String> list = new ArrayList<>(companyData.keySet());
        startActivity(new Intent(getBaseContext(), AddRequestCompanyActivity.class).putExtra("companyId", list.get(position)));
    }

    @Override
    public void onSearchViewShown() {

    }

    @Override
    public void onSearchViewClosed() {
        ArrayAdapter adapter = new ArrayAdapter(SearchCompanyActivity.this, android.R.layout.simple_list_item_1, companyData.values().toArray());
        companyList.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
