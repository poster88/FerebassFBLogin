package com.example.user.loginwhithfb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.loginwhithfb.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by POSTER on 11.08.2017.
 */

public class CategoryRecycleViewAdapter extends RecyclerView.Adapter<CategoryRecycleViewAdapter.CategoryHolder>{
    private ArrayList<String> arrayCategories = new ArrayList<>();
    private static MyClickListener myClickListener;
    private Context context;

    public static class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_label) TextView categoryLabel;

        public CategoryHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public CategoryRecycleViewAdapter(ArrayList<String> dataList, Context context){
        arrayCategories = dataList;
        this.context = context;
    }

    @Override
    public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent, false);
        CategoryHolder holder = new CategoryHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CategoryHolder holder, int position) {
        holder.categoryLabel.setText(arrayCategories.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayCategories.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
