package com.example.user.loginwhithfb.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.adapter.OrderListAdapter;
import com.example.user.loginwhithfb.lisntener.MyValueEventListener;
import com.example.user.loginwhithfb.model.OrderModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by POSTER on 24.06.2017.
 */

public class MyOrdersFragment extends BaseFragment{
    @BindView(R.id.order_list) ListView orderList;
    @BindView(R.id.label) TextView label;
    @BindView(R.id.data_status) TextView dataStatus;
    private DatabaseReference refData;
    private ArrayList<OrderModel> arrayList;
    private MyValueEventListener loadData = new MyValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            innitData(dataSnapshot);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);
        setFragmentForBinder(this, view);
        refData = super.database.getReference(USER_ORDER_TABLE).child(super.user.getUid());
        return view;
    }

    private void innitData(DataSnapshot dataSnapshot){
        arrayList = new ArrayList<>();
        for (DataSnapshot data: dataSnapshot.getChildren()) {
            arrayList.add(new OrderModel((Map<String, Object>) data.getValue()));
        }
        label.setVisibility(View.GONE);
        dataStatus.setVisibility(View.GONE);
        orderList.setVisibility(View.VISIBLE);
        orderList.setAdapter(new OrderListAdapter(getContext(), arrayList));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (super.user != null && !super.user.isAnonymous()){
            refData.addValueEventListener(loadData);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (super.user != null && !super.user.isAnonymous()){
            refData.removeEventListener(loadData);
        }
    }
}
