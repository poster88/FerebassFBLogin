package com.example.user.loginwhithfb.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.user.loginwhithfb.R;
import com.example.user.loginwhithfb.fragment.CompanyChatFragment;
import com.example.user.loginwhithfb.fragment.InformationFragment;
import com.example.user.loginwhithfb.fragment.NewsFragment;
import com.example.user.loginwhithfb.fragment.ProductCatalogFragment;
import com.example.user.loginwhithfb.fragment.MyAccountFragment;
import com.example.user.loginwhithfb.fragment.MyOrdersFragment;
import com.example.user.loginwhithfb.fragment.WishListFragment;
import com.example.user.loginwhithfb.other.CircleTransform;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NavigationDrawerActivity extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;


    private View navHeader;
    private Handler handler;
    private Runnable pendingRunnable = new Runnable() {
        @Override
        public void run() {
            Fragment fragment = getHomeFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
            fragmentTransaction.commitAllowingStateLoss();
        }
    };

    private ActionBarDrawerToggle toggle(){
        return new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                updateUI(drawerView);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        setActivityForBinder(this);
        setSupportActionBar(toolbar);
        handler = new Handler();
        navHeader = navigationView.getHeaderView(0);
        loadNavHeader();
        setUpNavigationView();
        if (savedInstanceState == null) {
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }

    private void updateUI(View view){
        TextView userName = ButterKnife.findById(view, R.id.name);
        TextView userEmail = ButterKnife.findById(view, R.id.email);
        ImageView userPhoto = ButterKnife.findById(view, R.id.img_profile);
        userName.setText(user.getDisplayName());
        userEmail.setText(user.getEmail());
        Glide.with(this).load(user.getPhotoUrl()).crossFade().thumbnail(0.5f).bitmapTransform(new CircleTransform(this)).diskCacheStrategy(DiskCacheStrategy.ALL).into(userPhoto);
    }

    private void loadNavHeader() {
        if (user.isAnonymous()){
           innitWidgets();
        }else {
            updateUI(navHeader);
            TextView logOut = ButterKnife.findById(navHeader, R.id.log_out);
            logOut.setVisibility(View.VISIBLE);
            logOut.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.reg_in_form_nav){
            NavigationDrawerActivity.super.startCurActivity(NavigationDrawerActivity.this, RegistrationActivity.class);
            finish();
            return;
        }else if (v.getId() == R.id.log_out){
            FirebaseAuth.getInstance().signOut();
        }
        NavigationDrawerActivity.super.startCurActivity(NavigationDrawerActivity.this, LoginActivity.class);
        finish();
    }

    private void innitWidgets() {
        navHeader.findViewById(R.id.reg_buttons_container).setVisibility(View.VISIBLE);
        navHeader.findViewById(R.id.name).setVisibility(View.GONE);
        navHeader.findViewById(R.id.email).setVisibility(View.GONE);
        navHeader.findViewById(R.id.sign_in_form_nav).setOnClickListener(this);
        navHeader.findViewById(R.id.reg_in_form_nav).setOnClickListener(this);
    }

    private void loadHomeFragment(){
        getSupportActionBar().setTitle(CURRENT_TAG);
        pendingRunnable.run();
        if (pendingRunnable != null){
            handler.post(pendingRunnable);
        }
        drawer.closeDrawers();
        invalidateOptionsMenu();
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(this);
        drawer.addDrawerListener(toggle());
        toggle().syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        if (!CURRENT_TAG.equals(TAG_HOME)) {
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
            return;
        }
        NavigationDrawerActivity.super.exitProgram();
    }

    private Fragment getHomeFragment(){
        Fragment fragment = null;
        if (CURRENT_TAG.equals(TAG_HOME)){
            fragment = new ProductCatalogFragment();
        }
        if (CURRENT_TAG.equals(TAG_ACCOUNT)){
            fragment = new MyAccountFragment();
        }
        if (CURRENT_TAG.equals(TAG_ORDER)){
            fragment = new MyOrdersFragment();
        }
        if (CURRENT_TAG.equals(TAG_CHAT)){
            fragment = new CompanyChatFragment();
        }
        if (CURRENT_TAG.equals(TAG_FAVORITE)){
            fragment = new WishListFragment();
        }
        if (CURRENT_TAG.equals(TAG_NEWS)){
            fragment = new NewsFragment();
        }
        if (CURRENT_TAG.equals(TAG_INFORMATION)){
            fragment = new InformationFragment();
        }
        return fragment;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_catalog_items) {
            CURRENT_TAG = TAG_HOME;
        }
        if (item.getItemId() == R.id.nav_account) {
            CURRENT_TAG = TAG_ACCOUNT;
        }
        if (item.getItemId() == R.id.nav_order) {
            CURRENT_TAG = TAG_ORDER;
        }
        if (item.getItemId() == R.id.nav_messages) {
            CURRENT_TAG = TAG_CHAT;
        }
        if (item.getItemId() == R.id.nav_wish_list) {
            CURRENT_TAG = TAG_FAVORITE;
        }
        if (item.getItemId() == R.id.nav_news) {
            CURRENT_TAG = TAG_NEWS;
        }
        if (item.getItemId() == R.id.nav_tech_support) {
            CURRENT_TAG = TAG_INFORMATION;
        }
        if (item.isChecked()) {
            item.setChecked(false);
        } else {
            item.setChecked(true);
        }
        loadHomeFragment();
        return true;
    }


}
