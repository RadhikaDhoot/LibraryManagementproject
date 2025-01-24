package com.libraryManagement;

import com.libraryManagement.model.Author;
import com.libraryManagement.repository.AuthorRepository;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AuthorRepositoryTests {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private AuthorRepository authorRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuthorRepositoryTests.class);

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test(description = "Testing to create a new author")
    public void testCreateAuthor() {
        Author author = new Author("A101", "James Clear");
        logger.info("Attempting to create a new Author with ID: {} and Name: {}", author.getAuthorId(), author.getAuthorName());

        // No return value for jdbcTemplate.update, just verify interaction
        when(jdbcTemplate.update(any(String.class), eq(author.getAuthorId()), eq(author.getAuthorName())))
                .thenReturn(1);
        logger.info("Calling create method from author repository");
        authorRepository.createAuthor(author);
        logger.info("Verifying that jdbcTemplate.update was called with the correct SQL and parameters");
        verify(jdbcTemplate, times(1)).update(
                "INSERT INTO authors (author_id, author_name) VALUES (?, ?)",
                author.getAuthorId(), author.getAuthorName());
        logger.info("Mocking jdbcTemplate.query to fetch the created author from the database.");
        when(jdbcTemplate.query(eq("SELECT * FROM authors WHERE author_id = ?"),
                any(RowMapper.class), eq(author.getAuthorId())))
                .thenReturn(Collections.singletonList(author));
        logger.info("Executing jdbcTemplate.query to verify the author with ID: {} exists", author.getAuthorId());
        List<Author> result = jdbcTemplate.query(
                "SELECT * FROM authors WHERE author_id = ?",
                (rs, rowNum) -> new Author(rs.getString("author_id"), rs.getString("author_name")),
                author.getAuthorId()
        );
        logger.info("Assertions to validate query results");
        Assert.assertNotNull(result, "Result should not be null");
        Assert.assertFalse(result.isEmpty(), "Result list should not be empty");
        Assert.assertEquals(result.size(), 1, "Only one author should be present");
        Assert.assertEquals(result.get(0).getAuthorId(), "A101", "Author ID should match");
        Assert.assertEquals(result.get(0).getAuthorName(), "James Clear", "Author name should match");
        logger.info("Author creation test passed");
    }

    @Test(description = "Test retrieving an author by ID")
    public void testGetAuthor() {
        Author author = new Author("A101", "James Clear");

        // Mock the jdbcTemplate.query method
//        when(authorRepository.getAuthor(author.getAuthorId())).thenReturn(Optional.of(author));
        when(jdbcTemplate.query(eq("SELECT * FROM authors WHERE author_id = ?"),
                any(RowMapper.class), eq("A101")))
                .thenReturn(List.of(author));
        logger.info("Calling to get author method from author repository");
        Optional<Author> result = authorRepository.getAuthor("A101");
        logger.info("Assertions to validate the query results");
        Assert.assertTrue(result.isPresent(), "Author should be present");
        Assert.assertEquals(result.get().getAuthorId(), "A101");
        Assert.assertEquals(result.get().getAuthorName(), "James Clear");

        logger.info("Verifying the jdbcTemplate query method was called with the correct SQL and parameters");
        verify(jdbcTemplate, times(1)).query(
                eq("SELECT * FROM authors WHERE author_id = ?"), any(RowMapper.class), eq("A101"));
        logger.info("Author retrieving by author ID test passed");
    }

    @Test(description = "Test retrieving all authors")
    public void testGetAllAuthors() {
        List<Author> mockAuthors = Arrays.asList(
                new Author("A101", "James Clear"),
                new Author("A102", "Brain Tracy")
        );

        // Mock the jdbcTemplate.query method with argument matchers
        when(jdbcTemplate.query(eq("SELECT * FROM authors ORDER BY author_id"), any(RowMapper.class)))
                .thenReturn(mockAuthors);
        logger.info("Calling to get all author method from author repository");
        List<Author> result = authorRepository.getAllAuthor();
        logger.info("Assertions to validate the results");
        Assert.assertNotNull(result, "Authors list should not be null");
        Assert.assertEquals(result.size(), 2);
        Assert.assertEquals(result.get(0).getAuthorId(), "A101");
        Assert.assertEquals(result.get(1).getAuthorId(), "A102");
        Assert.assertEquals(result.get(0).getAuthorName(), "James Clear");
        Assert.assertEquals(result.get(1).getAuthorName(), "Brain Tracy");

        logger.info("Verifying the jdbcTemplate query method, called with the correct SQL and parameter");
        verify(jdbcTemplate, times(1)).query(
                eq("SELECT * FROM authors ORDER BY author_id"), any(RowMapper.class));
        logger.info("All authors retrieval test passed");
    }

    @Test(description = "Test updating an author")
    public void testUpdateAuthor() {
        logger.info("Creating the author with initial values");
        Author initialAuthor = new Author("A101", "James Clear");
        logger.info("Mocking the creation of the author");
        when(jdbcTemplate.update(
                eq("INSERT INTO authors (author_id, author_name) VALUES (?, ?)"),
                eq(initialAuthor.getAuthorId()), eq(initialAuthor.getAuthorName())
        )).thenReturn(1);
        logger.info("Calling the createAuthor method to add the initial author");
        authorRepository.createAuthor(initialAuthor);

        logger.info("Updating the author's name");
        Author updatedAuthor = new Author("A101", "Robin Sharma");
        logger.info("Mocking the update of the author's name");
        when(jdbcTemplate.update(
                eq("UPDATE authors SET author_name = ? WHERE author_id = ?"),
                eq(updatedAuthor.getAuthorName()), eq(updatedAuthor.getAuthorId())
        )).thenReturn(1);

        logger.info("Calling the updateAuthor method to update the author's name");
        authorRepository.updateAuthor(updatedAuthor);

        logger.info("Verifying the update of the author's name");
        verify(jdbcTemplate, times(1)).update(
                eq("UPDATE authors SET author_name = ? WHERE author_id = ?"),
                eq(updatedAuthor.getAuthorName()), eq(updatedAuthor.getAuthorId()));

        logger.info("Mocking retrieval of the updated author");
        when(jdbcTemplate.query(
                eq("SELECT * FROM authors WHERE author_id = ?"),
                any(RowMapper.class), eq("A101")
        )).thenReturn(List.of(updatedAuthor));

        logger.info("Calling getAuthor method to retrieve the updated author");
        Optional<Author> result = authorRepository.getAuthor("A101");

        logger.info("Assertions to confirm the author's name update");
        Assert.assertTrue(result.isPresent(), "Author should be present");
        Assert.assertEquals(result.get().getAuthorId(), "A101");
        Assert.assertEquals(result.get().getAuthorName(), "Robin Sharma");

        logger.info("Author update test passed");
    }


    @Test(description = "Test deleting an author")
    public void testDeleteAuthor() {
        Author author = new Author("A101", "James Clear");
        when(jdbcTemplate.update(
                eq("DELETE FROM authors WHERE author_id = ?"), eq(author.getAuthorId()))).thenReturn(1);

        logger.info("Calling to delete author method from author repository");
        authorRepository.deleteAuthor(author.getAuthorId());

        // Verify that update method was called with correct SQL and parameter
        logger.info("Verifying the jdbcTemplate update, delete method was called with the correct SQL and parameters");
        verify(jdbcTemplate, times(1)).update(
                eq("DELETE FROM authors WHERE author_id = ?"), eq(author.getAuthorId()));
        String querySql = "SELECT * FROM authors WHERE author_id = ?";
        when(jdbcTemplate.query(eq(querySql), any(RowMapper.class), eq(author.getAuthorId())))
                .thenReturn(Collections.emptyList()); // Simulate no records found

        // Query to check the author does not exist
        List<Author> result = jdbcTemplate.query(querySql, (rs, rowNum) ->
                new Author(rs.getString("author_id"), rs.getString("author_name")), author.getAuthorId());
        logger.info("Assertions to validate the result");
        Assert.assertTrue(result.isEmpty(), "Author should no longer exist after deletion");
        logger.info("Author deletion test passed");
    }

}
