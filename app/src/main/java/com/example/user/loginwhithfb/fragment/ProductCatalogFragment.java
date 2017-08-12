package com.example.user.loginwhithfb.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.activity.BaseActivity;
import com.example.user.loginwhithfb.activity.ProductListActivity;
import com.example.user.loginwhithfb.adapter.CategoryRecycleViewAdapter;
import com.example.user.loginwhithfb.event.UpdateCompanyUI;
import com.example.user.loginwhithfb.eventbus.BusProvider;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by POSTER on 24.06.2017.
 */

public class ProductCatalogFragment extends BaseFragment {
    @BindView(R.id.cat_items_recycle_view) RecyclerView recyclerView;

    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Map<String, Object> categoryMap;
    private ArrayList<String> categoryList;
    private ArrayList<String> subcategoryList;
    private CategoryRecycleViewAdapter.MyClickListener myClickListener = new CategoryRecycleViewAdapter.MyClickListener() {
        @Override
        public void onItemClick(int position, View v) {
            Object o = categoryMap.get(categoryList.get(position));
            Map<String, Object> subCategories = (Map<String, Object>) o;
            subcategoryList = new ArrayList<>();
            for (Map.Entry data : subCategories.entrySet()) {
                subcategoryList.add(data.getKey().toString());
            }
            ListView subCatList = ButterKnife.findById(v, R.id.subCatListView);
            ImageView popUpItem = ButterKnife.findById(v, R.id.next_image);
            if (subCatList.getVisibility() == View.GONE){
                popUpItem.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                innitListData(subCatList, position);
            }else {
                subCatList.setVisibility(View.GONE);
                popUpItem.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
            }
        }
    };
    private void innitListData(ListView subCatList, final int catListPosition) {
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, subcategoryList);
        subCatList.setAdapter(adapter);
        subCatList.setVisibility(View.VISIBLE);
        subCatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              setDataToActivity(catListPosition, position);
            }
        });
    }

    private void setDataToActivity(int catListPosition, int subCatPosition){
        String reference = "https://fir-projectdb.firebaseio.com/CompaniesInfoTable/" +
                BaseActivity.userModel.getCompanyUid() + "/wareHouse/" + categoryList.get(catListPosition) + "/" +
                subcategoryList.get(subCatPosition) + "/";
        startActivity(new Intent(getContext(), ProductListActivity.class).putExtra("reference", reference));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_cat, container, false);
        setFragmentForBinder(this, view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }

    @Subscribe
    public void updateCompanyUI(UpdateCompanyUI event){
        if (BaseActivity.companiesInfoTable != null){
            categoryMap = (Map<String, Object>) BaseActivity.companiesInfoTable.getWareHouse();
            categoryList = new ArrayList<>();
            for (Map.Entry map: categoryMap.entrySet()) {
                categoryList.add(map.getKey().toString());
            }
            adapter = new CategoryRecycleViewAdapter(categoryList, getContext());
            recyclerView.setAdapter(adapter);
            ((CategoryRecycleViewAdapter) adapter).setOnItemClickListener(myClickListener);
        }
    }

    @Produce
    public UpdateCompanyUI updateCompanyUI(){
        return new UpdateCompanyUI();
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
