package com.example.gestionbib2.entity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionbib2.R;
import com.example.gestionbib2.service.LoanService;

import java.util.List;

public class LoanAdapter extends RecyclerView.Adapter<LoanAdapter.LoanViewHolder> {
    private List<Loan> loanList;
    private OnLoanActionListener listener;

    public LoanAdapter(List<Loan> loanList, OnLoanActionListener listener) {
        this.loanList = loanList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LoanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_loan, parent, false);
        return new LoanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LoanViewHolder holder, int position) {
        Loan loan = loanList.get(position);
        holder.tvUserName.setText(loan.getUserName());
        holder.tvBookTitle.setText(loan.getBookTitle());

        holder.btnApprove.setOnClickListener(v -> listener.onApprove(loan));
        holder.btnReject.setOnClickListener(v -> listener.onReject(loan));
    }

    @Override
    public int getItemCount() {
        return loanList.size();
    }

    public static class LoanViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvBookTitle;
        Button btnApprove, btnReject;

        public LoanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }

    public interface OnLoanActionListener {
        void onApprove(Loan loan);
        void onReject(Loan loan);
    }
}
