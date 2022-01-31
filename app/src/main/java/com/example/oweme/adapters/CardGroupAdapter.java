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
import com.example.oweme.models.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CardGroupAdapter extends RecyclerView.Adapter<CardGroupAdapter.CardGroupViewHolder> {
    private ArrayList<Group> groupList;

    public CardGroupAdapter(ArrayList<Group> groupList) {
        this.groupList = groupList;
    }

    public class CardGroupViewHolder extends RecyclerView.ViewHolder {
        private TextView groupNameText;
        private Button btn_groupDetails;

        public CardGroupViewHolder(final View view) {
            super(view);
            groupNameText = view.findViewById(R.id.TV_groupName);
            btn_groupDetails = view.findViewById((R.id.btn_groupDetails));
        }

        private void setCardGroupOnClickDetails(String groupId) {
            btn_groupDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("groupId", groupId);
                    bundle.putString("groupName", groupNameText.getText().toString());

                    Navigation.findNavController(itemView).navigate(R.id.action_groupList_to_groupMemberList, bundle);
                }
            });
        }
    }

    @NonNull
    @Override
    public CardGroupAdapter.CardGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_group, parent, false);
        return new CardGroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardGroupAdapter.CardGroupViewHolder holder, int position) {
        String groupID = groupList.get(position).getGroupName();
        holder.setCardGroupOnClickDetails(groupID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("group").document(groupID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("retrieve group name", "DocumentSnapshot data: " + document.getData());
                        holder.groupNameText.setText(document.get("groupName").toString());
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
        return groupList.size();
    }
}
