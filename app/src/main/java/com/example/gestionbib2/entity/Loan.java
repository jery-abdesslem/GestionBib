package com.example.gestionbib2.entity;

import com.google.firebase.firestore.PropertyName;

import java.util.Date;
public class Loan {
    private String loanId;
    private String bookId;
    private String userName;
    private String status; // "requested", "approved", "rejected", "returned"
    private Date loanDate;
    private Date returnDate;

    private String bookTitle;

    public Loan(String loanId, String bookId, String userName, String status, Date loanDate, Date returnDate, String bookTitle) {
        this.loanId = loanId;
        this.bookId = bookId;
        this.userName = userName;
        this.status = status;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.bookTitle = bookTitle;
    }

    public Loan() {
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    @Override
    public String toString() {
        return "Loan{" +
                "loanId='" + loanId + '\'' +
                ", bookId='" + bookId + '\'' +
                ", userName='" + userName + '\'' +
                ", status='" + status + '\'' +
                ", loanDate=" + loanDate +
                ", returnDate=" + returnDate +
                ", bookTitle='" + bookTitle + '\'' +
                '}';
    }
}