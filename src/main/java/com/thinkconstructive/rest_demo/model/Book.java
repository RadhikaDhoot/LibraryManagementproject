package com.thinkconstructive.rest_demo.model;

public class Book {

    private String book_id;
    private String book_author;
    private String book_title;
    private String book_detail;

    public Book () {
    }

    public Book (String book_id, String book_author, String book_title, String book_detail) {
        this.book_id = book_id;
        this.book_author = book_author;
        this.book_title = book_title;
        this.book_detail = book_detail;
    }

    public String getBookId() {
        return book_id;
    }

    public void setBookId(String book_id) {
        this.book_id = book_id;
    }

    public String getBookAuthor() {
        return book_author;
    }

    public void setBookAuthor(String book_author) {
        this.book_author = book_author;
    }

    public String getBookTitle() {
        return book_title;
    }

    public void setBookTitle(String book_title) {
        this.book_title = book_title;
    }

    public String getBookDetail() {
        return book_detail;
    }

    public void setBookDetail(String book_detail) {
        this.book_detail = book_detail;
    }




}
