package com.example.user.loginwhithfb.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.activity.NavigationDrawerActivity;
import com.example.user.loginwhithfb.event.UpdateItem;
import com.example.user.loginwhithfb.eventbus.BusProvider;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import butterknife.BindView;

/**
 * Created by POSTER on 24.06.2017.
 */

public class ProductCatalogFragment extends BaseFragment {
    @BindView(R.id.testItems)
    TextView testItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_cat, container, false);
        setFragmentForBinder(this, view);
        System.out.println("oncreate view ");
        return view;
    }

    @Subscribe
    public void updateItem(UpdateItem event){
        if (NavigationDrawerActivity.companiesInfoTable != null){
            testItem.setText(NavigationDrawerActivity.companiesInfoTable.getCompanyName());
        }
        System.out.println(" from update item");
    }

    @Produce
    public UpdateItem update(){
        System.out.println("default");
        return new UpdateItem();
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
