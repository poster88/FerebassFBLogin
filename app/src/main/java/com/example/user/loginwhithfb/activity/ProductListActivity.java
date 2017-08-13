package com.example.user.loginwhithfb.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.adapter.MyRecycleViewAdapter;
import com.example.user.loginwhithfb.eventbus.BusProvider;
import com.example.user.loginwhithfb.lisntener.MyValueEventListener;
import com.example.user.loginwhithfb.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
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
    private DatabaseReference refWithList;
    private MyValueEventListener loadData = new MyValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()){
                products = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    products.add(new Product((Map<String, Object>) data.getValue()));
                }
                refWithList = database.getReferenceFromUrl("https://fir-projectdb.firebaseio.com/" + "WishListTable/" + BaseActivity.userModel.getuID());
                refWithList.addListenerForSingleValueEvent(listener);
            }
        }
    };
    private MyValueEventListener listener = new MyValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            innitAdapter(dataSnapshot);
            System.out.println("init listener");
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (ProductListActivity.super.user != null && !ProductListActivity.super.user.isAnonymous()){
            BusProvider.getInstance().register(this);
            databaseReference.addValueEventListener(loadData);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        super.onStop();
        if (super.user != null && !super.user.isAnonymous()){
            BusProvider.getInstance().unregister(this);
            databaseReference.removeEventListener(loadData);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_list);
        super.setActivityForBinder(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        databaseReference = super.database.getReferenceFromUrl(getIntent().getStringExtra("reference"));
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

    private void innitAdapter(DataSnapshot dataSnapshot) {
        adapter = new MyRecycleViewAdapter(products, dataSnapshot, this);
        recyclerView.setAdapter(adapter);
        ((MyRecycleViewAdapter) adapter).setOnItemClickListener(myClickListener);
    }

    private MyRecycleViewAdapter.MyClickListener myClickListener = new MyRecycleViewAdapter.MyClickListener() {
        @Override
        public void onItemClick(int position, View v) {
            System.out.println(position + " " + products.get(position).getName() + " " + getResources().getResourceEntryName(v.getId()));
            DatabaseReference reference = database.getReferenceFromUrl("https://fir-projectdb.firebaseio.com/" + "WishListTable/" + ProductListActivity.super.user.getUid());
            Map<String, Object> newTable = new HashMap<>();
            newTable.put(products.get(position).getId(), products.get(position));
            reference.updateChildren(newTable);
        }
    };
}
