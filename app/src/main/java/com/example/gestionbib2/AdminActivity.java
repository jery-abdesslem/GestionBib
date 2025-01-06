package com.example.gestionbib2;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gestionbib2.entity.Book;
import com.example.gestionbib2.entity.Loan;
import com.example.gestionbib2.entity.User;
import com.example.gestionbib2.service.BookService;
import com.example.gestionbib2.service.LoanService;
import com.example.gestionbib2.service.UserService;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminActivity extends AppCompatActivity implements UserService.OnUserLoadedListener, BookService.OnBookLoadedListener {
    private UserService userService;
    private LoanService loanService;
    private BookService bookService;
    private ListView loanListView;
    private ArrayAdapter<String> adapter;
    private List<Loan> loanList;
    private List<User> users;
    private List<Book> books;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        loanService = new LoanService();
        bookService = new BookService();
        userService = new UserService();
        // Initialisation des éléments de l'interface
       // listView = findViewById(R.id.listView);
        loanListView = findViewById(R.id.loanListView);


        // Initialisation de l'adaptateur pour la liste
        loanList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        loanListView.setAdapter(adapter);
       // loadPendingLoans();


        Button createBookButton = findViewById(R.id.createBookButton);
        Button listLoanedBooksButton = findViewById(R.id.listLoanedBooksButton);
        Button listNonReturnedBooksButton = findViewById(R.id.listNonReturnedBooksButton);
        Button listUsersButton = findViewById(R.id.listUsersButton);
        Button listAvailableBooksButton = findViewById(R.id.listBooksButton);
        Button loadPendingLoansButton = findViewById(R.id.loadLoansButton);

        createBookButton.setOnClickListener(v -> createBook());
        listLoanedBooksButton.setOnClickListener(v -> listLoanedBooks());
        listNonReturnedBooksButton.setOnClickListener(v -> listNonReturnedBooks());
        listUsersButton.setOnClickListener(v -> listUsers());
        listAvailableBooksButton.setOnClickListener(v -> listAvailableBooks());
        // Gestion des clics sur le bouton pour charger les prêts en attente
        loadPendingLoansButton.setOnClickListener(v -> loadPendingLoans());
        registerForContextMenu(loanListView);
        // Gestion des clics sur les éléments de la liste
       loanListView.setOnItemClickListener((parent, view, position, id) -> {
            if (position < 0 || position >= loanList.size()) {
                Toast.makeText(this, "Données introuvables", Toast.LENGTH_SHORT).show();
                return;
            }

            // Récupérer le prêt sélectionné
            Loan selectedLoan = loanList.get(position);

            // Afficher les détails dans le layout spécifique
            showLoanDetails(selectedLoan);
        });
        registerForContextMenu(loanListView);

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.loanListView) {
            getMenuInflater().inflate(R.menu.context_menu_user_book, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_update) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            if (adapter.getCount() > 0) {
                String selectedItem = (String) loanListView.getAdapter().getItem(info.position);
                if (selectedItem.contains("Livre ID")) {
                    updateBook(selectedItem);
                } else if (selectedItem.contains("Utilisateur ID")) {
                    updateUser(selectedItem);
                }
            }
        }
        return true;
    }

    private void listUsers() {
        userService.getAllUsers(users -> {
            List<String> results = new ArrayList<>();
            for (User user : users) {
                results.add("Utilisateur ID: " + user.getUserId() +"\n" +
                        ", Nom: " + user.getName());
            }
            adapter.clear();
            adapter.addAll(results);
            adapter.notifyDataSetChanged();
        });
    }

    private void listAvailableBooks() {
        bookService.getAllAvailableBooks(books -> {
            List<String> results = new ArrayList<>();
            for (Book book : books) {
                results.add(
                        "Livre ID: " + book.getBookId() + "\n" +
                                "Titre: " + book.getTitle() + "\n" +
                                "Auteur: " + book.getAuthor() + "\n" +
                                "Catégorie: " + book.getCategory()
                );
            }
            adapter.clear();
            adapter.addAll(results);
            adapter.notifyDataSetChanged();
        });
    }

    private void loadPendingLoans() {
        loanService.getPendingLoans(loans -> {
            adapter.clear();
            loanList.clear(); // Mise à jour de la liste interne
            loanList.addAll(loans);
            for (Loan loan : loans) {
                adapter.add("Nom de l'utilisateur : " + loan.getUserName() +
                        "\nTitre du livre : " + loan.getBookTitle());
            }
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Prêts en attente chargés", Toast.LENGTH_SHORT).show();
        });
    }

    private void showLoanDetails(@NonNull Loan loan) {
        // Remplacer le layout actuel par `item_layout`
        setContentView(R.layout.item_loan);

        // Initialiser les vues dans le layout `item_layout`
        TextView userNameTextView = findViewById(R.id.tvUserName);
        TextView bookTitleTextView = findViewById(R.id.tvBookTitle);
        Button approveButton = findViewById(R.id.btnApprove);
        Button rejectButton = findViewById(R.id.btnReject);

        // Afficher les informations du prêt
        userNameTextView.setText("Nom de l'utilisateur : " + loan.getUserName());
        bookTitleTextView.setText("Titre du livre : " + loan.getBookTitle());

        // Gérer les clics sur les boutons
        approveButton.setOnClickListener(v -> {
            loanService.approveLoan(loan.getLoanId());
            Toast.makeText(this, "Prêt approuvé", Toast.LENGTH_SHORT).show();
            reloadAdminLayout();

        });

        rejectButton.setOnClickListener(v -> {
            loanService.rejectLoan(loan.getLoanId());
            Toast.makeText(this, "Prêt refusé", Toast.LENGTH_SHORT).show();
            reloadAdminLayout();
        });
    }

    private void reloadAdminLayout() {
        // Rechargez les données et mettez à jour l'interface utilisateur ici
        setContentView(R.layout.activity_admin);
        loadPendingLoans();
    }



    private void listLoanedBooks() {
        loanService.getLoanedBooks(loans -> {
            List<String> results = new ArrayList<>();
            for (Loan loan : loans) {
                results.add("Book Name: " + loan.getBookId() + ", User Name: " + loan.getUserName());
            }
            adapter.clear();
            adapter.addAll(results);
            adapter.notifyDataSetChanged();
        });
    }

    private void listNonReturnedBooks() {
        loanService.getNonReturnedBooks(loans -> {
            List<String> results = new ArrayList<>();
            for (Loan loan : loans) {
                results.add("Book Name : " + loan.getBookId() + ", User Name: " + loan.getUserName());
            }
            adapter.clear();
            adapter.addAll(results);
            adapter.notifyDataSetChanged();
        });
    }


    private void createBook() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Créer un Livre");

        View view = getLayoutInflater().inflate(R.layout.dialog_create_book, null);
        builder.setView(view);

        EditText titleEditText = view.findViewById(R.id.titleEditText);
        EditText authorEditText = view.findViewById(R.id.authorEditText);
        EditText categoryEditText = view.findViewById(R.id.categoryEditText);

        builder.setPositiveButton("Créer", (dialog, which) -> {
            String title = titleEditText.getText().toString();
            String author = authorEditText.getText().toString();
            String category = categoryEditText.getText().toString();
            String bookId = UUID.randomUUID().toString();

            Book book = new Book(bookId, title, author, category);
            bookService.createBook(book);
            Toast.makeText(AdminActivity.this, "Livre créé", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
    private void updateBook(String bookInfo) {
        try {
            String bookId = bookInfo.split("\n")[0].split(":")[1].trim();
            bookService.getBookById(bookId, this);
        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors de l'extraction de l'ID du livre", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateUser(String userInfo) {
        String userId = userInfo.split(",")[0].split(":")[1].trim();
        userService.getUserById(userId, this);
    }

    @Override
    public void onBookLoaded(Book book) {
        if (book == null) {
            Toast.makeText(this, "Livre introuvable", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Mettre à jour un Livre");

        View view = getLayoutInflater().inflate(R.layout.dialog_create_book, null);
        builder.setView(view);

        // Récupérer les champs du layout
        EditText titleEditText = view.findViewById(R.id.titleEditText);
        EditText authorEditText = view.findViewById(R.id.authorEditText);
        EditText categoryEditText = view.findViewById(R.id.categoryEditText);

        // Assurez-vous que les champs ne sont pas null
        if (titleEditText == null || authorEditText == null || categoryEditText == null) {
            Toast.makeText(this, "Erreur : Layout du dialog incorrect", Toast.LENGTH_SHORT).show();
            return;
        }

        // Remplir les champs avec les données du livre
        titleEditText.setText(book.getTitle());
        authorEditText.setText(book.getAuthor());
        categoryEditText.setText(book.getCategory());

        builder.setPositiveButton("Mettre à jour", (dialog, which) -> {
            String title = titleEditText.getText().toString();
            String author = authorEditText.getText().toString();
            String category = categoryEditText.getText().toString();

            if (title.isEmpty() || author.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Tous les champs doivent être remplis", Toast.LENGTH_SHORT).show();
                return;
            }

            Book updatedBook = new Book(book.getBookId(), title, author, category);
            bookService.updateBook(updatedBook);
            Toast.makeText(AdminActivity.this, "Livre mis à jour", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());

        builder.show();
    }


    @Override
    public void onUserLoaded(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Mettre à jour un Utilisateur");

        View view = getLayoutInflater().inflate(R.layout.dialog_create_user, null);
        builder.setView(view);

        EditText nameEditText = view.findViewById(R.id.nameEditText);
        EditText emailEditText = view.findViewById(R.id.emailEditText);

        nameEditText.setText(user.getName());
        emailEditText.setText(user.getEmail());

        builder.setPositiveButton("Mettre à jour", (dialog, which) -> {
            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();

            User updatedUser = new User(user.getUserId(), name, email);
            userService.updateUser(updatedUser);
            Toast.makeText(AdminActivity.this, "Utilisateur mis à jour", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}
