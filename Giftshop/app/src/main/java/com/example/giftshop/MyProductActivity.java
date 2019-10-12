package com.example.giftshop;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.giftshop.Adapter.EditProductAdapter;
import com.example.giftshop.Model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MyProductActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private Button b_edit, b_delete;
    private AlertDialog.Builder alertDialog;
    private TextView recycle_no_item;
    private List<Product> products;
    private static final String TAG = "MyProductActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("My Product");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);


        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        recyclerView = findViewById(R.id.recycle_product_view);
        swipeRefreshLayout = findViewById(R.id.refresh_layout);
        recycle_no_item = findViewById(R.id.recycle_no_item);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        upDateData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                upDateData();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void upDateData() {
        swipeRefreshLayout.setRefreshing(true);
        db.collection("product")
                .whereEqualTo("u_id", firebaseUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        products = queryDocumentSnapshots.toObjects(Product.class);
                        EditProductAdapter adapter = new EditProductAdapter(MyProductActivity.this, MyProductActivity.this, products);
                        recyclerView.setAdapter(adapter);
                        Log.e(TAG, "onSuccess: Load Complete" + products.size());
                        swipeRefreshLayout.setRefreshing(false);
                        if (products.isEmpty()){
                            recycle_no_item.setVisibility(View.VISIBLE);
                        }else{
                            recycle_no_item.setVisibility(View.GONE);
                        }
                    }
                });
    }
}
