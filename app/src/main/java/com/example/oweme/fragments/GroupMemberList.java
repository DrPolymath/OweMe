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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.oweme.R;
import com.example.oweme.adapters.CardMemberAdapter;
import com.example.oweme.adapters.MemberAddSpinnerAdapter;
import com.example.oweme.models.Member;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupMemberList extends Fragment {
    private TextView TV_GroupList_name;
    private ArrayList<Member> memberList;
    private RecyclerView RV_memberList;

    private AlertDialog d_addFriend;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_member_list, container, false);

        TV_GroupList_name = view.findViewById(R.id.TV_memberList_groupName);
        RV_memberList = view.findViewById(R.id.RV_memberList);
        memberList = new ArrayList<>();
        TV_GroupList_name.setText(getArguments().get("groupName").toString());

        ExtendedFloatingActionButton fab_addFriend = view.findViewById(R.id.fab_addFriend);
        fab_addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAddFriendDialog();
            }
        });

        getGroupData();

        return view;
    }

    private void getGroupData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference userRef = db.collection("group").document(getArguments().get("groupId").toString());
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

                    memberList.clear();

                    if(snapshot.get("members") != null) {
                        List<String> members = (List<String>) snapshot.get("members");
                        Log.d("groupList", "groups: " + members.toString());

                        for(String memberId : members) {
                            memberList.add(new Member(memberId));
                        }
                    }

                    setAdapter();
                } else {
                    Log.d("groupList", "Current data: null");
                }
            }
        });
    }

    private void createAddFriendDialog(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        final View contentPopupView = getLayoutInflater().inflate(R.layout.dialog_add_friend, null);
        d_addFriend = new AlertDialog.Builder(getContext()).setView(contentPopupView).create();
        d_addFriend.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ArrayList<Member> spinner_memberList = new ArrayList<>();
        Spinner spinnerMemberList = contentPopupView.findViewById(R.id.spin_dialogSelectFriend);

        spinner_memberList.add(new Member("Select Friend", ""));

        final DocumentReference thisUser = db.collection("users").document(mAuth.getCurrentUser().getUid());
        thisUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        if(document.get("friends") == null) {
                            spinner_memberList.set(0, new Member("Friendlist emtpy", ""));
                            return;
                        }

                        List<String> friends = (List<String>) document.get("friends");
                        for(String friendId : friends) {
                            final DocumentReference userRef = db.collection("users").document(friendId);
                            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            spinner_memberList.add(new Member(document.getId() ,document.get("name").toString(), document.get("phonenumber").toString()));
                                        } else {
                                            Log.d("spinner:", "No such document");
                                        }
                                    } else {
                                        Log.d("spinner:", "get failed with ", task.getException());
                                    }
                                }
                            });
                        }
                    } else {
                        Log.d("spinner:", "No such document");
                    }
                } else {
                    Log.d("spinner:", "get failed with ", task.getException());
                }
            }
        });

        MemberAddSpinnerAdapter memberAddAdapter = new MemberAddSpinnerAdapter(contentPopupView.getContext(), spinner_memberList);
        spinnerMemberList.setAdapter(memberAddAdapter);

        spinnerMemberList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                memberAddAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button btn_dialogAddFriend = contentPopupView.findViewById(R.id.btn_dialogAddFriendToGroup);

        btn_dialogAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference groupRef = db.collection("group").document(getArguments().get("groupId").toString());
                groupRef.update("members", FieldValue.arrayUnion(spinner_memberList.get(spinnerMemberList.getSelectedItemPosition()).getMemberId()));
                d_addFriend.dismiss();
            }
        });

        d_addFriend.show();
    }

    private void setAdapter() {
        CardMemberAdapter cardMemberAdapter = new CardMemberAdapter(memberList, getArguments().get("groupId").toString());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        RV_memberList.setLayoutManager(layoutManager);
        RV_memberList.setItemAnimator(new DefaultItemAnimator());
        RV_memberList.setAdapter(cardMemberAdapter);
    }
}