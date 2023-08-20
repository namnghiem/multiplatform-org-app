package com.trinitysmf.mysmf.ui.activities;

import android.content.Intent;
import android.preference.PreferenceManager;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.trinitysmf.mysmf.R;
import com.trinitysmf.mysmf.adapters.ViewPagerAdapter;
import com.trinitysmf.mysmf.ui.views.CustomViewPager;

public class MainActivity extends AppCompatActivity implements OnTabSelectListener {
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private BottomBar mBottomBar;
    private CustomViewPager mViewPager;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        AndroidThreeTen.init(this);


        auth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_main);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        if (auth.getCurrentUser()== null){
            if( !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("login_skip", false)){
                //if the user wants to browse without signing in
                finish();
            }
            //otherwise, let them continue, but change the signout button to signin
            navigationView.getMenu().findItem(R.id.menu_sign_out).setTitle("Sign In");
            //signing in and signing out basically are the same thing
        }
        //set the toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //set up the indicator
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open_drawer, R.string.close_drawer);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        //set up view pager
        mViewPager = (CustomViewPager) findViewById(R.id.fragment_container);
        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        mViewPager.setPagingEnabled(false);
        mViewPager.setOffscreenPageLimit(3);


        //setup the bottom nav
        mBottomBar = findViewById(R.id.bottom_bar);
        mBottomBar.setOnTabSelectListener(this);
        /*
        //show the first fragment
        if (savedInstanceState == null) {
            // only create fragment if activity is started for the first time
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, HomeFragment.newInstance()).commit();
        }*/
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_sign_out:

                        AuthUI.getInstance()
                                .signOut(MainActivity.this)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // user is now signed out
                                        PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                                                .edit()
                                                .putBoolean("login_skip", false)
                                                .apply();
                                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                });
                        break;
                    case R.id.menu_event_check_in:
                        startActivity(new Intent(MainActivity.this, QrActivity.class));
                }
                return true;
            }
        });
    }



    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onTabSelected(@IdRes int tabId) {
        switch (tabId){
            case R.id.menu_events:
                mViewPager.setCurrentItem(2);
                break;
            case R.id.menu_feed:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.menu_home:
                mViewPager.setCurrentItem(0);
                break;
            //case R.id.menu_news:
                //mViewPager.setCurrentItem(3);
               // break;
        }
    }
}

