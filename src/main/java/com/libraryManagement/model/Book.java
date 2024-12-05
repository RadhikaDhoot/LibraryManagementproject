package com.libraryManagement.model;
import com.fasterxml.jackson.databind.JsonNode;

public class Book {

    private String bookId;
    private String bookAuthor;
    private String bookTitle;
    private JsonNode bookDetail;

    public Book () {
    }

    public Book (String bookId, String bookAuthor, String bookTitle, JsonNode bookDetail) {
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

    public JsonNode getBookDetail() {
        return bookDetail;
    }

    public void setBookDetail(JsonNode bookDetail) {
        this.bookDetail = bookDetail;
    }

}
