package com.example.user.loginwhithfb;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.user.loginwhithfb.fragment.HomeFragment;
import com.example.user.loginwhithfb.fragment.MyAccountFragment;
import com.example.user.loginwhithfb.fragment.MyOrdersFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NavigationDrawerActivity extends AppCompatActivity {
    @BindArray(R.array.nav_item_activity_titles) String[] activityTitles;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private View navHeader;
    private boolean shouldLoadHomeFRagOnBackPress = true;
    private Handler handler;

    public static int navItemIndex = 0;
    private static final String TAG_HOME = "HOME";
    private static final String TAG_ACCOUNT = "ACCOUNT";
    private static final String TAG_ORDER = "ORDER";
    public static String CURRENT_TAG = TAG_HOME;

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

    private void loadNavHeader() {
        TextView userName = ButterKnife.findById(navHeader, R.id.name);
        TextView userEmail = ButterKnife.findById(navHeader, R.id.email);
        ImageView userPhoto = ButterKnife.findById(navHeader, R.id.img_profile);
        TextView logOut = ButterKnife.findById(navHeader, R.id.log_out);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("EMAAIL STATUS VERIFY: ", "" + user.isEmailVerified());
        if (user != null){
            if (user.isAnonymous()){
                RelativeLayout regBtnContainer = ButterKnife.findById(navHeader, R.id.reg_buttons_container);
                TextView signInBtn = ButterKnife.findById(navHeader, R.id.sign_in_form_nav);
                TextView regBtn = ButterKnife.findById(navHeader, R.id.reg_in_form_nav);

                regBtnContainer.setVisibility(View.VISIBLE);
                userName.setVisibility(View.GONE);
                userEmail.setVisibility(View.GONE);

                regBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new RegistrationFragment()).commit();
                    }
                });

                signInBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                });
                return;
            }
            userName.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());
            Glide.with(this).load(user.getPhotoUrl()).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(userPhoto);
            logOut.setVisibility(View.VISIBLE);
            logOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    Log.d("OUT", "Succes sign out!");
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            });
        }
    }

    private void loadHomeFragment(){
        selectNavMenu();
        setToolbarTitle();
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        Runnable pendingRunnable = new Runnable() {
            @Override
            public void run() {
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        if (pendingRunnable != null){
            handler.post(pendingRunnable);
        }
        drawer.closeDrawers();
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment(){
        Fragment fragment = null;
        if (navItemIndex == 0){
            fragment = new HomeFragment();
        }
        if (navItemIndex == 1){
           fragment = new MyAccountFragment();
        }
        if (navItemIndex == 2){
            fragment = new MyOrdersFragment();
        }
        return fragment;
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu(){
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home){
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_HOME;
                }
                if (item.getItemId() == R.id.nav_account){
                    navItemIndex = 1;
                    CURRENT_TAG = TAG_ACCOUNT;
                }
                if (item.getItemId() == R.id.nav_order){
                    navItemIndex = 2;
                    CURRENT_TAG = TAG_ORDER;
                }
                if (item.isChecked()) {
                    item.setChecked(false);
                }else {
                    item.setChecked(true);
                }
                loadHomeFragment();
                return true;
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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

        if (shouldLoadHomeFRagOnBackPress) {
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }
        super.onBackPressed();
    }
}
