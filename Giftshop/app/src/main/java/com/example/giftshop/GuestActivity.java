package com.example.giftshop;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.asksira.loopingviewpager.LoopingViewPager;
import com.example.giftshop.Adapter.LastEventAdapter_loop;
import com.example.giftshop.Adapter.ProductAdapter;
import com.example.giftshop.Model.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rd.PageIndicatorView;

import java.util.List;

public class GuestActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private static String TAG = "GuestActivity";
    private List<Product> products;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView recycle_no_item;
    private AlertDialog _builder;
    private SharedPreferences.Editor editor;
    private LoopingViewPager viewPager_loop;
    private PageIndicatorView pageIndicatorView;
    private RelativeLayout new_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guest);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle("Guest mode. Read only");
        toolbar.setSubtitleTextColor(Color.parseColor("#f6ff00"));
        setSupportActionBar(toolbar);

        swipeRefreshLayout = findViewById(R.id.refresh_layout);
        recycle_no_item = findViewById(R.id.recycle_no_item);
        recyclerView = findViewById(R.id.recycle_product_view);
        viewPager_loop = findViewById(R.id.viewpager_loop);
        pageIndicatorView = findViewById(R.id.pageIndicator);
        new_layout = findViewById(R.id.new_layout);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("GUEST", 0);
        editor = pref.edit();

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        db = FirebaseFirestore.getInstance();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
            }
        });

        swipeRefreshLayout.setRefreshing(true);

        View select_layout = getLayoutInflater().inflate(R.layout.select_language, null);
        _builder = new androidx.appcompat.app.AlertDialog.Builder(GuestActivity.this)
                .setTitle(R.string.select_language)
                .setView(select_layout)
                .setNegativeButton(R.string.cancel, null)
                .create();

        final ImageView btn_en = (ImageView) select_layout.findViewById(R.id.lang_en);
        final ImageView btn_th = (ImageView) select_layout.findViewById(R.id.lang_th);

        btn_en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _builder.dismiss();
                editor.putString("lang", "en");
                editor.commit();
                new androidx.appcompat.app.AlertDialog.Builder(GuestActivity.this)
                        .setTitle(R.string.restart_application)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                restartApp();
                            }
                        })
                        .setNegativeButton(R.string.later, null)
                        .show();

            }
        });
        btn_th.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _builder.dismiss();
                editor.putString("lang", "th");
                editor.commit();
                new androidx.appcompat.app.AlertDialog.Builder(GuestActivity.this)
                        .setTitle(R.string.restart_application)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                restartApp();
                            }
                        })
                        .setNegativeButton(R.string.later, null)
                        .show();
            }
        });


    }

    private void updateNewItem(){
        db.collection("product")
                .orderBy("timestamps", Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        final List<Product> _product;
                        _product = queryDocumentSnapshots.toObjects(Product.class);
                        final LoopingPagerAdapter adapter = new LastEventAdapter_loop(GuestActivity.this, _product, true);
                        if (_product.size() == 0) new_layout.setVisibility(View.GONE);
                        else new_layout.setVisibility(View.VISIBLE);
                        viewPager_loop.setAdapter(adapter);
                        pageIndicatorView.setCount(viewPager_loop.getIndicatorCount());
                        viewPager_loop.setIndicatorPageChangeListener(new LoopingViewPager.IndicatorPageChangeListener() {
                            @Override
                            public void onIndicatorProgress(int selectingPosition, float progress) {

                            }

                            @Override
                            public void onIndicatorPageChange(int newIndicatorPosition) {
                                pageIndicatorView.setSelection(newIndicatorPosition);
                            }
                        });
                    }
                });
    }

    private void updateData() {
        db.collection("product")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        products = queryDocumentSnapshots.toObjects(Product.class);
                        ProductAdapter adapter = new ProductAdapter(GuestActivity.this, products);
                        recyclerView.setAdapter(adapter);
                        Log.e(TAG, "onSuccess: Load Complete");
                        swipeRefreshLayout.setRefreshing(false);
                        if (products.isEmpty()) {
                            recycle_no_item.setVisibility(View.VISIBLE);
                        } else {
                            recycle_no_item.setVisibility(View.GONE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: " + e);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        updateNewItem();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.guest_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.e(TAG, "onOptionsItemSelected: " + item.getItemId());
        switch (item.getItemId()) {
            case R.id.menu_login:
                new AlertDialog.Builder(GuestActivity.this)
                        .setTitle("You want to login?")
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @SuppressLint("ApplySharedPref")
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(GuestActivity.this, LoginActivity.class));
                                editor.putBoolean("guest", false);
                                editor.commit();
                                finish();
                            }
                        }).setNegativeButton(R.string.cancel, null)
                        .show();
                break;
            case R.id.menu_language:
                _builder.show();
                break;
            case R.id.menu_about:
                startActivity(new Intent(this,AboutActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void restartApp() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
