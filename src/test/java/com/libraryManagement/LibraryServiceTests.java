package com.libraryManagement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryManagement.model.Author;
import com.libraryManagement.model.Book;
import com.libraryManagement.service.LibraryService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class LibraryServiceTests {
    private static final Logger logger = LoggerFactory.getLogger(LibraryServiceTests.class);

    @Mock
    private LibraryService libraryService;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test (description = "Testing to create new author")
    public void testCreateAuthor() {
        //Mock data
        String authorId = "A106";
        String authorName = "Johanna Spyri";
        Author newAuthor = new Author(authorId, authorName);

        //Mock behaviour
        when(libraryService.createAuthor(newAuthor)).thenReturn(true);
        when(libraryService.getAuthor(authorId)).thenReturn(Optional.of(newAuthor));

        //Calling the method to create author
        try {
            logger.info("Attempting to create new author with authorId: {}", newAuthor.getAuthorId());
            boolean isAdded = libraryService.createAuthor(newAuthor);
            logger.info("Author creation is: {}", isAdded ? "Successful" : "Failed");

            //Assertion to verify the behaviour
            Assert.assertTrue(isAdded, "Author creation should be successful");

            //Retrieving the added author
            Optional<Author> retrieveAuthor = libraryService.getAuthor(authorId);
            Assert.assertTrue(retrieveAuthor.isPresent());

            //Verify the author details
            logger.info("Retrieved Author: Author ID - {}, Author Name - {}", newAuthor.getAuthorId(), newAuthor.getAuthorName());
            Assert.assertEquals(newAuthor.getAuthorId(), authorId, "Author ID should match");
            Assert.assertEquals(newAuthor.getAuthorName(), authorName, "Author Name should match");

            //Verify that the create and get method was called
            verify(libraryService).createAuthor(newAuthor);
        } catch (Exception e) {
            logger.error("Error occurred during author creation", e);
            Assert.fail("Error occurred during author creation" + e.getMessage());
        }
    }

    @Test(description = "Testing to find the author by its ID")
    public void testFindAuthorById () {
        //Mock data
        String authorId = "A101";
        String nonExistingAuthorId = "A000";
        Author mockAuthor = new Author(authorId, "Johanna Spyri");

        //Mock behaviour
        when(libraryService.getAuthor(authorId)).thenReturn(Optional.of(mockAuthor));

        //Call the method to test
        Optional<Author> author = libraryService.getAuthor(authorId);

        //Assertions to verify the behaviour
        if(author.isPresent()) {
            Author retrievedAuthor = author.get();
            Assert.assertEquals(retrievedAuthor.getAuthorId(), authorId, "Author ID should match");
            Assert.assertEquals(retrievedAuthor.getAuthorName(), "Johanna Spyri", "Author name should match");
            logger.info("Author found with with Author ID: {}, Author Name: {}", authorId, retrievedAuthor.getAuthorName());
        } else {
            logger.error("Author with Author ID: {} does not exist", nonExistingAuthorId);
            Assert.fail("Author not found");
        }

        //Verify that the get method was called
        verify(libraryService).getAuthor(authorId);
    }

    @Test (description = "Testing to get all the authors")
    public void testFindAllAuthors() {
        // Mock data
        List<Author> mockAuthors = Arrays.asList(
                new Author("A101", "James Clear"),
                new Author("A102", "Brain Tracy"),
                new Author("A103", "Cal Newport"),
                new Author("A104", "Darius Foroux"),
                new Author("A105", "Robin Sharma")
        );

        // Mock behavior
        when(libraryService.getAllAuthors()).thenReturn(mockAuthors);

        // Call the method to test
        List<Author> authors = libraryService.getAllAuthors();

        // Log the result
        logger.info("Retrieved {} authors from the service", authors.size());

        // Assertions to verify the behavior
        Assert.assertNotNull(authors, "Authors list should not be null");
        Assert.assertEquals(authors.size(), 5, "The number of authors should match the expected value");

        // Verify that the getAll method was called
        verify(libraryService).getAllAuthors();
    }

    @Test(description = "Testing to update an existing author")
    public void testUpdateAuthor() {
        String authorId = "A101";
        Author mockAuthor = new Author(authorId, "James Clear");
        Author updatedAuthor = new Author(authorId, "Jay Shetty");

        //Mocking the behaviour
        when(libraryService.getAuthor(authorId)).thenReturn(Optional.of(mockAuthor));
        when(libraryService.updateAuthor(updatedAuthor)).thenReturn(true);
        try {
            logger.info("Attempting to update the author with author ID: {}", authorId);
            boolean isUpdated = libraryService.updateAuthor(updatedAuthor);
            logger.info("Author name  is: {}", updatedAuthor.getAuthorName());
            Assert.assertTrue(isUpdated, "Author should be updated successfully");

            //Retrieve and Verify the updated author
            Optional<Author> retrievedAuthor = libraryService.getAuthor(authorId);
            //Assertions to verify the behaviour
            Assert.assertTrue(retrievedAuthor.isPresent(), "Updated author should be present in the database");
            Assert.assertEquals(updatedAuthor.getAuthorName(), "Jay Shetty", "Author name should be updated");
            logger.info("Successfully updated the author with author ID: {}", authorId);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument provided during update", e);
            Assert.fail("Invalid argument provided");
        }
        //Verify that update method was called
        verify(libraryService).updateAuthor(updatedAuthor);
    }

    @Test (description = "Testing to delete an existing and non existing author")
    public void testDeleteAuthor() {

        //Setting up test data
        String existingAuthorId = "A111";
        String nonExistingAuthorId = "A222";
        Author mockAuthor = new Author(existingAuthorId, "J K Rowling");

        //Mocking the behaviour for existing author
        when(libraryService.getAuthor(existingAuthorId)).thenReturn(Optional.of(mockAuthor));
        when(libraryService.deleteAuthor(existingAuthorId)).thenReturn(true);

        //Mocking the behaviour for non-existing author
        when(libraryService.getAuthor(nonExistingAuthorId)).thenReturn(Optional.empty());
        when(libraryService.deleteAuthor(nonExistingAuthorId)).thenReturn(false);

        //Testing logic for existing author to check the behaviour
        Optional<Author> existingAuthor = libraryService.getAuthor(existingAuthorId);
        Assert.assertTrue(existingAuthor.isPresent(), "Author should be present");
        boolean existingAuthorIsDeleted = libraryService.deleteAuthor(existingAuthorId);
        Assert.assertTrue(existingAuthorIsDeleted, "Existing author should be deleted successfully");

        //Verifying while the existing author has been deleted
        verify(libraryService).deleteAuthor(existingAuthorId);

        //Testing logic for non-existing author
        Optional<Author> nonExistingAuthor = libraryService.getAuthor(nonExistingAuthorId);
        Assert.assertFalse(nonExistingAuthor.isPresent(), "Author should not be present");
        boolean nonExistingAuthorIsDeleted = libraryService.deleteAuthor(nonExistingAuthorId);
        Assert.assertFalse(nonExistingAuthorIsDeleted, "Non-existing author should not be deleted");

        //Verifying the behaviour for non-existing author
        verify(libraryService).deleteAuthor(nonExistingAuthorId);
    }

    @Test(description = "Test creating a new book")
    public void testCreateBook() throws Exception {
        JsonNode bookDetail = new ObjectMapper().createObjectNode()
                .put("publishing year", 2015)
                .put("genre", "Fiction");

        Book newBook = new Book("B106", "Heidi", "Johanna Spyri", bookDetail);
        logger.info("Attempting to create a new Book: {}", newBook);

        // Mocking the behavior of createBook method to simulate adding the book successfully
        when(libraryService.createBook(newBook)).thenReturn(true);
        boolean isAdded = libraryService.createBook(newBook);
        logger.info("Book creation result: {}", isAdded);
        Assert.assertTrue(isAdded, "Book should be created successfully");

        // Verify that the createBook method was called with the correct parameters
        verify(libraryService).createBook(newBook);
        logger.info("Verified that created book was called with the correct book details");

        // Mock the behavior for retrieving the created book
        when(libraryService.getBook("B106")).thenReturn(Optional.of(newBook));
        Optional<Book> retrievedBook = libraryService.getBook("B106");
        logger.info("Retrieved Book for ID 'B106': {}", retrievedBook);
        Assert.assertTrue(retrievedBook.isPresent(), "Retrieved book should be present");
        Book addedBook = retrievedBook.get();

        // Assert that the book's details match the expected values
        Assert.assertEquals(addedBook.getBookId(), "B106", "Book ID should match");
        Assert.assertEquals(addedBook.getBookAuthor(), "Heidi", "Author name should match");
        Assert.assertEquals(addedBook.getBookTitle(), "Johanna Spyri", "Book title should match");

        // Verify that the book details (JSON) match
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode retrievedBookDetail = objectMapper.readTree(addedBook.getBookDetail().asText());
//        Assert.assertEquals(retrievedBookDetail, bookDetail.toString(), "Book details should match");
    }

    @Test(description = "Test deleting an existing book from the database")
    public void testDeleteBook() {
        String bookId = "B101";
        JsonNode bookDetail = new ObjectMapper().createObjectNode()
                .put("publishing year", 2015)
                .put("genre", "Fiction");

        Book mockBook = new Book(bookId, "The Alchemist", "Paulo Coelho", bookDetail);

        //Mocking the behaviour
        when(libraryService.getBook(bookId)).thenReturn(Optional.of(mockBook));
        when(libraryService.deleteBook(bookId)).thenReturn(true);

        Optional<Book> book = libraryService.getBook(bookId);
        if(book.isPresent()) {
            logger.info("Book found with book Id: {}. Proceeding to delete...", bookId);

            //Perform deletion of existing book in the database
            boolean isDeleted = libraryService.deleteBook(bookId);
            Assert.assertTrue(isDeleted, "Book should be deleted successfully");
            logger.info("Book deleted successfully");
            when(libraryService.getBook(bookId)).thenReturn(Optional.empty());

            //Verify that the book no longer exists in the database
            Optional<Book> deletedBook = libraryService.getBook(bookId);
            Assert.assertFalse(deletedBook.isPresent(), "Deleted book should not be present in the database");

            logger.info("Verified that the book with book Id: {} has been deleted successfully", bookId);
        } else {
            logger.info("Book with book Id: {} not found. Cannot proceed further with delete operation", bookId);
            Assert.fail("Book not found, deletion cannot proceed");
        }

        //Verify that the delete book executed
        verify(libraryService).deleteBook(bookId);
    }

    @Test
    void contextLoads() {
        Assert.assertNotNull(libraryService, "Library Service should not be null");
    }

}
