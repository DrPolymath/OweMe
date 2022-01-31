package com.example.oweme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.oweme.R;
import com.example.oweme.models.Member;

import java.util.ArrayList;

public class MemberAddSpinnerAdapter extends ArrayAdapter<Member> {

    public MemberAddSpinnerAdapter(Context context, ArrayList<Member> memberList) {
        super(context, 0, memberList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.member_add_spinner, parent, false);
        }

        TextView TV_memberName = convertView.findViewById(R.id.TV_spinner_memberName);
        TextView TV_phoneNumber = convertView.findViewById(R.id.TV_spinner_phoneNumber);

        Member currentMember = getItem(position);

        if (currentMember != null) {
            TV_memberName.setText(currentMember.getMemberName());
            TV_phoneNumber.setText(currentMember.getPhoneNumber());
        }

        return convertView;
    }
}
