package com.example.oweme.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oweme.R;
import com.example.oweme.models.Bill;

import java.util.List;

public class CardBillAdapter extends RecyclerView.Adapter<CardBillAdapter.CardBillHolder>{
    private List<Bill> mBillList;
    private OnBillListener mOnBillListener;

    public static class CardBillHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        public TextView event;
        public TextView amount;
        public TextView debtor;
        OnBillListener onBillListener;

        public CardBillHolder(@NonNull View itemView,OnBillListener onBillListener) {
            super(itemView);
            event = itemView.findViewById(R.id.TV_event);
            amount = itemView.findViewById(R.id.TV_amount);
            debtor = itemView.findViewById(R.id.TV_debtor);
            this.onBillListener = onBillListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            onBillListener.onBillClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public CardBillHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Getting layout from custom layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_bill, parent, false);
        // Assigning holder to the layout given
        CardBillHolder cvh = new CardBillHolder(v,mOnBillListener);
        return  cvh;
    }

    public CardBillAdapter(List<Bill> billList, OnBillListener onBillListener){
        mBillList = billList;
        mOnBillListener = onBillListener;
    }

    @Override
    public void onBindViewHolder(@NonNull CardBillHolder holder, int position) {
        Bill currentBill = mBillList.get(position);
        holder.event.setText(currentBill.getEvent());
        holder.amount.setText("RM " +currentBill.getAmount());
        holder.debtor.setText(currentBill.getDebtor().size()+ " Debtors");
    }

    @Override
    public int getItemCount() {
        return mBillList.size();
    }

    public interface OnBillListener{
        void onBillClick(int position);
    }
}
