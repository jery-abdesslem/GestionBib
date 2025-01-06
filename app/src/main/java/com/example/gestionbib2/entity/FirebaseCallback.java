package com.example.gestionbib2.entity;

import java.util.List;

public interface FirebaseCallback {
    void onCallback(List<Loan> loans);
}
