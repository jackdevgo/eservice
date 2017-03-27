package com.jack.eservice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.Resource;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoginFragment.OnFragmentInteractionListener, LoginFragment.OnLoginokListener,
        HomeFragment.OnFragmentInteractionListener,
        SerialFragment.OnFragmentInteractionListener,
        SocketFragment.OnFragmentInteractionListener {

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private DrawerLayout drawer;
    private FragmentManager fragmentManager;
    private String userid, pwd;
    private boolean loggedin;

    private LoginFragment loginFragment;
    private HomeFragment homeFragment;
    private SerialFragment serialFragment;
    private SocketFragment socketFragment;
    private FirebaseFragment firebaseFragment;
    private SettingFragment settingFragment;

    private TextView tvuserid;
    private NavigationView navigationView;
    private String slanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        //  initFloatingAction();
        initDrawer();
        initLogin();
        initHomeFragment();
    }


    private void initLogin() {
        SharedPreferences set = getSharedPreferences("loginset", MODE_PRIVATE);
        userid = set.getString("userid", "");
        pwd = set.getString("pwd", "");
        loggedin = set.getBoolean("loggedin", false);
        if (loggedin) {
            tvuserid.setText("Hi " + userid + "!");
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
        } else {
            tvuserid.setText(R.string.not_login);
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
        }

    }

    private void initLoginFragment() {
        loginFragment = LoginFragment.newInstance("userid", "pwd");
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction trans = fragmentManager.beginTransaction();
        trans.replace(R.id.content_main, loginFragment);
        trans.addToBackStack(null);
        trans.commit();
    }

    private void initHomeFragment() {
        homeFragment = HomeFragment.newInstance("a", "b");
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction trans = fragmentManager.beginTransaction();
        trans.replace(R.id.content_main, homeFragment).commit();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private void initSerialFragment() {
        serialFragment = SerialFragment.newInstance("a", "b");
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction trans = fragmentManager.beginTransaction();
        trans.replace(R.id.content_main, serialFragment).commit();
    }

    private void initSocketFragment() {
        socketFragment = SocketFragment.newInstance("a", "b");
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction trans = fragmentManager.beginTransaction();
        trans.replace(R.id.content_main, socketFragment).commit();
    }

    private void initFirebaseFragment() {
        firebaseFragment = FirebaseFragment.newInstance("a", "b");
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction trans = fragmentManager.beginTransaction();
        trans.replace(R.id.content_main, firebaseFragment).commit();
    }

    private void initSettingFragment() {
        settingFragment = SettingFragment.newInstance("a", "b");
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction trans = fragmentManager.beginTransaction();
        trans.replace(R.id.content_main, settingFragment).commit();
    }

    private void initDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (navigationView.getHeaderCount() > 0) {
            View header = navigationView.getHeaderView(0);
            tvuserid = (TextView) header.findViewById(R.id.tvuserid);
        }
    }

    private void initFloatingAction() {
//        fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (count == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String lan;

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show();
                initSettingFragment();
                break;
            case R.id.action_english:
                lan = "en";
                writePreference(lan);
                restartapp();
                break;
            case R.id.action_chinese:
                lan = "zh_TW";
                writePreference(lan);
                restartapp();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void writePreference(String lan) {
        SharedPreferences pref = getSharedPreferences("language", MODE_PRIVATE);
        pref.edit()
                .putString("locale", lan)
                .commit();
    }

    private void restartapp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
        finish();
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(0);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                toolbar.setTitle(R.string.home);
                initHomeFragment();
                break;
            case R.id.nav_serial:
                if (!loggedin) {
                    toolbar.setTitle(R.string.login);
                    initLoginFragment();
                } else {
                    toolbar.setTitle(R.string.serial_tracking);
                    initSerialFragment();
                }
                break;
            case R.id.nav_socket:
                if (!loggedin) {
                    toolbar.setTitle(R.string.login);
                    initLoginFragment();
                } else {
                    toolbar.setTitle(R.string.socket);
                    initSocketFragment();
                }
                break;
            case R.id.nav_firebase:
                if (!loggedin) {
                    toolbar.setTitle(R.string.login);
                    initLoginFragment();
                } else {
                    toolbar.setTitle(R.string.firebase);
                    initFirebaseFragment();
                }
                break;
            case R.id.nav_manage:
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_send:
                break;
            case R.id.nav_login:
                initLoginFragment();
                break;
            case R.id.nav_logout:
                doLogoout();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void doLogoout() {
        SharedPreferences set = getSharedPreferences("loginset", MODE_PRIVATE);
        set.edit()
                .clear().commit();
        userid = "";
        pwd = "";
        loggedin = false;
        tvuserid.setText("");
        navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
        navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
        initHomeFragment();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
        if (uri.toString() == "login") {
            loggedin = true;
            onBackPressed();
        }
    }

    @Override
    public void onLoginok(String suserid, String spwd) {
        userid = suserid;
        pwd = spwd;
        tvuserid.setText("Hi " + userid + "!");
        navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences pref = newBase.getSharedPreferences("language", MODE_PRIVATE);
        slanguage = pref.getString("locale", "");
        super.attachBaseContext(MyContextWrapper.wrap(newBase, slanguage));
    }
}
