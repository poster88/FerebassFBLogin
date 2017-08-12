package com.example.user.loginwhithfb.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.adapter.MyRecycleViewAdapter;
import com.example.user.loginwhithfb.lisntener.MyValueEventListener;
import com.example.user.loginwhithfb.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by POSTER on 12.08.2017.
 */

public class ProductListActivity extends BaseActivity{
    @BindView(R.id.prod_items_recycle_view) RecyclerView recyclerView;

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Product> products;
    private DatabaseReference databaseReference;
    private Handler handler;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            innitAdapter();
        }
    };
    private MyValueEventListener loadData = new MyValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()){
                products = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    products.add(new Product((Map<String, Object>) data.getValue()));
                }
                handler.post(runnable);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_list);
        super.setActivityForBinder(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        handler = new Handler();
        databaseReference = super.database.getReferenceFromUrl(getIntent().getStringExtra("reference"));
        databaseReference.addListenerForSingleValueEvent(loadData);
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

    private void innitAdapter() {
        adapter = new MyRecycleViewAdapter(products, this);
        recyclerView.setAdapter(adapter);
    }
}
