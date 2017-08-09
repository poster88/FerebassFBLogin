package com.example.user.loginwhithfb.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.eventbus.BusProvider;

/**
 * Created by POSTER on 24.06.2017.
 */

public class ProductCatalogFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_cat, container, false);
        setFragmentForBinder(this, view);

        //TODO: create listener for items
        return view;
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
