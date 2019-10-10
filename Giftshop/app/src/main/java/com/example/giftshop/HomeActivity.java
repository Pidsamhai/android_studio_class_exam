package com.example.giftshop;

import android.animation.Animator;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    FirebaseUser firebaseUser;
    NavigationView navigationView;
    private AlertDialog builder;
    private static final String TAG = "HomeActivity";
    private TextView profile_name, profile_email, recycle_no_item;
    private ImageView profile_pic;
    private FirebaseFirestore db;
    private List<Product> products;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GoogleSignInOptions gso;
    private GoogleSignInClient googleSignInClient;
    private static Integer ADDPRODUCT_REQUEST_CODE = 10;
    private ViewPager viewPager;
    private WormDotsIndicator indicator;
    private RelativeLayout new_layout;
    private Button b_close;

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
        db = FirebaseFirestore.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        final View headerView = navigationView.getHeaderView(0);
        profile_name = headerView.findViewById(R.id.profile_name);
        profile_email = headerView.findViewById(R.id.profile_email);
        profile_pic = headerView.findViewById(R.id.drawer_profile_pic);
        recyclerView = findViewById(R.id.recycle_product_view);
        swipeRefreshLayout = findViewById(R.id.refresh_layout);
        recycle_no_item = findViewById(R.id.recycle_no_item);
        viewPager = findViewById(R.id.last_event);
        indicator = findViewById(R.id.dots_indicator);
        b_close = findViewById(R.id.b_close);
        new_layout = findViewById(R.id.new_layout);

        b_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new_layout.setVisibility(View.GONE);
            }
        });

        db.collection("product")
                .limit(3)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        final List<Product> _product;
                        _product = queryDocumentSnapshots.toObjects(Product.class);
                        final LastEventAdapter adapter = new LastEventAdapter(HomeActivity.this,_product);
                        viewPager.setAdapter(adapter);
                        indicator.setViewPager(viewPager);
                    }
                });






        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        upDateData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                upDateData();
            }
        });

        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );


        Button b_profile_setting = headerView.findViewById(R.id.b_profile_setting);


        b_profile_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawer(navigationView);
                Intent intent = new Intent(HomeActivity.this, SettingProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseUser.getDisplayName() != null && firebaseUser.getPhotoUrl() != null) {
            Glide.with(HomeActivity.this)
                    .load(firebaseUser.getPhotoUrl())
                    .centerCrop()
                    .apply(RequestOptions.circleCropTransform())
                    .into(profile_pic);
            profile_name.setText(firebaseUser.getDisplayName());
            profile_email.setText(firebaseUser.getEmail());
        }
    }



    void fireBaseLogout() {
        FirebaseAuth.getInstance().signOut();
        builder.show();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut();
        final Intent intent = new Intent(HomeActivity.this, MainActivity.class);
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

    private void upDateData() {
        swipeRefreshLayout.setRefreshing(true);
        db.collection("product")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        products = queryDocumentSnapshots.toObjects(Product.class);
                        ProductAdapter adapter = new ProductAdapter(HomeActivity.this, products);
                        recyclerView.setAdapter(adapter);
                        Log.e(TAG, "onSuccess: Load Complete");
                        swipeRefreshLayout.setRefreshing(false);
                        new_layout.setVisibility(View.VISIBLE);
                        if (products.isEmpty()) {
                            recycle_no_item.setVisibility(View.VISIBLE);
                        } else {
                            recycle_no_item.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_card_view:
                Log.d(TAG, "onNavigationItemSelected: " + item.getTitle());
                break;
            case R.id.menu_add_product:
                Intent intent = new Intent(HomeActivity.this, AddProductActivity.class);
                startActivityForResult(intent, ADDPRODUCT_REQUEST_CODE);
                break;
            case R.id.menu_my_product:
                Intent my_product = new Intent(HomeActivity.this, MyProductActivity.class);
                startActivityForResult(my_product, ADDPRODUCT_REQUEST_CODE);
                break;
            case R.id.menu_bottom_navigator:
                Log.d(TAG, "onNavigationItemSelected: " + item.getTitle());
                break;
            case R.id.menu_logout:
                fireBaseLogout();
                break;
        }
        drawerLayout.closeDrawer(navigationView);
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADDPRODUCT_REQUEST_CODE) {
            upDateData();
        }
    }
}
