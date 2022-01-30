package com.example.oweme.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.oweme.R;
import com.example.oweme.activities.MainActivity;
import com.example.oweme.adapters.DebtorAdapter;
import com.example.oweme.models.Bill;
import com.example.oweme.models.User;
import com.example.oweme.utils.FirebaseNotificationSender;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class BillDetail extends Fragment {

    private static final String TAG = "BillDetail";
    private String billId;
    private Bill bill;
    private ListView mListView;
    private String creditor;
    private FirebaseAuth mAuth;
    private String creditorDeviceToken;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        mAuth = FirebaseAuth.getInstance();
        billId = getArguments().getString("id");
        bill = (Bill) getArguments().getSerializable("bill");
        return inflater.inflate(R.layout.bill_detail, container, false);

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindingBillHeader(view);
        bindingBillDetail(view);

    }

    private void bindingBillHeader(View view){
        TextView event,date,amount,total_debtor,creditorName;
        event           = view.findViewById(R.id.TV_event);
        date            = view.findViewById(R.id.TV_date);
        amount          = view.findViewById(R.id.TV_amount);
        total_debtor    = view.findViewById(R.id.TV_total_debtor);
        creditorName    = view.findViewById(R.id.tv_creditorName);


        event.setText(bill.getEvent());
        date.setText(bill.getStringDate());
        amount.setText(String.format("RM %.2f", bill.getAmount()));
        total_debtor.setText(bill.getDebtor().size() + " Debtors");
        getUsernameFromID(bill.getCreditor(),creditorName);
    }

    private void bindingBillDetail(View view){
        mListView = view.findViewById(R.id.LV_debtors);
        DebtorAdapter mAdapter = new DebtorAdapter(bill.getDebtor(),billId, bill.getCreditor().getId());
        mListView.setAdapter(mAdapter);

        TextView tv_perPerson = view.findViewById(R.id.tv_perPerson);
        Float split_amount = bill.getAmount()/bill.getDebtor().size();
        tv_perPerson.setText(String.format("RM %.2f per person", split_amount));


        Button btn_pay = view.findViewById(R.id.btn_pay);
        if(bill.getCreditor().getId().equals(mAuth.getCurrentUser().getUid()) ){
            btn_pay.setEnabled(false);
            btn_pay.setVisibility(view.GONE);
        }

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotificationToCreditor(view);
            }
        });



    }

    public void sendNotificationToCreditor(View v){
        String title = "OweMe";
        String message = "Your friend want to settle your payment for \n" + bill.getEvent() +" on " + bill.getStringDate() ;
        FirebaseNotificationSender firebaseNotificationSender = new FirebaseNotificationSender(creditorDeviceToken,title,message
                ,v.getContext().getApplicationContext(), getActivity());
        firebaseNotificationSender.SendNotification();
    }


    private void getUsernameFromID(DocumentReference userRef,TextView tv){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    User user = task.getResult().toObject(User.class);
                    tv.setText(user.getName());
                    creditorDeviceToken = user.getDeviceToken();
                }
            }
        });

//        userCollection.document(uid).addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                tv.setText(value.getString("name"));
//            }
//        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


}