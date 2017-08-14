package com.example.user.loginwhithfb.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.model.OrderModel;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by POSTER on 14.08.2017.
 */

public class OrderListAdapter extends BaseAdapter{
    private ArrayList<OrderModel> list = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;

    public OrderListAdapter(Context context, ArrayList<OrderModel> list) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = list;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.orders_list_card_item, parent, false);
        if (!list.get(position).getPhotoUri().equals("default_uri")){
            loadImage(convertView, position);
        }
        ((TextView) ButterKnife.findById(convertView, R.id.title)).setText(list.get(position).getItemName());
        ((TextView) ButterKnife.findById(convertView, R.id.item_order_price)).setText(list.get(position).getItemPrice());
        return convertView;
    }

    private void loadImage(View convertView, int position) {
        final ProgressBar progressBar = ButterKnife.findById(convertView, R.id.progress_bar_item_order);
        final ImageView imageView = ButterKnife.findById(convertView, R.id.item_order_image);
        Glide.with(context).load(Uri.parse(list.get(position).getPhotoUri())).listener(new RequestListener<Uri, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                return false;
            }
        }).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
