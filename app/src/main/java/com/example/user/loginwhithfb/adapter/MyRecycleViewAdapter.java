package com.example.user.loginwhithfb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.model.Product;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 009 09.08.17.
 */

public class MyRecycleViewAdapter extends RecyclerView.Adapter<MyRecycleViewAdapter.ProductModelHolder>{
    private ArrayList<Product> dataSet = new ArrayList();
    private Context context;
    private static MyClickListener myClickListener;


    public static class ProductModelHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_name_label) TextView itemName;
        @BindView(R.id.item_description) TextView itemDescription;
        @BindView(R.id.item_count) TextView itemCount;
        @BindView(R.id.item_available_status) TextView itemStatus;
        @BindView(R.id.item_price) TextView itemPrice;
        @BindView(R.id.item_image) ImageView itemImage;


        public ProductModelHolder(View itemView) {
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

    public MyRecycleViewAdapter(ArrayList<Product> myDataset, Context context){
        dataSet = myDataset;
        this.context = context;
    }

    @Override
    public ProductModelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        ProductModelHolder holder = new ProductModelHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ProductModelHolder holder, int position) {
        holder.itemName.setText("from holder");
        holder.itemDescription.setText("from holder");
        holder.itemCount.setText("from holder");
        holder.itemStatus.setText("from holder");
        holder.itemPrice.setText("from holder");
        //holder.itemImage.setImageDrawable();
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
