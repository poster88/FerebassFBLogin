package com.example.user.loginwhithfb.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.loginwhithfb.R;

/**
 * Created by POSTER on 19.07.2017.
 */

public class CompanyChatFragment extends BaseFragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_company_chat, container, false);
        return view;
    }
}