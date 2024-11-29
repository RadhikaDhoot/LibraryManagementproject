package com.libraryManagement.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryManagement.model.Author;
import com.libraryManagement.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class AuthorRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AuthorRepository (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //Creating and inserting an authors detail into authors table
    public void createAuthor(Author author) {
        //Validating the author objects
        if(author.getAuthorId() == null || author.getAuthorId().isEmpty()) {
            throw new IllegalArgumentException("Error: Author ID is required as authorId and cannot be null or empty");
        }

        if(author.getAuthorName() == null || author.getAuthorName().isEmpty()) {
            throw new IllegalArgumentException("Error: Author Name is required as authorName and cannot be null or empty");
        }

        String sql = "INSERT INTO authors (author_id, author_name) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sql, author.getAuthorId(), author.getAuthorName());
            System.out.println("Author created Successfully");
        } catch (Exception e) {
            System.err.println("Error while creating the author: " + e.getMessage());
        }
    }

    //Retrieving an author's detail by its id
    public Optional<Author> getAuthor(String authorId) {
        String sql = "SELECT * FROM authors WHERE author_id = ?";
        List<Author> authors = jdbcTemplate.query(sql, new AuthorRowMapper(), authorId);
        return authors.isEmpty() ? Optional.empty() : Optional.of(authors.get(0));
    }

    //Retrieving all the authors present in the database
    public List<Author> getAllAuthor() {
        String sql = "SELECT * FROM authors";
        return jdbcTemplate.query(sql, new AuthorRowMapper());
    }

    //Updating the author
    public void updateAuthor(Author author) {
        //Validating the author objects
        if(author.getAuthorId() == null || author.getAuthorId().isEmpty()) {
            throw new IllegalArgumentException("Error: Author ID is required as authorId and it cannot be null or empty");
        }
        if (author.getAuthorName() == null || author.getAuthorName().isEmpty()) {
            throw new IllegalArgumentException(("Error: Author Name is required as authorName and it cannot be null or empty"));
        }

        String sql = "UPDATE authors SET author_name = ? WHERE author_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, author.getAuthorName(), author.getAuthorId());
            if (rowsAffected == 0) {
                throw new RuntimeException("No author found with ID: " + author.getAuthorId());
            }
        } catch (Exception e) {
            System.err.println("Error updating the author: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Deleting the author
    public void deleteAuthor(String authorId) {
        String sql = "DELETE FROM authors WHERE author_id = ?";
        jdbcTemplate.update(sql, authorId);
    }

    private static class AuthorRowMapper implements RowMapper<Author> {
        @Override
        public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
            Author author = new Author();
            author.setAuthorId(rs.getString("author_id"));
            author.setAuthorName(rs.getString("author_name"));
            return author;
        }
    }
}
