package com.example.gestionbib2.service;

import android.util.Log;

import com.example.gestionbib2.entity.Book;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BookService {
    private FirebaseFirestore db;

    public BookService() {
        db = FirebaseFirestore.getInstance();
    }

    public void createBook(Book book) {
        db.collection("books")
                .document(book.getBookId())
                .set(book)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Book successfully created"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error creating book", e));
    }




    public Task<QuerySnapshot> searchBooksByAuthor(String author) {
        return db.collection("books").whereEqualTo("author", author).get();
    }

    public Task<QuerySnapshot> searchBooksByCategory(String category) {
        return db.collection("books").whereEqualTo("category", category).get();
    }


    public void updateBook(Book book) {
        db.collection("books").document(book.getBookId()).set(book);
    }
    public void getBookById(String bookId, OnBookLoadedListener callback) {
        db.collection("books").document(bookId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Book book = documentSnapshot.toObject(Book.class);
                        callback.onBookLoaded(book);
                    }
                });
    }

    public void getAllAvailableBooks(Consumer<List<Book>> callback) {
        db.collection("books").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Book> books = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);
                        books.add(book);
                    }
                    callback.accept(books);
                });
    }
    public interface OnBookLoadedListener {
        void onBookLoaded(Book book);
    }

}
