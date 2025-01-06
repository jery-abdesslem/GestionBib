package com.example.gestionbib2.service;

import android.util.Log;

import com.example.gestionbib2.entity.Loan;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LoanService {
    private FirebaseFirestore db;

    public LoanService() {
        db = FirebaseFirestore.getInstance();
    }

    public void getPendingLoans(Consumer<List<Loan>> callback) {
        db.collection("loans").whereEqualTo("status", "requested").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Loan> loans = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Loan loan = document.toObject(Loan.class);
                        loans.add(loan);
                    }
                    callback.accept(loans);
                });
    }

    public void approveLoan(String loanId) {
        db.collection("loans").document(loanId)
                .update("status", "approved")
                .addOnSuccessListener(aVoid -> Log.d("LoanService", "Prêt approuvé avec succès"))
                .addOnFailureListener(e -> Log.e("LoanService", "Erreur lors de l'approbation du prêt", e));
    }

    public void rejectLoan(String loanId) {
        db.collection("loans").document(loanId)
                .update("status", "rejected")
                .addOnSuccessListener(aVoid -> Log.d("LoanService", "Prêt refusé avec succès"))
                .addOnFailureListener(e -> Log.e("LoanService", "Erreur lors du refus du prêt", e));
    }


    public void getLoanedBooks(Consumer<List<Loan>> callback) {
        db.collection("loans").whereEqualTo("status", "approved").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Loan> loans = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Loan loan = document.toObject(Loan.class);
                        loans.add(loan);
                    }
                    callback.accept(loans);
                });
    }

    public void getNonReturnedBooks(Consumer<List<Loan>> callback) {
        db.collection("loans").whereEqualTo("status", "approved").whereEqualTo("returnDate", null).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Loan> loans = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Loan loan = document.toObject(Loan.class);
                        loans.add(loan);
                    }
                    callback.accept(loans);
                });
    }

    public void requestLoan(Loan loan) {
        db.collection("loans").document(loan.getLoanId()).set(loan);
    }

}
