package com.libraryManagement.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.libraryManagement.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class BookRepository  {
    private static final Logger logger = LoggerFactory.getLogger(BookRepository.class);

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public BookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //Inserting or Creating new book into the database
    public void createBook(Book book) {
        String sql = "INSERT INTO books(book_id, book_author, book_title, book_detail) VALUES (?, ?, ?, ?::jsonb)";
        logger.info("Book details to insert - bookId: {}, bookAuthor: {}, bookTitle: {}, bookDetail: {}",
                book.getBookId(), book.getBookAuthor(), book.getBookTitle(), book.getBookDetail());
        try {
            jdbcTemplate.update(sql, book.getBookId(), book.getBookAuthor(), book.getBookTitle(), book.getBookAuthor());
            logger.info("SQL executed successfully. Book created in the database");
        } catch (Exception e) {
            logger.error("Error while creating the book: {}", e.getMessage(), e);
            e.printStackTrace();
        }
    }

    //Retrieving the books by I'd
    public Optional<Book> getBook(String bookId) {
        String sql = "SELECT * FROM books where book_id = ?";
        logger.info("Executing SQL query to fetch the book ID: {}", bookId);
        List<Book> books = jdbcTemplate.query(sql, new BookRowMapper(), bookId);
        logger.debug("SQL query executed successfully. Number of books retrieved: {}", books.size());
        if(books.isEmpty()) {
            logger.warn("No book found with Id: {}", bookId);
            return Optional.empty();
        } else {
            Book retrievedBook = books.get(0);
            logger.info("Book retrieved successfully: {}", retrievedBook);
            return Optional.of(retrievedBook);
        }
    }

    //Retrieving all the books available in the database
    public List<Book> getAllBooks() {
        String sql = "SELECT * FROM books ORDER BY book_id";
        return jdbcTemplate.query(sql, new BookRowMapper());
    }

    //Updating an existing book
    public void updateBook(Book book) {
        String sql = "UPDATE books SET book_author = ?, book_title = ?, book_detail = ?::jsonb WHERE book_id = ?";
        try {

            int rowsAffected = jdbcTemplate.update(sql, book.getBookAuthor(), book.getBookTitle(), book.getBookDetail(), book.getBookId());
            if (rowsAffected == 0) {
                throw new RuntimeException("No book found with ID: " + book.getBookId());
            }
        } catch (Exception e) {
            System.err.println("Error updating the book:" + e.getMessage());
            e.printStackTrace();
        }
    }

    //Deleting a book from database
    public void deleteBook(String bookId) {
        String sql = "DELETE FROM books WHERE book_id = ?";
        jdbcTemplate.update(sql, bookId);
    }

    private static class BookRowMapper implements RowMapper<Book> {
        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Book book = new Book();
            book.setBookId(rs.getString("book_id"));
            book.setBookAuthor(rs.getString("book_author"));
            book.setBookTitle(rs.getString("book_title"));
            String bookDetailJson = rs.getString("book_detail");
            logger.info("Raw JSON String retrieved from database: {}", bookDetailJson);
            try {
                JsonNode bookDetail = new ObjectMapper().readTree(bookDetailJson);
                book.setBookDetail(bookDetail);
                logger.info("Parsed JSON Node: {}", bookDetail);
            } catch (Exception e) {
                throw new RuntimeException("Error while reading book_detail from JSON", e);
            }
            logger.info("Successfully mapped book with bookDetail: {}", book.getBookDetail());
            return book;
        }
    }
}
