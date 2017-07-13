package com.example.user.loginwhithfb.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.activity.CompanyInfoActivity;
import com.example.user.loginwhithfb.model.CompaniesInfoTable;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by POSTER on 24.06.2017.
 */

public class HomeFragment extends Fragment {
    private FirebaseDatabase database;
    private DatabaseReference reference;


    public HomeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("CompaniesInfoTable");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ((Button)view.findViewById(R.id.company_info_gen_comp_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BTN CLK", "company_info_gen_comp_btn click");
                createCpmpanieInfoTable();
            }
        });

        return view;
    }

    private void createCpmpanieInfoTable() {
        String id = reference.push().getKey();
        List<String> productKeys = new ArrayList<>();
        productKeys.add("key product 1");
        productKeys.add("key product 2");

        Map<String, Object> positions = new HashMap<>();
        List<String> peoples = new ArrayList<>();
        peoples.add("person 1");
        peoples.add("person 2");
        peoples.add("person 3");

        positions.put("pos 1", peoples);
        CompaniesInfoTable companyInfoTable = new CompaniesInfoTable(
                "id", "test", "test", "test", productKeys, positions
        );
        Map<String, Object> newTable = new HashMap<>();
        Map<String, Object> tempTable = companyInfoTable.toMap();
        newTable.put(id, tempTable);
        reference.updateChildren(newTable);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
