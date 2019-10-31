package com.example.giftshop.Model;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class Product extends ArrayList<String> {

    private String u_id;
    private String u_name;
    private String u_pic;
    private String name;
    private String description;
    private String tel;
    private String picture;
    private String picture_url;
    private String product_id;
    private String price;
    private String facebook_name;
    private String facebook_url;
    private String line_url;
    private String line_id;
    private String lat;
    private String lon;
    private Timestamp timestamps;

    public Product() {
    }

    public Product(String u_id, String name, String description, String tel, String picture) {
    }

    public Product(
            String u_id,
            String u_name,
            String u_pic,
            String name,
            String description,
            String tel,
            String picture,
            String picture_url,
            String price,
            String facebook_name,
            String facebook_url,
            String line_url,
            String line_id,
            String lat,
            String lon,
            String product_id,
            Timestamp timestamps
    ) {
    }


    public String getU_id() {
        return u_id;
    }

    public String getU_name() {
        return u_name;
    }

    public String getU_pic() {
        return u_pic;
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

    public String getPicture_url() {
        return picture_url;
    }

    public String getLine_id() {
        return line_id;
    }

    public Timestamp getTimestamps() {
        return timestamps;
    }
}
