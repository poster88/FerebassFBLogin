package com.example.user.loginwhithfb.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.activity.BaseActivity;
import com.example.user.loginwhithfb.adapter.MyRecycleViewAdapter;
import com.example.user.loginwhithfb.event.UpdateCompanyUI;
import com.example.user.loginwhithfb.eventbus.BusProvider;
import com.example.user.loginwhithfb.lisntener.MyValueEventListener;
import com.example.user.loginwhithfb.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by POSTER on 24.06.2017.
 */

public class ProductCatalogFragment extends BaseFragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference reference;

    private MyValueEventListener loadData = new MyValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_cat, container, false);
        setFragmentForBinder(this, view);

        recyclerView = (RecyclerView)view.findViewById(R.id.cat_items_recycle_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyRecycleViewAdapter(getDataset(), getContext());
        recyclerView.setAdapter(adapter);

        ((MyRecycleViewAdapter) adapter).setOnItemClickListener(new MyRecycleViewAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                System.out.println("position click " + position);
            }
        });
        return view;
    }

    private ArrayList<Product> getDataset(){
        ArrayList<Product> results = new ArrayList();
        for (int index = 0; index < 20; index++){
            Product model = new Product(
                    "id", "name", "decsr", "uri", 12, 4.13, true
            );
            results.add(model);
        }
        return results;
    }

    @Subscribe
    public void updateCompanyUI(UpdateCompanyUI event){
        reference = database.getReferenceFromUrl("https://fir-projectdb.firebaseio.com/" + COMP_INF_TABLE + "/" + BaseActivity.userModel.getCompanyUid() + "/" + "wareHouse" + "/");
        Map<String, Object> categoryMap = (Map<String, Object>) BaseActivity.companiesInfoTable.getWareHouse();
        List<String> category = new ArrayList<>();
        List<Object> subcategory = new ArrayList<>();
        for (Map.Entry map: categoryMap.entrySet()) {
            category.add(map.getKey().toString());
            subcategory.add(map.getValue());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }
}
