package com.example.giftshop;

import java.util.List;

public class Product {

    private String u_id;
    private String name;
    private String description;
    private String tel;
    private String picture;
    private String product_id;

    public Product() {}

    public Product(String u_id,String name,String description,String tel,String picture){}


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
}
