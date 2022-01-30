package com.example.oweme.utils;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.oweme.models.Bill;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.RegEx;

public class SharedViewModel extends ViewModel {

    private static final String TAG = "SharedViewModel";
    private MutableLiveData<List<Bill>> mutableBillList = new MutableLiveData<>();
    private MutableLiveData<Bill> mutableBill = new MutableLiveData<>();
    private MutableLiveData<List<String>> idList = new MutableLiveData<>();

    public LiveData<List<Bill>> getBillList() {
        new LoadBill().execute();
        return mutableBillList;
    }

    public LiveData<List<String>> getBillId(){
        return idList;
    }

    class LoadBill extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            List<Bill> bills = new ArrayList<>();
            List<String> ids = new ArrayList<>();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            String userID = mAuth.getCurrentUser().getUid();

            CollectionReference billsRef = db.collection("bill");
            DocumentReference userRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            Query billQueryDebtor = billsRef.whereEqualTo("debtor."+userID+".status","Unpaid");
            Log.d(TAG,FirebaseAuth.getInstance().getCurrentUser().getUid());
            Query billQueryCreditor = billsRef.whereEqualTo("creditor", userRef);

            billQueryCreditor.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document: task.getResult()){
                            String id = document.getId();
                            Bill bill = document.toObject(Bill.class);
                            bills.add(bill);
                            ids.add(id);

                            idList.postValue(ids);
                            mutableBillList.postValue(bills);
                        }

                        Log.d(TAG, String.valueOf(bills.size()));
                    }else{
                        Log.d(TAG, "Data not loaded");
                    }
                }
            });

            billQueryDebtor.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document: task.getResult()){
                            String id = document.getId();
                            Bill bill = document.toObject(Bill.class);
                            bills.add(bill);
                            ids.add(id);

                            idList.postValue(ids);
                            mutableBillList.postValue(bills);
                        }

                        Log.d(TAG, String.valueOf(bills.size()));
                    }else{
                        Log.d(TAG, "Data not loaded");
                    }
                }
            });

        }
    }


}