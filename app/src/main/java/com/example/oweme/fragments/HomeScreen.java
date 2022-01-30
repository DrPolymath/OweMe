package com.example.oweme.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oweme.R;
import com.example.oweme.models.User;
import com.example.oweme.utils.FirebaseCloudService;
import com.example.oweme.utils.SharedViewModel;
import com.example.oweme.adapters.CardBillAdapter;
import com.example.oweme.models.Bill;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeScreen extends Fragment implements CardBillAdapter.OnBillListener {

    //Utility
    private AlertDialog.Builder addBill;
    private AlertDialog dialog;
    private FirebaseCloudService firebaseCloudService;

    //DATA
    private SharedViewModel sharedViewModel;
    public List<Bill> billList = new ArrayList<>();
    public List<String> billIdList = new ArrayList<>();
    private User user;

    //RecyclerView
    private RecyclerView mRecyclerView; //The team
    private RecyclerView.Adapter mAdapter; //More like manager
    private RecyclerView.LayoutManager mLayoutManager; //The strategy used by manager

    private void createAddBillDialog(){
        addBill = new AlertDialog.Builder(getContext());
        Calendar calendar = Calendar.getInstance();
        final View contentPopupView = getLayoutInflater().inflate(R.layout.dialog_add_bill, null);

        // Init view ID
        addBill.setView(contentPopupView);
        EditText et_date = contentPopupView.findViewById(R.id.et_date);

        // Create Dialog
        dialog = addBill.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        // Display Dialog



        // Function
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String format = "dd/MM/yy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.UK);
                et_date.setText(dateFormat.format(calendar.getTime()));
            }
        };

        et_date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new DatePickerDialog(contentPopupView.getContext(),date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        dialog.show();
    }



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        return  inflater.inflate(R.layout.homescreen, container, false);
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindingAccountInfo(view);

        // [START RecyclerView]
        mRecyclerView = view.findViewById(R.id.RV_memberList);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        mAdapter = new CardBillAdapter(billList,this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        // [END RecyclerView]

        // [START Dialog Add Bill]
        FloatingActionButton fab_addBill = view.findViewById(R.id.fab_addBill);
        fab_addBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAddBillDialog();
            }
        });

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getBillList().observe(getViewLifecycleOwner(), new Observer<List<Bill>>() {
            @Override
            public void onChanged(List<Bill> bills) {
                billList.clear();
                billList.addAll(bills);
                mAdapter.notifyDataSetChanged();

            }
        });

        sharedViewModel.getBillId().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> ids) {
                billIdList.clear();
                billIdList.addAll(ids);
            }
        });


    }

    private void bindingAccountInfo(View view){
        TextView username = view.findViewById(R.id.TV_username);
        TextView balance = view.findViewById(R.id.TV_GroupList_name);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    User user = task.getResult().toObject(User.class);
                    username.setText(user.getName());
                }
            }
        });

        DocumentReference userRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        db.collection("bill").whereEqualTo("creditor",userRef).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    float debt = 0;
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        Bill bill = documentSnapshot.toObject(Bill.class);
                        debt = debt - bill.getAmount();
                        Log.d("HomeScreen",""+debt);
                    }
                    balance.setText("RM "+ debt);
                }
            }
        });

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onBillClick(int position) {
//        billList.get(position);
        Bill bill = billList.get(position);
        String id = billIdList.get(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("bill", bill);
        bundle.putString("id",id);
        NavHostFragment.findNavController(HomeScreen.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment,bundle);
    }
}