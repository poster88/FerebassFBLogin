package com.example.user.loginwhithfb.activity;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.lisntener.MyValueEventListener;
import com.example.user.loginwhithfb.model.OrderModel;
import com.example.user.loginwhithfb.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by POSTER on 13.08.2017.
 */

public class OrderActivity extends BaseActivity {
    @BindView(R.id.order_user_name_edit) EditText userNameAndEmail;
    @BindView(R.id.order_user_adress) EditText deliveryAddress;
    @BindView(R.id.item_card) LinearLayout itemCardLayout;
    @BindView(R.id.order_count_edit) EditText itemCount;
    @BindView(R.id.order_message) EditText orderMessage;

    private TextView itemName;
    private TextView itemDescr;
    private ImageView itemImage;
    private ProgressBar progressBar;
    private DatabaseReference reference;
    private Product product;
    private MyValueEventListener loadItem = new MyValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            product = dataSnapshot.getValue(Product.class);
            innitDataToWidgets();
        }
    };
    private void innitDataToWidgets() {
        userNameAndEmail.setText(super.userModel.getName() + " " + super.userModel.getLastName() + ", " +
                super.userModel.getEmail() + ", " + super.userModel.getMobileNumber());
        itemName.setText(product.getName());
        itemDescr.setText(product.getDescription());
        Glide.with(this).load(Uri.parse(product.getPhotoUri())).listener(new RequestListener<Uri, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        }).diskCacheStrategy(DiskCacheStrategy.ALL).into(itemImage);
    }
    private DialogInterface.OnClickListener dialogBtnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_order_card);
        super.setActivityForBinder(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        innitWidgets();
        reference = super.database.getReferenceFromUrl(getIntent().getStringExtra("item_ref"));
        reference.addListenerForSingleValueEvent(loadItem);
    }

    private void innitWidgets() {
        itemName = ButterKnife.findById(itemCardLayout, R.id.item_name_label);
        itemName = ButterKnife.findById(itemCardLayout, R.id.item_name_label);
        itemDescr = ButterKnife.findById(itemCardLayout, R.id.item_description);
        itemImage = ButterKnife.findById(itemCardLayout, R.id.item_image);
        progressBar = ButterKnife.findById(itemCardLayout, R.id.progress_bar_item_card);
        setVisibilityGone(itemCardLayout, R.id.item_count_label);
        setVisibilityGone(itemCardLayout, R.id.item_count);
        setVisibilityGone(itemCardLayout, R.id.item_available_status);
        setVisibilityGone(itemCardLayout, R.id.item_price);
        setVisibilityGone(itemCardLayout, R.id.item_add_wish_list);
        setVisibilityGone(itemCardLayout, R.id.buy_btn);
    }

    private void setVisibilityGone(View view,  int id){
        (ButterKnife.findById(view, id)).setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.create_order_btn)
    public void createOrder(){
        if (validateWidgets()){
            DatabaseReference orderRef = super.database.getReference(USER_ORDER_TABLE + "/" + super.userModel.getuID());
            OrderModel orderModel = new OrderModel();
            orderModel.setUserID(super.userModel.getuID());
            orderModel.setUserName(super.userModel.getName());
            orderModel.setUserEmail(super.userModel.getEmail());
            orderModel.setProductID(product.getId());
            orderModel.setAddress(deliveryAddress.getText().toString());
            orderModel.setCount(Integer.valueOf(itemCount.getText().toString()));
            orderModel.setMessage(orderMessage.getText().toString());
            Map<String, Object> newTable = new HashMap<>();
            Map<String, Object> tempMap = orderModel.toMap();
            newTable.put(orderRef.push().getKey(), tempMap);
            orderRef.updateChildren(newTable);
            showAlertDialogOneBtn("Order message", "Order created.", "Close", dialogBtnClickListener);
        }
    }

    private boolean validateWidgets() {
        if (userNameAndEmail.getText().toString().equals("") || deliveryAddress.getText().toString().equals("") || itemCount.getText().toString().equals("0")){
            return false;
        }
        return true;
    }
}
