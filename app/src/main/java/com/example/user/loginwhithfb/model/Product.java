package com.example.user.loginwhithfb.model;

/**
 * Created by POSTER on 07.08.2017.
 */

public class Product {
    private String id;
    private String name;
    private String desc;
    private String logoUri;
    private int count;
    private double price;
    private boolean availability;

    public Product(String id, String name, String desc, String logoUri, int count, double price, boolean availability) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.logoUri = logoUri;
        this.count = count;
        this.price = price;
        this.availability = availability;
    }

    public Product() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLogoUri() {
        return logoUri;
    }

    public void setLogoUri(String logoUri) {
        this.logoUri = logoUri;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }
}
