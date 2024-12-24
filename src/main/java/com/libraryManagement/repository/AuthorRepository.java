package com.libraryManagement.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryManagement.model.Author;
import com.libraryManagement.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.BadSqlGrammarException;
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
    private static final Logger logger = LoggerFactory.getLogger(AuthorRepository.class);

    @Autowired
    public AuthorRepository (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //Creating and inserting an authors detail into authors table
    public void createAuthor(Author author) {
        String sql = "INSERT INTO authors (author_id, author_name) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sql, author.getAuthorId(), author.getAuthorName());
            logger.info("Author created Successfully");
        } catch (DuplicateKeyException e) {
            logger.error("Error: authorId is a primary key and it already exists. {}", e.getMessage());
        } catch (DataIntegrityViolationException e) {
            logger.error("Error: Missing required fields: {}", e.getMessage());
        } catch (DataAccessException e) {
            logger.error("Error while creating the author: {}", e.getMessage());
        }
    }

    //Retrieving an author's detail by its id
    public Optional<Author> getAuthor(String authorId) {
        String sql = "SELECT * FROM authors WHERE author_id = ?";
        try {
            logger.info("Executing SQL query to fetch the author with ID: {}", authorId);
            List<Author> authors = jdbcTemplate.query(sql, new AuthorRowMapper(), authorId);
            logger.debug("SQL query executed successfully. Number of authors retrieved: {}", authors.size());
            if (authors.isEmpty()) {
                logger.warn("No author found with Id: {}", authorId);
                return Optional.empty();
            } else {
                Author retrievedAuthor = authors.get(0);
                logger.info("Author retrieved successfully");
                return Optional.of(retrievedAuthor);
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Error occurred while fetching the author", e);
        }
    }

    //Retrieving all the authors present in the database
    public List<Author> getAllAuthor() {
        String sql = "SELECT * FROM authors ORDER BY author_id";
        try {
            logger.info("Fetching all authors from the database");
            List<Author> authors = jdbcTemplate.query(sql, new AuthorRowMapper());
            logger.info("Successfully retrieved {} authors", authors.size());
            return authors;
        } catch (DataAccessException e) {
            logger.error("Error occurred while fetching all authors: {}", e.getMessage());
            throw new RuntimeException("Error occurred while fetching all authors: {}", e);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while fetching all authors: {}", e.getMessage());
            throw new RuntimeException("Unexpected error occurred while fetching all authors", e);
        }
    }

    //Updating the author
    public void updateAuthor(Author author) {
        String sql = "UPDATE authors SET author_name = ? WHERE author_id = ?";
        try {
            jdbcTemplate.update(sql, author.getAuthorName(), author.getAuthorId());
            logger.info("Author updated successfully {}", author);
        } catch (DataIntegrityViolationException e) {
            logger.error("Error: Missing required fields for author '{}' : {}", author, e.getMessage());
        } catch (DataAccessException e) {
            logger.error("Error occurred while updating the author '{}' : {}", author, e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating the author: {}",e.getMessage());
        }
    }

    public void deleteAuthor(String authorId) {
        String sql = "DELETE FROM authors WHERE author_id = ?";
        try {
            jdbcTemplate.update(sql, authorId);
        } catch (BadSqlGrammarException e) {
            logger.error("SQL syntax error while deleting the author: {}", e.getMessage());
        }
        catch (DataAccessException e) {
            logger.error("Error deleting the author with ID '{}'", authorId);
        } catch (Exception e) {
            logger.error("Unexpected error for deleting the author", e);
        }
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
