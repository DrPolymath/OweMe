package com.example.oweme.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.oweme.R;
import com.example.oweme.models.Debtor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DebtorAdapter extends BaseAdapter {
    private static final String TAG = "DebtorAdapter";
    private String billId;
    private ArrayList debtorArray;
    private String creditorId;

    public DebtorAdapter(Map<String,Debtor> debtorMap, String billId, String creditorId) {
        debtorArray = new ArrayList();
        debtorArray.addAll(debtorMap.entrySet());
        this.billId = billId;
        this.creditorId = creditorId;

    }

    @Override
    public int getCount() {
        return debtorArray.size();
    }

    @Override
    public Object getItem(int position) {
        return (Map.Entry) debtorArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Map.Entry<String,Debtor> debtor = (Map.Entry<String, Debtor>) getItem(position);
        String debtorID = debtor.getKey();
        Debtor debtorData = debtor.getValue();
        String debtorName = debtorData.getName();
        String debtorStatus = debtorData.getStatus();



        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_debtor, parent, false);

        TextView tv_debtorName =  view.findViewById(R.id.tv_debtorName);
        TextView tv_status =  view.findViewById(R.id.tv_status);
        tv_debtorName.setText(debtorName);
        tv_status.setText(debtorStatus);

        ImageButton btn_approve = view.findViewById(R.id.btn_approve);
        if(debtorData.getStatus().equals("Paid") || !FirebaseAuth.getInstance().getCurrentUser().getUid().equals(creditorId)) {
            btn_approve.setEnabled(false);
            btn_approve.setVisibility(view.GONE);
        }

        btn_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                confirmationDialog(v,debtorID,position);
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                updatePaymentStatus(debtorID,view);
                                debtorData.setStatus("Paid");
                                notifyDataSetChanged();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Are you sure you want to approve this payment ?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();



            }
        });

        return view;
    }


    private void updatePaymentStatus(String userID,View view){
        Map<String,Object> token = new HashMap<>();
        token.put("deviceToken", true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("bill").document(billId).update("debtor."+userID+".status", "Paid").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.d("Updating Data : ", "Successful");
                }
                else
                    Log.d("Updating Data : ", "Failed");
            }
        });
    }
}
