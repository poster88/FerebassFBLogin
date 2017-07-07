package com.example.user.loginwhithfb.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.user.loginwhithfb.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by POSTER on 04.07.2017.
 */

public class SearchCompanyActivity extends AppCompatActivity{
    @BindView(R.id.search_view) MaterialSearchView searchView;
    @BindView(R.id.toolbar_search)  Toolbar toolbar;
    @BindView(R.id.companyList) ListView companyList;

    private String[] companyTitles = {"First", "Second", "Third", "Four", "Five", "Six", "Dog",
            "Boll", "Bag", "Data", "Yellow", "Skip", "Mite", "Ten"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_company);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //TODO: create adapter for a search view by companies
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, companyTitles);
        companyList.setAdapter(adapter);
        companyList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("clicked id is: " + id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                ArrayAdapter adapter = new ArrayAdapter(SearchCompanyActivity.this, android.R.layout.simple_list_item_1, companyTitles);
                companyList.setAdapter(adapter);
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && !newText.isEmpty()){
                    List<String> firstFound = new ArrayList<String>();
                    for (String str: companyTitles) {
                        if(str.trim().toLowerCase().contains(newText.trim().toLowerCase())){
                            firstFound.add(str);
                        }
                    }
                    ArrayAdapter adapter = new ArrayAdapter(SearchCompanyActivity.this, android.R.layout.simple_list_item_1, firstFound);
                    companyList.setAdapter(adapter);
                }else {
                    ArrayAdapter adapter = new ArrayAdapter(SearchCompanyActivity.this, android.R.layout.simple_list_item_1, companyTitles);
                    companyList.setAdapter(adapter);
                }
                return false;
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
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_item, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }


}
