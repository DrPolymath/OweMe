package com.example.oweme.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.oweme.R;

public class GroupMemberList extends Fragment {

    private TextView TV_GroupList_name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_member_list, container, false);

        TV_GroupList_name = view.findViewById(R.id.TV_GroupList_name);

        TV_GroupList_name.setText(getArguments().get("groupId").toString());
        return view;
    }
}