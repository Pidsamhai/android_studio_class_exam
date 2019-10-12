package com.example.giftshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.giftshop.Helper.IntentStringHelper;
import com.example.giftshop.Model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditProductActivity extends AppCompatActivity {

    private EditText e_product_name, e_product_detail, e_product_tel;
    private ImageView img_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Product");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_24dp));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        String img_url = getIntent().getStringExtra(IntentStringHelper.IMAGE_URL);
        String name = getIntent().getStringExtra(IntentStringHelper.PRODUCT_NAME);
        String description = getIntent().getStringExtra(IntentStringHelper.PRODUCT_DESCRIPTION);
        String tel = getIntent().getStringExtra(IntentStringHelper.PRODUCT_TEL);

        e_product_name = findViewById(R.id.e_product_name);
        e_product_detail = findViewById(R.id.e_product_detail);
        e_product_tel = findViewById(R.id.e_product_tel);
        img_layout = findViewById(R.id.img_layout);

        Log.e("IMAGE URL", "onCreate: " + img_url);

        e_product_name.setText(name);
        e_product_detail.setText(description);
        e_product_tel.setText(tel);

        Glide.with(getApplicationContext())
                .load(img_url)
                .centerCrop()
                .into(img_layout);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
