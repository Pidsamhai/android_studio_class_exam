package com.example.giftshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.giftshop.Helper.IntentStringHelper;
import com.example.giftshop.Model.Product;

import java.util.ArrayList;
import java.util.Objects;

public class ProductItemInfoActivity extends AppCompatActivity {

    private ImageView img,i_profile_pic;
    private TextView t_name,t_price,t_description,t_tel,t_facebook_name,t_line_id,t_location,t_profilename;
    private LinearLayout l_facebook,l_line,l_location,l_call;


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
        l_facebook = findViewById(R.id.l_facebook);
        l_line = findViewById(R.id.l_line);
        l_location = findViewById(R.id.l_location);
        l_call = findViewById(R.id.l_call);
        i_profile_pic = findViewById(R.id.img_profile);
        t_profilename = findViewById(R.id.t_profile_name);

        //Get data form intent
        final String profile_pic_url = getIntent().getStringExtra(IntentStringHelper.PROFILE_PIC_URL);
        final String profile_name = getIntent().getStringExtra(IntentStringHelper.PROFILE_NAME);
        final String url = getIntent().getStringExtra(IntentStringHelper.IMAGE_URL);
        final String tel = getIntent().getStringExtra(IntentStringHelper.PRODUCT_TEL);
        final String name = getIntent().getStringExtra(IntentStringHelper.PRODUCT_NAME);
        final String price = getIntent().getStringExtra(IntentStringHelper.PRODUCT_PRICE);
        final String facbook_name = getIntent().getStringExtra(IntentStringHelper.FACEBOOK_NAME);
        final String facebook_url = getIntent().getStringExtra(IntentStringHelper.FACEBOOK_URL);
        Log.e("FACEBOOK URL", "onCreate: " + facebook_url);
        final String line_url = getIntent().getStringExtra(IntentStringHelper.LINE_URL);
        final String lat = getIntent().getStringExtra(IntentStringHelper.LAT);
        final String lon = getIntent().getStringExtra(IntentStringHelper.LON);
        final String description = getIntent().getStringExtra(IntentStringHelper.PRODUCT_DESCRIPTION);

        Log.e("INFO", "onCreate: " + name);
        Log.e("INFO", "onCreate: " + price);
        Log.e("IMG", "onCreate: " + url);


        Double _price = Double.parseDouble(price);

        t_name.setText(name);
        t_price.setText(String.format("%,.2f",_price).replace(".00",""));
        t_description.setText(description);
        t_tel.setText(tel);
        t_location.setText(lat + "," + lon);
        t_facebook_name.setText(facbook_name);
        t_profilename.setText(profile_name);

        if(!profile_name.trim().isEmpty()){
            Glide.with(getApplicationContext())
                    .load(profile_pic_url)
                    .centerCrop()
                    .apply(RequestOptions.circleCropTransform())
                    .into(i_profile_pic);
        }

        if(lat.trim().isEmpty() || lon.trim().isEmpty()){
            l_location.setVisibility(View.GONE);
        }else {
            l_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String strUri = "http://maps.google.com/maps?q=loc:" + lat + "," + lon;
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
                    try{
                        startActivity(intent);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        l_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + tel));
                startActivity(intent);
            }
        });

        if (facbook_name.trim().isEmpty() ){
            l_facebook.setVisibility(View.GONE);
        }else if (!facebook_url.trim().isEmpty()){
            l_facebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String _url = facebook_url;
                    _url = _url.replace("http://","");
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://" + _url));
                    try{
                        startActivity(intent);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        if (line_url.trim().isEmpty()){
            l_line.setVisibility(View.GONE);
        }else {
            l_line.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(line_url));
                    try{
                        startActivity(intent);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),"Error can't open link",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

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
