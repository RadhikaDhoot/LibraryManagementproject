package com.libraryManagement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryManagement.model.Author;
import com.libraryManagement.model.Book;
import com.libraryManagement.service.LibraryService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.BadSqlGrammarException;
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
        Author newAuthor = new Author("A106", "Johanna Spyri");

        //Mock behaviour
        when(libraryService.createAuthor(newAuthor)).thenReturn(true);
        when(libraryService.getAuthor(newAuthor.getAuthorId())).thenReturn(Optional.of(newAuthor));

        //Calling the method to create author
        try {
            logger.info("Attempting to create new author with authorId: {}", newAuthor.getAuthorId());
            boolean isAdded = libraryService.createAuthor(newAuthor);
            logger.info("Author creation is: {}", isAdded ? "Successful" : "Failed");
            //Assertion to verify the behaviour
            Assert.assertTrue(isAdded, "Author creation should be successful");
            //Retrieving the added author
            Optional<Author> retrieveAuthor = libraryService.getAuthor(newAuthor.getAuthorId());
            Assert.assertTrue(retrieveAuthor.isPresent());
            //Verify the author details
            logger.info("Retrieved Author: Author ID - {}, Author Name - {}", newAuthor.getAuthorId(), newAuthor.getAuthorName());
            Assert.assertEquals(retrieveAuthor.get().getAuthorId(), newAuthor.getAuthorId(), "Author ID should match");
            Assert.assertEquals(retrieveAuthor.get().getAuthorName(), newAuthor.getAuthorName(), "Author Name should match");
            //Verify that the create method was called
            verify(libraryService).createAuthor(newAuthor);
        } catch (BadSqlGrammarException e) {
            logger.error("SQL Syntax error occurred", e);
            Assert.fail("SQL Syntax error" + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Error occurred due to wrong variables", e);
            Assert.fail("Error occurred due to wrong variables" + e.getMessage());
        } catch (Exception e) {
            logger.error("Error occurred while creating the author", e);
            Assert.fail("Error occurred while creating the author" + e.getMessage());
        }
    }

    @Test(description = "Testing to find the author by its ID")
    public void testFindAuthorById () {
        //Mock data
        String authorId = "A101";
        String nonExistingAuthorId = "A000";
        Author mockAuthor = new Author(authorId, "Johanna Spyri");

        when(libraryService.getAuthor(authorId)).thenReturn(Optional.of(mockAuthor));
        logger.info("Testing retrieval of author with author Id: {}", authorId);
        //Call the method to test
        Optional<Author> author = libraryService.getAuthor(authorId);

        //Assertions to verify the values
        if(author.isPresent()) {
            Author retrievedAuthor = author.get();
            Assert.assertEquals(retrievedAuthor.getAuthorId(), authorId, "Author ID should match");
            Assert.assertEquals(retrievedAuthor.getAuthorName(), "Johanna Spyri", "Author name should match");
            logger.info("Author found with Author ID: {}, Author Name: {}", authorId, retrievedAuthor.getAuthorName());
        } else {
            logger.error("Author with Author ID: {} does not exist", nonExistingAuthorId);
            Assert.fail("Author not found");
        }
        logger.info("Testing retrieval of non-existing author with Author ID: {}", nonExistingAuthorId);
        Optional<Author> nonExistingAuthor = libraryService.getAuthor(nonExistingAuthorId);
        Assert.assertFalse(nonExistingAuthor.isPresent());
        logger.info("Verified that author with author Id: {} does not exist", nonExistingAuthor);
        verify(libraryService).getAuthor(authorId);
        verify(libraryService).getAuthor(nonExistingAuthorId);
    }

    @Test (description = "Testing to get all the authors")
    public void testFindAllAuthors() {
        List<Author> mockAuthors = Arrays.asList(
                new Author("A101", "James Clear"),
                new Author("A102", "Brain Tracy"),
                new Author("A103", "Cal Newport"),
                new Author("A104", "Darius Foroux"),
                new Author("A105", "Robin Sharma")
        );

        when(libraryService.getAllAuthors()).thenReturn(mockAuthors);
        List<Author> authors = libraryService.getAllAuthors();
        logger.info("Retrieved {} authors from the library", authors.size());
        // Assertions to verify
        Assert.assertNotNull(authors, "Authors list should not be null");
        Assert.assertEquals(authors.size(), 5, "The number of authors should match the expected value");
        // Verify that the getAll method was called
        verify(libraryService).getAllAuthors();
        logger.info("Verified that getAuthors method was called");
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
            logger.info("Attempting to update the author with author ID: {} and author name: {}", authorId, mockAuthor.getAuthorName());
            boolean isUpdated = libraryService.updateAuthor(updatedAuthor);
            logger.info("Updated author name is: {}", updatedAuthor.getAuthorName());
            Assert.assertTrue(isUpdated, "Author should be updated successfully");

            //Retrieve and Verify the updated author
            Optional<Author> retrievedAuthor = libraryService.getAuthor(authorId);
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
        String existingAuthorId = "A111";
        String nonExistingAuthorId = "A222";
        Author mockAuthor = new Author(existingAuthorId, "J K Rowling");

        //Mocking the behaviour
        when(libraryService.getAuthor(existingAuthorId)).thenReturn(Optional.of(mockAuthor));
        when(libraryService.deleteAuthor(existingAuthorId)).thenReturn(true);
        when(libraryService.getAuthor(nonExistingAuthorId)).thenReturn(Optional.empty());
        when(libraryService.deleteAuthor(nonExistingAuthorId)).thenReturn(false);

        logger.info("Testing deletion of existing author with author ID: {}", existingAuthorId);
        Optional<Author> existingAuthor = libraryService.getAuthor(existingAuthorId);
        Assert.assertTrue(existingAuthor.isPresent(), "Author should be present");
        logger.info("Author with author Id: {} is present in the database", mockAuthor.getAuthorId());
        boolean existingAuthorIsDeleted = libraryService.deleteAuthor(existingAuthorId);
        Assert.assertTrue(existingAuthorIsDeleted, "Existing author should be deleted successfully");

        verify(libraryService).deleteAuthor(existingAuthorId);
        logger.info("Verified that delete author method was called");

        logger.info("Testing deletion of non-existing author with author ID: {}", nonExistingAuthorId);
        Optional<Author> nonExistingAuthor = libraryService.getAuthor(nonExistingAuthorId);
        Assert.assertFalse(nonExistingAuthor.isPresent(), "Author should not be present");
        logger.info("Verified that the author with author Id: {} is not present in the database", nonExistingAuthor);
    }

    @Test(description = "Test creating a new book")
    public void testCreateBook() throws JsonProcessingException {
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

        verify(libraryService).createBook(newBook);
        logger.info("Verified that created book was called with the correct parameters");

        // Mock the behavior for retrieving the created book
        when(libraryService.getBook("B106")).thenReturn(Optional.of(newBook));
        Optional<Book> retrievedBook = libraryService.getBook("B106");
        logger.info("Retrieved Book for ID 'B106'");
        Assert.assertTrue(retrievedBook.isPresent(), "Retrieved book should be present");
    }

    @Test
    public void testFindBookById() throws JsonProcessingException {
        JsonNode bookDetail = new ObjectMapper().createObjectNode()
                .put("publishing year", 2020)
                .put("genre", "Motivational");

        Book mockBook = new Book("B111", "Ankur Warikoo", "Do Epic Shit", bookDetail);
        when(libraryService.getBook(mockBook.getBookId())).thenReturn(Optional.of(mockBook));
        logger.info("Attempting to find book with book Id: {}", mockBook.getBookId());
        Optional<Book> retrievedBook = libraryService.getBook(mockBook.getBookId());
        Assert.assertTrue(retrievedBook.isPresent());
        logger.info("Book found");
        Assert.assertEquals(retrievedBook.get().getBookId(), "B111", "Book Id should match");
        Assert.assertEquals(retrievedBook.get().getBookAuthor(), "Ankur Warikoo", "Author name should match");
        Assert.assertEquals(retrievedBook.get().getBookTitle(), "Do Epic Shit", "Book Title should match");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode retrieveBookDetail = objectMapper.readTree(mockBook.getBookDetail().toString());
        Assert.assertTrue(retrieveBookDetail.equals(bookDetail), "Book detail should match");
        verify(libraryService).getBook(mockBook.getBookId());
        logger.info("Verified that the book with Book Id was retrieved successfully");
    }

    @Test(description = "Test finding all books")
    public void testFindAllBooks() {
        JsonNode bookDetail1 = new ObjectMapper().createObjectNode()
                .put("publishing year", 2015)
                .put("genre", "Fiction");
        Book book1 = new Book("B101", "The Alchemist", "Paulo Coelho", bookDetail1);
        JsonNode bookDetail2 = new ObjectMapper().createObjectNode()
                .put("publishing year", 2020)
                .put("genre", "Non-Fiction");
        Book book2 = new Book("B102", "Atomic Habits", "James Clear", bookDetail2);
        List<Book> books = Arrays.asList(book1, book2);

        // Mocking the behavior of findAllBooks method
        logger.info("Attempting to find all books");
        when(libraryService.getAllBooks()).thenReturn(books);
        // Call the method under test
        List<Book> retrievedBooks = libraryService.getAllBooks();
        // Assertions to validate the behavior
        Assert.assertNotNull(retrievedBooks, "Retrieved books list should not be null");
        Assert.assertEquals(retrievedBooks.size(), 2, "There should be 2 books in the list");

        // Verify that the service's findAllBooks method was called
        verify(libraryService).getAllBooks();
        logger.info("Verified that all books were retrieved successfully");
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
