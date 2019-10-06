package com.example.giftshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    FirebaseUser firebaseUser;
    NavigationView navigationView;
    private AlertDialog builder;
    private static final String TAG = "HomeActivity";
    private TextView profile_name,profile_email,t_name;
    private ImageView profile_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);


        LayoutInflater layoutInflater = getLayoutInflater();
        builder = new AlertDialog.Builder(HomeActivity.this)
                .setTitle("Logout...")
                .setView(layoutInflater.inflate(R.layout.progress_dialog, null))
                .setCancelable(false)
                .create();




        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final View headerView = navigationView.getHeaderView(0);
        profile_name = headerView.findViewById(R.id.profile_name);
        profile_email = headerView.findViewById(R.id.profile_email);
        profile_pic = headerView.findViewById(R.id.drawer_profile_pic);

        t_name = findViewById(R.id.t_name);
        t_name.setText(firebaseUser.getDisplayName());

        Button b_profile_setting = headerView.findViewById(R.id.b_profile_setting);



        b_profile_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawer(navigationView);
                Intent intent = new Intent(HomeActivity.this,SettingProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseUser.getDisplayName() !=null && firebaseUser.getPhotoUrl() != null){
            Glide.with(HomeActivity.this)
                    .load(firebaseUser.getPhotoUrl())
                    .into(profile_pic);
            profile_name.setText(firebaseUser.getDisplayName());
            profile_email.setText(firebaseUser.getEmail());
        }
    }

    void fireBaseLogout(){
        FirebaseAuth.getInstance().signOut();
        builder.show();
        final Intent intent = new Intent(HomeActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                builder.dismiss();
                startActivity(intent);
            }
        }, 1000);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_card_view:
                Log.d(TAG, "onNavigationItemSelected: " + item.getTitle());
                break;
            case R.id.menu_recycler_view:
                Log.d(TAG, "onNavigationItemSelected: " + item.getTitle());
                break;
            case R.id.menu_view_pager:
                Log.d(TAG, "onNavigationItemSelected: " + item.getTitle());
                break;
            case R.id.menu_bottom_navigator:
                Log.d(TAG, "onNavigationItemSelected: " + item.getTitle());
                break;
            case R.id.menu_logout:
                fireBaseLogout();
                break;
        }
        drawerLayout.closeDrawer(navigationView);
        return true;
    }

}
