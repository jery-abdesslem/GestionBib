package com.example.gestionbib2.entity;

public class Book {
    private String bookId;
    private String title;
    private String author;
    private String category;
    //private boolean isAvailable;

    public Book() {
    }
    public Book(String bookId,String title,  String author, String category/*,boolean isAvailable*/ ) {
        this.bookId = bookId;
        //this.isAvailable = isAvailable;
        this.category = category;
        this.author = author;
        this.title = title;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    /*public boolean isAvailable() {
        return isAvailable;
    }*/

   /* public void setAvailable(boolean available) {
        isAvailable = available;
    }*/

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
