package com.thinkconstructive.rest_demo.model;

public class Author {

    private String author_id;
    private String author_name;

    public Author() {
    }

    public Author(String author_id, String author_name) {
        this.author_id = author_id;
        this.author_name = author_name;
    }

    public String getAuthorId() {
        return author_id;
    }

    public void setAuthorId(String author_id) {
        this.author_id = author_id;
    }

    public String getAuthorName() {
        return author_name;
    }

    public void setAuthorName(String author_name) {
        this.author_name = author_name;
    }
}
