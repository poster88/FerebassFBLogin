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
import com.example.user.loginwhithfb.adapter.WishListAdapter;
import com.example.user.loginwhithfb.lisntener.MyValueEventListener;
import com.example.user.loginwhithfb.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by POSTER on 19.07.2017.
 */

public class WishListFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Product> products;
    private DatabaseReference databaseReference;
    private MyValueEventListener listener = new MyValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            products = new ArrayList<>();
            if (dataSnapshot.exists()){
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    products.add(new Product((Map<String, Object>) data.getValue()));
                }
                innitAdapter();
            }
        }
    };
    private void innitAdapter() {
        adapter = new MyRecycleViewAdapter(products, getContext());
        recyclerView.setAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_products_list, container, false);
        setFragmentForBinder(this, view);
        recyclerView = (RecyclerView) view.findViewById(R.id.prod_items_recycle_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(layoutManager);
        databaseReference = database.getReferenceFromUrl("https://fir-projectdb.firebaseio.com/" + "WishListTable/" + BaseActivity.userModel.getuID());
        databaseReference.addValueEventListener(listener);
        return view;
    }
}
