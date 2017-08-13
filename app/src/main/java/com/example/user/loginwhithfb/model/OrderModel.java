package com.example.user.loginwhithfb.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by POSTER on 13.08.2017.
 */

public class OrderModel {
    private String userID;
    private String userName;
    private String userEmail;
    private String productID;
    private String address;
    private int count;
    private String message;

    public OrderModel(String userID, String userName, String userEmail, String productID, String adress, int count, String message) {
        this.userID = userID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.productID = productID;
        this.address = adress;
        this.count = count;
        this.message = message;
    }

    public OrderModel() {
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("userID", userID);
        result.put("userName", userName);
        result.put("productID", productID);
        result.put("address", address);
        result.put("count", count);
        result.put("message", message);
        return result;
    }

    public OrderModel(String adress) {
        this.address = adress;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
