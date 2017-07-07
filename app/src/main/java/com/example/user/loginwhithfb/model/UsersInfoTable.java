package com.example.user.loginwhithfb.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by POSTER on 28.06.2017.
 */

public class UsersInfoTable {
    private String name;
    private String lastName;
    private String surname;
    private String photoUrl;
    private int mobileNumber;
    private String email;
    private String uID;
    private boolean emailVerifiedStatus;

    public UsersInfoTable() {

    }

    public UsersInfoTable(String name, String lastName, String surname, String photoUrl, int mobileNumber, String email, String uID, boolean emailVerifiedStatus) {
        this.name = name;
        this.lastName = lastName;
        this.surname = surname;
        this.photoUrl = photoUrl;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.uID = uID;
        this.emailVerifiedStatus = emailVerifiedStatus;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("lastName", lastName);
        result.put("surname", surname);
        result.put("photoUrl", photoUrl);
        result.put("mobileNumber", mobileNumber);
        result.put("email", email);
        result.put("uID", uID);
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(int mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public boolean getEmailVerifiedStatus() {
        return emailVerifiedStatus;
    }

    public void setEmailVerifiedStatus(boolean emailVerifiedStatus) {
        this.emailVerifiedStatus = emailVerifiedStatus;
    }
}
