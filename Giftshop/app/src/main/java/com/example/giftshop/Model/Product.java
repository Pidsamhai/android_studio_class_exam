package com.example.giftshop.Model;

import android.widget.ScrollView;

import java.util.ArrayList;

public class Product extends ArrayList<String> {

    private String u_id;
    private String name;
    private String description;
    private String tel;
    private String picture;
    private String product_id;
    private String price;
    private String facebook_name;
    private String facebook_url;
    private String line_url;
    private String lat;
    private String lon;

    public Product() {}

    public Product(String u_id,String name,String description,String tel,String picture){}
    public Product(
            String u_id,
            String name,
            String description,
            String tel,
            String picture,
            String price,
            String facebook_name,
            String facebook_url,
            String line_url,
            String lat,
            String lon
    ){}


    public String getU_id() {
        return u_id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getTel() {
        return tel;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getPicture() {
        return picture;
    }

    public String getFacebook_name() {
        return facebook_name;
    }

    public String getFacebook_url() {
        return facebook_url;
    }

    public String getLat() {
        return lat;
    }

    public String getLine_url() {
        return line_url;
    }

    public String getLon() {
        return lon;
    }

    public String getPrice() {
        return price;
    }
}
