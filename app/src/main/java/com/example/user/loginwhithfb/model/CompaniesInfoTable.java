package com.example.user.loginwhithfb.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by POSTER on 12.07.2017.
 */

public class CompaniesInfoTable {
    private String companyId;
    private String companyName;
    private String companyDescr;
    private String companyLogoUri;
    private Object companyProducts;
    private Object positions;

    public CompaniesInfoTable() {
    }

    public CompaniesInfoTable(String id, String companyName, String companyDescr, String companyLogoUri, Object companyProducts, Object positions) {
        this.companyId = id;
        this.companyName = companyName;
        this.companyDescr = companyDescr;
        this.companyLogoUri = companyLogoUri;
        this.companyProducts = companyProducts;
        this.positions = positions;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("companyId", companyId);
        result.put("companyName", companyName);
        result.put("companyDescr", companyDescr);
        result.put("companyLogoUri", companyLogoUri);
        result.put("companyProducts", companyProducts);
        result.put("positions", positions);
        return result;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String id) {
        this.companyId = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyDescr() {
        return companyDescr;
    }

    public void setCompanyDescr(String companyDescr) {
        this.companyDescr = companyDescr;
    }

    public String getCompanyLogoUri() {
        return companyLogoUri;
    }

    public void setCompanyLogoUri(String companyLogoUri) {
        this.companyLogoUri = companyLogoUri;
    }

    public Object getCompanyProducts() {
        return companyProducts;
    }

    public void setCompanyProducts(Object companyProducts) {
        this.companyProducts = companyProducts;
    }

    public Object getPositions() {
        return positions;
    }

    public void setPositions(Object positions) {
        this.positions = positions;
    }
}
