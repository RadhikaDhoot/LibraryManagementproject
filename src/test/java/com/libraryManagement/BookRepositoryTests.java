package com.libraryManagement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryManagement.model.Author;
import com.libraryManagement.model.Book;
import com.libraryManagement.repository.BookRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class BookRepositoryTests {
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private BookRepository bookRepository;

    private static final Logger logger = LoggerFactory.getLogger(BookRepositoryTests.class);

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test (description = "Test to create book")
    public void testCreateBook() {
        JsonNode bookDetail = new ObjectMapper().createObjectNode()
                .put("publishing year", 2001)
                .put("genre", "Self Help");
        String bookDetailJson = bookDetail.toString();

        Book newBook = new Book("B101", "Brain Tracy", "Eat That Frog", bookDetail);
        logger.info("Attempting to create a new Book");
        when(jdbcTemplate.update(any(String.class), eq(newBook.getBookId()), eq(newBook.getBookAuthor()), eq(newBook.getBookTitle())))
                .thenReturn(1);
        logger.info("Calling create method from Book Repository");
        bookRepository.createBook(newBook);
        logger.info("Verifying that jdbcTemplate.update was called with the correct SQL and parameters");
        verify(jdbcTemplate, times(1)).update(
                "INSERT INTO books(book_id, book_author, book_title, book_detail) VALUES (?, ?, ?, ?::jsonb)",
                newBook.getBookId(), newBook.getBookAuthor(), newBook.getBookTitle(), bookDetailJson);
        logger.info("Mocking jdbcTemplate.query to fetch the created book from the database");
        when(jdbcTemplate.query(eq("SELECT * FROM books WHERE book_id = ?"),
                any(RowMapper.class), eq(newBook.getBookId())))
                .thenReturn(Collections.singletonList(newBook));
        logger.info("Execution jdbcTemplate.query to verify the book with ID: {} exists", newBook.getBookId());
        List<Book> result = jdbcTemplate.query(
                "SELECT * FROM books WHERE book_id = ?",
                (rs, rowNum) -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode detail = null;
                    try {
                        detail = objectMapper.readTree(rs.getString("book_detail"));
                    } catch (Exception e) {
                        logger.error("Error parsing book_detail JSON", e);
                    }
                    return new Book(rs.getString("book_id"), rs.getString("book_author"), rs.getString("book_title"), detail);
                },
                newBook.getBookId()
        );
        logger.info("Assertions to validate query results");
        Assert.assertNotNull(result, "Result should not be null");
        Assert.assertFalse(result.isEmpty(), "Result list should not be empty");
        Assert.assertEquals(result.size(), 1, "Only one book should be present");
        Assert.assertEquals(result.get(0).getBookId(), "B101", "Author ID should match");
        Assert.assertEquals(result.get(0).getBookAuthor(), "Brain Tracy", "Book name should match");
        logger.info("Book creation test passed");
    }

    @Test(description = "Testing the update query")
    public void testUpdateBook() {
        logger.info("Creating a original book with initial values");
        JsonNode bookDetail = new ObjectMapper().createObjectNode()
                .put("publishing year", 1988)
                .put("genre", "Novel");
        Book originalBook = new Book("B101", "Paulo Coelho", "The Alchemist", bookDetail);

        logger.info("Defining the updated book details");
        JsonNode updatedDetail = new ObjectMapper().createObjectNode()
                .put("publishing year", 2016)
                .put("genre", "Self Help");
        Book updatedBook = new Book("B101", "Cal Newport", "Deep Work", updatedDetail);

        logger.info("Mocking the initial book to ensure its existence in the database");
        when(jdbcTemplate.queryForObject(eq("SELECT COUNT(*) FROM books WHERE book_id = ?"),
                eq(Integer.class), eq(updatedBook.getBookId())))
                .thenReturn(1);

        logger.info("Verified that the book with ID {} exists in the database", updatedBook.getBookId());

        logger.info("Mocking the update query");
        when(jdbcTemplate.update(
                eq("UPDATE books SET book_author = ?, book_title = ?, book_detail = ?::jsonb WHERE book_id = ?"),
                eq(updatedBook.getBookAuthor()), eq(updatedBook.getBookTitle()), eq(updatedBook.getBookDetail()), eq(updatedBook.getBookId())))
                .thenReturn(1);

        logger.info("Updated the book details for book ID {}", updatedBook.getBookId());

        logger.info("Mocking the get query to verify the updated book details");
        when(jdbcTemplate.query(eq("SELECT * FROM books WHERE book_id = ?"),
                any(RowMapper.class), eq(updatedBook.getBookId())))
                .thenReturn(Collections.singletonList(updatedBook));

        logger.info("Attempting to update the book with ID {}", updatedBook.getBookId());
        bookRepository.updateBook(updatedBook);

        verify(jdbcTemplate, times(1)).update(
                "UPDATE books SET book_author = ?, book_title = ?, book_detail = ?::jsonb WHERE book_id = ?",
                updatedBook.getBookAuthor(), updatedBook.getBookTitle(), updatedBook.getBookDetail(), updatedBook.getBookId());
        logger.info("Verified that update query was executed with correct parameters");

        List<Book> result = jdbcTemplate.query(
                "SELECT * FROM books WHERE book_id = ?",
                (rs, rowNum) -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode detail = null;
                    try {
                        detail = objectMapper.readTree(rs.getString("book_detail"));
                    } catch (Exception e) {
                        logger.error("Error parsing book_detail JSON", e);
                    }
                    return new Book(rs.getString("book_id"), rs.getString("book_author"), rs.getString("book_title"), detail);
                },
                updatedBook.getBookId()
        );

        logger.info("Verified the updated book details have been updated successfully");

        logger.info("Assertions to validate the update");
        Assert.assertNotNull(result, "Result should not be null");
        Assert.assertFalse(result.isEmpty(), "Result list should not be empty");
        Assert.assertEquals(result.size(), 1, "Only one book should be present");
        Assert.assertEquals(result.get(0).getBookId(), updatedBook.getBookId(), "Book ID should match");
        Assert.assertEquals(result.get(0).getBookAuthor(), updatedBook.getBookAuthor(), "Book Author should match");
        Assert.assertEquals(result.get(0).getBookTitle(), updatedBook.getBookTitle(), "Book Title should match");
        Assert.assertEquals(result.get(0).getBookDetail(), updatedBook.getBookDetail(), "Book Details should match");
    }

    @Test(description = "Testing deleting a book")
    public void testDeleteBook() {
        logger.info("Creating a book to delete");
        JsonNode bookDetail = new ObjectMapper().createObjectNode()
                .put("publishing year", 2001)
                .put("genre", "Self Help");
        Book bookToDelete = new Book("B000", "Brain Tracy", "Eat That Frog", bookDetail);

        logger.info("Mocking the delete book query");
        when(jdbcTemplate.update(
                eq("DELETE FROM books WHERE book_id = ?"), eq(bookToDelete.getBookId())))
                .thenReturn(1);

        logger.info("Calling to delete book method from book repository");
        bookRepository.deleteBook(bookToDelete.getBookId());

        logger.info("Verifying the JdbcTemplate update, delete method was called with the correct SQL and parameters");
        verify(jdbcTemplate, times(1)).update(
                eq("DELETE FROM books WHERE book_id = ?"), eq(bookToDelete.getBookId()));

        String querySql = "SELECT * FROM books WHERE book_id = ?";
        when(jdbcTemplate.query(eq(querySql), any(RowMapper.class), eq(bookToDelete.getBookId())))
                .thenReturn(Collections.emptyList());

        // Query to check book does not exist
        List<Book> result = jdbcTemplate.query(querySql, (rs, rowNum) -> {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode detail = null;
            try {
                detail = objectMapper.readTree(rs.getString("book_detail"));
            } catch (Exception e) {
                logger.error("Error parsing book_detail JSON", e);
            }
            return new Book(
                    rs.getString("book_id"), rs.getString("book_author"), rs.getString("book_title"), detail);
        }, bookToDelete.getBookId());

        logger.info("Assertions to validate the result");
        Assert.assertTrue(result.isEmpty(), "BookToDelete should no longer exist after deletion");
        logger.info("Book deletion test passed");
    }
}
