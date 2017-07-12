package com.example.user.loginwhithfb.activity;

import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.model.RequestToAddClientToCompaniesTable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    @BindView(R.id.progressBar) ProgressBar progressBar;

    private ArrayList<String> arrayCompanyTitles;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_company);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("RequestToAddClientToCompaniesTable");
        innitCompanyList();

        companyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("Comapany name is: " + arrayCompanyTitles.get(position));
                //TODO: create a bundle for Data
                startActivity(new Intent(getBaseContext(), AddRequestCompanyActivity.class)
                        .putExtra("companyName", arrayCompanyTitles.get(position)));
                finish();
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                ArrayAdapter adapter = new ArrayAdapter(SearchCompanyActivity.this, android.R.layout.simple_list_item_1, arrayCompanyTitles);
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
                    for (String str: arrayCompanyTitles) {
                        if(str.trim().toLowerCase().contains(newText.trim().toLowerCase())){
                            firstFound.add(str);
                        }
                    }
                    ArrayAdapter adapter = new ArrayAdapter(SearchCompanyActivity.this, android.R.layout.simple_list_item_1, firstFound);
                    companyList.setAdapter(adapter);
                }else {
                    ArrayAdapter adapter = new ArrayAdapter(SearchCompanyActivity.this, android.R.layout.simple_list_item_1, arrayCompanyTitles);
                    companyList.setAdapter(adapter);
                }
                return false;
            }
        });
    }

    private boolean innitCompanyList() {
        showProgressDialog();
        arrayCompanyTitles = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                        arrayCompanyTitles.add(data.getValue(RequestToAddClientToCompaniesTable.class).getCompanyName());
                }
                ArrayAdapter adapter = new ArrayAdapter(SearchCompanyActivity.this, android.R.layout.simple_list_item_1, arrayCompanyTitles);
                companyList.setAdapter(adapter);
                hideProgressDialog();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SearchCompanyActivity.this, "Error : " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                hideProgressDialog();
            }
        });
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_item, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    public boolean showProgressDialog() {
        progressBar.setVisibility(View.VISIBLE);
        return true;
    }

    public boolean hideProgressDialog() {
        progressBar.setVisibility(View.GONE);
        companyList.setVisibility(View.VISIBLE);
        return false;
    }


}
