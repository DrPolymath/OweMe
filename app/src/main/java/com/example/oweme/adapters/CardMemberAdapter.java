package com.example.oweme.adapters;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oweme.R;
import com.example.oweme.models.Member;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CardMemberAdapter extends RecyclerView.Adapter<CardMemberAdapter.CardMemberViewHolder>{

    private ArrayList<Member> memberList;
    private String groupId;

    public CardMemberAdapter(ArrayList<Member> memberList, String groupId) {
        this.memberList = memberList;
        this.groupId = groupId;
    }

    public class CardMemberViewHolder extends RecyclerView.ViewHolder {
        private TextView TV_memberName;
        private Button btn_removeMember;

        public CardMemberViewHolder(final View view) {
            super(view);
            TV_memberName = view.findViewById(R.id.TV_memberName);
            btn_removeMember = view.findViewById(R.id.btn_group_removeMember);
        }

        private void setRemoveMemberEvent(String memberId) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final DocumentReference groupRef = db.collection("group").document(groupId);

            btn_removeMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    groupRef.update("members", FieldValue.arrayRemove(memberId));
                }
            });
        }
    }



    @NonNull
    @Override
    public CardMemberAdapter.CardMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_member, parent, false);
        return new CardMemberAdapter.CardMemberViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardMemberAdapter.CardMemberViewHolder holder, int position) {
        String memberId = memberList.get(position).getMemberName();
        holder.setRemoveMemberEvent(memberId);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(memberId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("retrieve group name", "DocumentSnapshot data: " + document.getData());
                        holder.TV_memberName.setText(document.get("name").toString());
                    } else {
                        Log.d("retrieve group name", "No such document");
                    }
                } else {
                    Log.d("retrieve group name", "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }
}
