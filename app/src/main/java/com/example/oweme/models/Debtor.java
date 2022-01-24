package com.example.oweme.models;

import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class Debtor extends HashMap<String, Object> {

    private Map<String,Object> debtor;
    private Object amount;
    private Object status;
    private Object name;

    public Debtor() {
    }

    public Debtor(Map<String, Object> debtor) {
        this.debtor = debtor;
        this.amount = debtor.get("amount");
        this.status = debtor.get("status");
        this.name = debtor.get("name");
    }

    public Map<String, Object> getDebtor() {
        return debtor;
    }

    public void setDebtor(Map<String, Object> debtor) {
        this.debtor = debtor;
    }

    public Float getAmount() {
        return Float.parseFloat(amount.toString());
    }

    public void setAmount(Object amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status.toString();
    }

    public void setStatus(Object status) {
        this.status = status;
    }

    public String getName() {
        return name.toString();
    }

    public void setName(Object name) {
        this.name = name;
    }
}
