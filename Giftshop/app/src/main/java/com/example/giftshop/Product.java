package com.example.giftshop;

import java.util.List;

public class Product {

    private String uid;
    private String name;
    private String description;
    private String tel;
    //private List<String> picture;

    public Product() {}

    //public Product(String uid,String name,String description,String tel,List<String> picture){}
    public Product(String uid,String name,String description,String tel){}

    public String getUid() {
        return uid;
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

    /*public List<String> getPicture() {
        return picture;
    }

     */
}
