package com.example.gestionbib2.service;

import com.example.gestionbib2.entity.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class UserService {
    private FirebaseFirestore db;

    public UserService() {
        db = FirebaseFirestore.getInstance();
    }

    public void createUser(User user) {
        db.collection("users").document(user.getUserId()).set(user);
    }

    public void updateUser(User user) {
        db.collection("users").document(user.getUserId()).set(user);
    }

    public Task<DocumentSnapshot> getUserById(String userId) {
        return db.collection("users").document(userId).get();
    }

    public void getUserById(String userId, OnUserLoadedListener callback) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        callback.onUserLoaded(user);
                    }
                });
    }

    public void getAllUsers(Consumer<List<User>> callback) {
        db.collection("users").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        User user = document.toObject(User.class);
                        users.add(user);
                    }
                    callback.accept(users);
                });
    }

    public interface OnUserLoadedListener {
        void onUserLoaded(User user);
    }
}

