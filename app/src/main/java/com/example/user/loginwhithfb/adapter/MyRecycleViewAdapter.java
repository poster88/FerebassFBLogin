package com.example.user.loginwhithfb.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        @BindView(R.id.progress_bar_item_card) ProgressBar progressBar;

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

    public MyRecycleViewAdapter(ArrayList<Product> myDataSet, Context context){
        dataSet = myDataSet;
        this.context = context;
    }

    @Override
    public ProductModelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        ProductModelHolder holder = new ProductModelHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ProductModelHolder holder, int position) {
        holder.itemName.setText(dataSet.get(position).getName());
        holder.itemDescription.setText(dataSet.get(position).getDescription());
        holder.itemCount.setText(String.valueOf(dataSet.get(position).getCount()));
        if (dataSet.get(position).isAvailability()){
            holder.itemStatus.setText("Наявний");
        }else {
            holder.itemStatus.setText("Не наявний");
        }
        holder.itemPrice.setText(String.valueOf(dataSet.get(position).getCount()));
        if (!dataSet.get(position).getPhotoUries().equals("default_uri")){
            Glide.with(context).load(Uri.parse(dataSet.get(position).getPhotoUries()))
                    .listener(new RequestListener<Uri, GlideDrawable>() {
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
            holder.itemImage.setImageResource(R.mipmap.ic_image);
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
