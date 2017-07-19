package com.example.user.loginwhithfb.activity;

import android.content.Intent;
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
import android.view.Menu;
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

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NavigationDrawerActivity extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{
    @BindArray(R.array.nav_item_activity_titles) String[] activityTitles;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private View navHeader;
    private boolean shouldLoadHomeFRagOnBackPress = true;
    private Handler handler;

    private static final String TAG_HOME = "PRODUCT_CATALOG";
    private static final String TAG_ACCOUNT = "MY_ACCOUNT";
    private static final String TAG_ORDER = "MY_ORDERS";
    private static final String TAG_CHAT = "COMPANY_CHAT";
    private static final String TAG_FAVORITE = "FAVORITE";
    private static final String TAG_NEWS = "NEWS";
    private static final String TAG_INFORMATION = "INFORMATION";
    private static String CURRENT_TAG = TAG_HOME;
    private static int navItemIndex = 0;

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
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        handler = new Handler();
        navHeader = navigationView.getHeaderView(0);
        loadNavHeader();
        setUpNavigationView();
        if (savedInstanceState == null) {
            navItemIndex = 0;
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
        Glide.with(this).load(user.getPhotoUrl()).crossFade().thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(userPhoto);
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
            startActivity(new Intent(NavigationDrawerActivity.this, RegistrationActivity.class));
            finish();
            return;
        }else if (v.getId() == R.id.log_out){
            FirebaseAuth.getInstance().signOut();
        }
        startActivity(new Intent(NavigationDrawerActivity.this, LoginActivity.class));
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
        selectNavMenu();
        setToolbarTitle();
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }
        pendingRunnable.run();
        if (pendingRunnable != null){
            handler.post(pendingRunnable);
        }
        drawer.closeDrawers();
        invalidateOptionsMenu();
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu(){
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(this);
        drawer.addDrawerListener(toggle());
        toggle().syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        if (shouldLoadHomeFRagOnBackPress && navItemIndex != 0) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private Fragment getHomeFragment(){
        Fragment fragment = null;
        if (navItemIndex == 0){
            fragment = new ProductCatalogFragment();
        }
        if (navItemIndex == 1){
            fragment = new MyAccountFragment();
        }
        if (navItemIndex == 2){
            fragment = new MyOrdersFragment();
        }
        if (navItemIndex == 3){
            fragment = new CompanyChatFragment();
        }
        if (navItemIndex == 4){
            fragment = new WishListFragment();
        }
        if (navItemIndex == 5){
            fragment = new NewsFragment();
        }
        if (navItemIndex == 6){
            fragment = new InformationFragment();
        }
        return fragment;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_catalog_items) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
        }
        if (item.getItemId() == R.id.nav_account) {
            navItemIndex = 1;
            CURRENT_TAG = TAG_ACCOUNT;
        }
        if (item.getItemId() == R.id.nav_order) {
            navItemIndex = 2;
            CURRENT_TAG = TAG_ORDER;
        }
        if (item.getItemId() == R.id.nav_messages) {
            navItemIndex = 3;
            CURRENT_TAG = TAG_CHAT;
        }
        if (item.getItemId() == R.id.nav_wish_list) {
            navItemIndex = 4;
            CURRENT_TAG = TAG_FAVORITE;
        }
        if (item.getItemId() == R.id.nav_news) {
            navItemIndex = 5;
            CURRENT_TAG = TAG_NEWS;
        }
        if (item.getItemId() == R.id.nav_tech_support) {
            navItemIndex = 6;
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
