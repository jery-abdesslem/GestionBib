package com.example.gestionbib2;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionbib2.entity.Loan;
import com.example.gestionbib2.entity.LoanAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class LoanListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LoanAdapter adapter;
    private List<Loan> loanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new LoanAdapter(loanList, new LoanAdapter.OnLoanActionListener() {
            @Override
            public void onApprove(Loan loan) {
                updateLoanStatus(loan.getLoanId(), "approved");
            }

            @Override
            public void onReject(Loan loan) {
                updateLoanStatus(loan.getLoanId(), "rejected");
            }
        });

        recyclerView.setAdapter(adapter);

        // Récupérer les prêts depuis Firestore
        getPendingLoans();
    }

    private void getPendingLoans() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection("loans")
                .whereEqualTo("status", "requested")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    loanList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Loan loan = document.toObject(Loan.class);
                        if (loan != null) {
                            loanList.add(loan);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Erreur : " + e.getMessage()));
    }

    private void updateLoanStatus(String loanId, String newStatus) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection("loans").document(loanId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Statut mis à jour avec succès !"))
                .addOnFailureListener(e -> Log.e("Firestore", "Erreur : " + e.getMessage()));
    }
}
