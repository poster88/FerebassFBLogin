package com.example.user.loginwhithfb.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by POSTER on 06.07.2017.
 */

public class RequestToAddClientToCompaniesTable {
    private String id;
    private String dataRequest;
    private String clientUid;
    private String companyName;
    private String contactPersonUid;
    private String statusRequest;

    public RequestToAddClientToCompaniesTable() {
    }

    public RequestToAddClientToCompaniesTable(String id, String dataRequest, String clientUid, String companyName, String contactPersonUid, String statusRequest) {
        this.id = id;
        this.dataRequest = dataRequest;
        this.clientUid = clientUid;
        this.companyName = companyName;
        this.contactPersonUid = contactPersonUid;
        this.statusRequest = statusRequest;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("id" , id);
        result.put("dataRequest" , dataRequest);
        result.put("clientUid" , clientUid);
        result.put("companyName" , companyName);
        result.put("contactPersonUid" , contactPersonUid);
        result.put("statusRequest" , statusRequest);
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDataRequest() {
        return dataRequest;
    }

    public void setDataRequest(String dataRequest) {
        this.dataRequest = dataRequest;
    }

    public String getClientUid() {
        return clientUid;
    }

    public void setClientUid(String clientUid) {
        this.clientUid = clientUid;
    }

    public String getContactPersonUid() {
        return contactPersonUid;
    }

    public void setContactPersonUid(String contactPersonUid) {
        this.contactPersonUid = contactPersonUid;
    }

    public String getStatusRequest() {
        return statusRequest;
    }

    public void setStatusRequest(String statusRequest) {
        this.statusRequest = statusRequest;
    }
}
