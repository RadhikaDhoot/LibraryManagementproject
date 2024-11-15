package com.thinkconstructive.rest_demo.repository;

import com.thinkconstructive.rest_demo.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class BookRepository  {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public BookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createBook(Book book) {
        String sql = "INSERT INTO books (book_id, book_author, book_title, book_detail) VALUES (?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql, book.getBookId(), book.getBookAuthor(), book.getBookAuthor(), book.getBookDetail());
            System.out.println("Book created successfully");
        } catch (DataIntegrityViolationException e) {
            System.err.println("Error: Required fields are missing or invalid data input");
            e.printStackTrace();
        } catch (DataAccessException e) {
            System.err.println("Database error occurred while creating the book");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred");
            e.printStackTrace();
        }
    }

    public Optional<Book> getBook(String book_id) {
        String sql = "SELECT * FROM books where book_id = ?";
        List<Book> books = jdbcTemplate.query(sql, new BookRowMapper(), book_id);
        return books.isEmpty() ? Optional.empty() : Optional.of(books.get(0));
    }

    public List<Book> getAllBooks() {
        String sql = "SELECT * FROM books";
        return jdbcTemplate.query(sql, new BookRowMapper());
    }

    public void updateBook(Book book) {
        String sql = "UPDATE books SET book_author = ?, book_title = ?, book_detail = ? WHERE book_id = ?";
        jdbcTemplate.update(sql, book.getBookAuthor(), book.getBookTitle(), book.getBookDetail(), book.getBookId());
    }

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
            book.setBookDetail(rs.getString("book_detail"));
            return book;
        }
    }
}
