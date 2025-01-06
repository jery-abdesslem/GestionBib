package com.example.gestionbib2;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestionbib2.entity.Book;
import com.example.gestionbib2.entity.Loan;
import com.example.gestionbib2.entity.User;
import com.example.gestionbib2.service.BookService;
import com.example.gestionbib2.service.LoanService;
import com.example.gestionbib2.service.UserService;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class UserActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private UserService userService;
    private BookService bookService;
    private LoanService loanService;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private AutoCompleteTextView autoCompleteSearch;
    private ArrayAdapter<String> autoCompleteAdapter;
    private List<String> suggestionsList = new ArrayList<>();
    private Spinner spinnerUser;
    private TextView textViewSelection;
    private ImageView imageViewDrapeau;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        userService = new UserService();
        bookService = new BookService();
        loanService = new LoanService();
        spinnerUser = findViewById(R.id.spinnerUser);
        textViewSelection = findViewById(R.id.textViewSelection);
        imageViewDrapeau = findViewById(R.id.imageViewDrapeau);

        setupSpinner();

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listView.setAdapter(adapter);

        Button createUserButton = findViewById(R.id.createUserButton);
        Button requestLoanButton = findViewById(R.id.requestLoanButton);

        createUserButton.setOnClickListener(v -> createUser());
        requestLoanButton.setOnClickListener(v -> requestLoan());

        // Initialiser l'AutoCompleteTextView
        autoCompleteSearch = findViewById(R.id.autoCompleteSearch);

        // Initialiser l'adaptateur pour l'autocomplétion
        autoCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suggestionsList);
        autoCompleteSearch.setAdapter(autoCompleteAdapter);

        // Charger les suggestions depuis Firestore
        loadSuggestionsFromFirestore();

        // Gérer les sélections dans l'AutoCompleteTextView
        autoCompleteSearch.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = autoCompleteAdapter.getItem(position);
            if (selectedItem != null) {
                searchBooksByAuthorOrCategory(selectedItem);
            }
        });

        // Ajouter un TextWatcher pour surveiller les changements de texte
        autoCompleteSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupSpinner() {
        String[] items = {"victor", "denis"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUser.setAdapter(adapter);
        spinnerUser.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = parent.getItemAtPosition(position).toString();
        textViewSelection.setText("Auteur sélectionné : " + selectedItem);

        if (selectedItem.equals("denis")) {
            imageViewDrapeau.setImageResource(R.drawable.denis);
        } else if (selectedItem.equals("victor")) {
            imageViewDrapeau.setImageResource(R.drawable.victor);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        textViewSelection.setText("no author selected");
    }

    private void loadSuggestionsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("books")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String author = document.getString("author");
                        String category = document.getString("category");

                        // Ajouter auteur et catégorie à la liste des suggestions
                        if (author != null && !suggestionsList.contains(author)) {
                            suggestionsList.add(author);
                        }
                        if (category != null && !suggestionsList.contains(category)) {
                            suggestionsList.add(category);
                        }
                    }

                    // Mettre à jour l'adaptateur
                    autoCompleteAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Erreur lors du chargement des suggestions", e));
    }

    private void createUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Créer un Utilisateur");

        View view = getLayoutInflater().inflate(R.layout.dialog_create_user, null);
        builder.setView(view);

        EditText nameEditText = view.findViewById(R.id.nameEditText);
        EditText emailEditText = view.findViewById(R.id.emailEditText);

        builder.setPositiveButton("Créer", (dialog, which) -> {
            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String userId = UUID.randomUUID().toString();

            User user = new User(userId, name, email);
            userService.createUser(user);
            Toast.makeText(UserActivity.this, "Utilisateur créé", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void searchBooksByAuthorOrCategory(String query) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("books")
                .whereEqualTo("author", query)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        // Si aucun auteur ne correspond, chercher par catégorie
                        db.collection("books")
                                .whereEqualTo("category", query)
                                .get()
                                .addOnSuccessListener(this::displayBooks);
                    } else {
                        // Afficher les livres trouvés par auteur
                        displayBooks(queryDocumentSnapshots);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Erreur lors de la recherche des livres", e));
    }

    private void displayBooks(QuerySnapshot querySnapshot) {
        List<String> results = new ArrayList<>();
        for (QueryDocumentSnapshot document : querySnapshot) {
            String title = document.getString("title");
            String author = document.getString("author");
            String category = document.getString("category");
            results.add("Titre: " + title + ", Auteur: " + author + ", Catégorie: " + category);
        }

        // Mettre à jour la ListView
        adapter.clear();
        adapter.addAll(results);
        adapter.notifyDataSetChanged();

        if (results.isEmpty()) {
            Toast.makeText(this, "Aucun livre trouvé", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestLoan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Demander un Prêt");

        View view = getLayoutInflater().inflate(R.layout.dialog_request_loan, null);
        builder.setView(view);

        EditText titleEditText = view.findViewById(R.id.titleEditText);
        EditText nameEditText = view.findViewById(R.id.nameEditText);

        builder.setPositiveButton("Demander", (dialog, which) -> {
            String bookName = titleEditText.getText().toString();
            String userName = nameEditText.getText().toString();
            String loanId = UUID.randomUUID().toString();

            Loan loan = new Loan(loanId, bookName, userName, "requested", new Date(), null, bookName);
            loanService.requestLoan(loan);
            Toast.makeText(UserActivity.this, "Demande de prêt envoyée", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}
