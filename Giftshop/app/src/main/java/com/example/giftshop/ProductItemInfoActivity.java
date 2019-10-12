package com.example.giftshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.giftshop.Helper.IntentStringHelper;
import com.example.giftshop.Model.Product;

import java.util.ArrayList;
import java.util.Objects;

public class ProductItemInfoActivity extends AppCompatActivity {

    private ImageView img;
    private TextView t_name,t_price,t_description,t_tel,t_facebook_name,t_line_id,t_location;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_item_info);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_keyboard_arrow_left_black_24dp));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //Bind id
        img = findViewById(R.id.img);
        t_name = findViewById(R.id.t_name);
        t_price = findViewById(R.id.t_price);
        t_description = findViewById(R.id.t_description);
        t_tel = findViewById(R.id.t_tel);
        t_location = findViewById(R.id.t_location);
        t_facebook_name = findViewById(R.id.t_facebook_name);

        //Get data form intent
        final String url = getIntent().getStringExtra(IntentStringHelper.IMAGE_URL);
        final String tel = getIntent().getStringExtra(IntentStringHelper.PRODUCT_TEL);
        final String name = getIntent().getStringExtra(IntentStringHelper.PRODUCT_NAME);
        final String price = getIntent().getStringExtra(IntentStringHelper.PRODUCT_PRICE);
        final String facbook_name = getIntent().getStringExtra(IntentStringHelper.FACEBOOK_NAME);
        final String facebook_url = getIntent().getStringExtra(IntentStringHelper.FACEBOOK_URL);
        final String line_url = getIntent().getStringExtra(IntentStringHelper.LINE_URL);
        final String lat = getIntent().getStringExtra(IntentStringHelper.LAT);
        final String lon = getIntent().getStringExtra(IntentStringHelper.LON);
        final String description = getIntent().getStringExtra(IntentStringHelper.PRODUCT_DESCRIPTION);

        Log.e("INFO", "onCreate: " + name);
        Log.e("INFO", "onCreate: " + price);



        t_name.setText(name);
        t_price.setText(price);
        t_description.setText(description);
        t_tel.setText(tel);
        t_location.setText(lat + "," + lon);
        t_facebook_name.setText(facbook_name);

        Glide.with(ProductItemInfoActivity.this)
                .load(url)
                .centerCrop()
                .into(img);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
