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
 * Created by POSTER on 13.08.2017.
 */

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ListHolder>{
    private ArrayList<Product> dataSet = new ArrayList();
    private Context context;

    public static class ListHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.item_name_label) TextView itemName;
        @BindView(R.id.item_description) TextView itemDescription;
        @BindView(R.id.item_count) TextView itemCount;
        @BindView(R.id.item_available_status) TextView itemStatus;
        @BindView(R.id.item_price) TextView itemPrice;
        @BindView(R.id.item_image) ImageView itemImage;
        @BindView(R.id.progress_bar_item_card) ProgressBar progressBar;

        public ListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public WishListAdapter(ArrayList<Product> dataSet, Context context) {
        this.dataSet = dataSet;
        this.context = context;
    }

    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        ListHolder holder = new ListHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ListHolder holder, int position) {
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
    }

    private void loadPhoto(final ListHolder holder, int position){
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
    public int getItemCount() {
        return dataSet.size();
    }
}
