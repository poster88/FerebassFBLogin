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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

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
        CompanyInfoActivity companyInfoTable = new CompanyInfoActivity(
                
        );
        ArrayList<String> membersList = new ArrayList<>();



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
