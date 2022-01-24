package com.example.oweme.models;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ArrayAdapter;

import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.security.PrivilegedAction;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class Bill implements Serializable {

    private DocumentReference creditor;
    private Map<String,Debtor> debtor;
    private @ServerTimestamp Date date;
    private Float amount;
    private String event;

    public Bill() {

    }

    public DocumentReference getCreditor() {
        return creditor;
    }

    public void setCreditor(DocumentReference creditor) {
        this.creditor = creditor;
    }

    public Map<String, Debtor> getDebtor() {
        return debtor;
    }

    public void setDebtor(Map<String, Debtor> debtor) {
        this.debtor = debtor;
    }



    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getStringDate(){
        SimpleDateFormat spf = new SimpleDateFormat("MMM dd, yyyy");
        String strdate = spf.format(date);
        return strdate;
    }

}
