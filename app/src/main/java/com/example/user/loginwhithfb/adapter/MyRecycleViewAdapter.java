package com.example.user.loginwhithfb.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.model.Product;
import com.google.firebase.database.DataSnapshot;

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
    private DataSnapshot dataSnapshot;

    public static class ProductModelHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_name_label) TextView itemName;
        @BindView(R.id.item_description) TextView itemDescription;
        @BindView(R.id.item_count) TextView itemCount;
        @BindView(R.id.item_available_status) TextView itemStatus;
        @BindView(R.id.item_price) TextView itemPrice;
        @BindView(R.id.item_image) ImageView itemImage;
        @BindView(R.id.progress_bar_item_card) ProgressBar progressBar;
        @BindView(R.id.item_add_wish_list) ImageView imageAddToList;
        @BindView(R.id.buy_btn) Button buyBtn;

        public ProductModelHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MyRecycleViewAdapter(ArrayList<Product> myDataSet, DataSnapshot dataSnapshot, Context context){
        this.dataSet = myDataSet;
        this.context = context;
        this.dataSnapshot = dataSnapshot;
    }

    public MyRecycleViewAdapter(ArrayList<Product> myDataSet, Context context){
        this.dataSet = myDataSet;
        this.context = context;
    }

    @Override
    public ProductModelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        ProductModelHolder holder = new ProductModelHolder(view);
        return holder;
    }

    private void loadPhoto(final ProductModelHolder holder, int position){
        if (!dataSet.get(position).getPhotoUri().equals("default_uri")){
            Glide.with(context).load(Uri.parse(dataSet.get(position).getPhotoUri())).listener(new RequestListener<Uri, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                    holder.progressBar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    holder.progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.itemImage);
        }else {
            holder.progressBar.setVisibility(View.GONE);
            holder.itemImage.setImageResource(R.mipmap.ic_image);
        }
    }

    @Override
    public void onBindViewHolder(final ProductModelHolder holder, final int position) {
        holder.itemName.setText(dataSet.get(position).getName());
        holder.itemDescription.setText(dataSet.get(position).getDescription());
        holder.itemCount.setText(String.valueOf(dataSet.get(position).getCount()));
        if (dataSet.get(position).isAvailability()){
            holder.itemStatus.setText("Наявний");
        }else {
            holder.itemStatus.setText("Не наявний");
        }
        holder.itemPrice.setText(String.valueOf(dataSet.get(position).getPrice()) + " грн");
        loadPhoto(holder, position);
        addToOrders(position, holder);
        if (dataSnapshot != null) {
            addToWithList(holder, position);
        }else {
            holder.imageAddToList.setVisibility(View.GONE);
        }
    }

    private void addToOrders(final int position, final ProductModelHolder holder){
        View.OnClickListener buyClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClickListener.onItemClick(position, v);
            }
        };
        holder.buyBtn.setOnClickListener(buyClickListener);
    }

    private void addToWithList(final ProductModelHolder holder, final int position){
        if (!dataSnapshot.hasChild(dataSet.get(position).getId())){
            View.OnClickListener addToWithList = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myClickListener.onItemClick(position, v);
                    if (dataSet.get(position).isAvailability()){
                        holder.imageAddToList.setImageResource(R.drawable.ic_favorite_black_24dp);
                    }
                }
            };
            holder.imageAddToList.setOnClickListener(addToWithList);
        }else {
            holder.imageAddToList.setImageResource(R.drawable.ic_favorite_black_24dp);
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}