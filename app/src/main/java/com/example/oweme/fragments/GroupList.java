package com.example.oweme.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.oweme.R;
import com.example.oweme.adapters.CardGroupAdapter;
import com.example.oweme.models.Group;
import com.example.oweme.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupList extends Fragment {
    private ArrayList<Group> groupList;
    private RecyclerView RV_groupList;

    private AlertDialog.Builder db_addGroup;
    private AlertDialog d_addGroup;

    public GroupList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void createAddGroupDialog(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference userRef = db.collection("users").document(mAuth.getCurrentUser().getUid());

        final View contentPopupView = getLayoutInflater().inflate(R.layout.dialog_add_group, null);
        d_addGroup = new AlertDialog.Builder(getContext()).setView(contentPopupView).create();
        d_addGroup.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button btn_addGroup = contentPopupView.findViewById(R.id.btn_dialogAdd);
        EditText ET_groupName = contentPopupView.findViewById(R.id.ET_groupName);
        TextView TV_title = contentPopupView.findViewById(R.id.textView3);

        btn_addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> data = new HashMap<>();
                data.put("groupName", ET_groupName.getText().toString());
                data.put("members", Arrays.asList(mAuth.getCurrentUser().getUid()));
                DocumentReference newGroupRef = db.collection("group").document();

                // Later...
                newGroupRef.set(data);
                userRef.update("group", FieldValue.arrayUnion(newGroupRef.getId()));

                d_addGroup.dismiss();
            }
        });

        d_addGroup.show();
    }

    private void setAdapter() {
        CardGroupAdapter cardGroupAdapter = new CardGroupAdapter(groupList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        RV_groupList.setLayoutManager(layoutManager);
        RV_groupList.setItemAnimator(new DefaultItemAnimator());
        RV_groupList.setAdapter(cardGroupAdapter);
    }

    private void setGroupInfo() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference userRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("groupList", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {

                    Log.d("groupList", "Current data: " + snapshot.getData());

                    groupList.clear();

                    if(snapshot.get("group") != null) {
                        List<String> groups = (List<String>) snapshot.get("group");
                        Log.d("groupList", "groups: " + groups.toString());

                        for(String groupId : groups) {
                            groupList.add(new Group(groupId));
                        }
                    }

                    setAdapter();
                } else {
                    Log.d("groupList", "Current data: null");
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);
        RV_groupList = view.findViewById(R.id.RV_memberList);
        groupList = new ArrayList<>();
        setGroupInfo();

        ExtendedFloatingActionButton fab_addGroup = view.findViewById(R.id.fab_addFriend);
        fab_addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAddGroupDialog();
            }
        });

        return view;
    }
}