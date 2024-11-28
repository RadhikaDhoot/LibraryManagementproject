package com.libraryManagement.model;

import java.util.Map;

public class Book {

    private String bookId;
    private String bookAuthor;
    private String bookTitle;
    private Map<String, Object> bookDetail;

    public Book () {
    }

    public Book (String bookId, String bookAuthor, String bookTitle, Map<String, Object> bookDetail) {
        this.bookId = bookId;
        this.bookAuthor = bookAuthor;
        this.bookTitle = bookTitle;
        this.bookDetail = bookDetail;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public Map<String, Object> getBookDetail() {
        return bookDetail;
    }

    public void setBookDetail(Map<String, Object> bookDetail) {
        this.bookDetail = bookDetail;
    }

}
