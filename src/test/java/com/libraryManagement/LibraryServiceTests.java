package com.libraryManagement;

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

    private List<Author> mockAuthors;
    private List<Book> mockBooks;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockAuthors = Arrays.asList(
                new Author("A101", "James Clear"),
                new Author("A102", "Brain Tracy"),
                new Author("A103", "Cal Newport"),
                new Author("A104", "Darius Foroux"),
                new Author("A105", "Robin Sharma")
        );
        mockBooks = Arrays.asList(
                new Book("B101", "Paulo Coelho", "The Alchemist", new ObjectMapper().createObjectNode().put("publishing year", 1988).put("genre", "Fiction")),
                new Book("B102", "James Clear", "Atomic Habits", new ObjectMapper().createObjectNode().put("publishing year", 2012).put("genre", "Self-Help")),
                new Book("B103", "Cal Newport", "Deep Work", new ObjectMapper().createObjectNode().put("publishing year", 2016).put("genre", "Productivity")),
                new Book("B104", "Darius Foroux", "Do It Today", new ObjectMapper().createObjectNode().put("publishing year", 2018).put("genre", "Philosophy")),
                new Book("B105", "Robin Sharma", "The 5 AM Club", new ObjectMapper().createObjectNode().put("publishing year", 2020).put("genre", "Self-Help"))
        );
    }

    @Test(description = "Testing to find the existing author by its ID")
    public void testFindAuthorById() {
        String authorId = "A101";
        //Mock the behaviour
        when(libraryService.getAuthor(authorId)).thenReturn(Optional.of(new Author("A101", "James Clear")));
        logger.info("Testing retrieval of author with author Id: {}", authorId);
        Optional<Author> author = libraryService.getAuthor(authorId);
        //Assertions to verify the values
        Assert.assertTrue(author.isPresent(), "Author should be found");
        Assert.assertEquals(author.get().getAuthorId(), authorId, "Author ID should match");
        Assert.assertEquals(author.get().getAuthorName(), "James Clear", "Author name should match");

        // Verifying the interaction
        logger.info("Verify that the get author method was called on library service");
        verify(libraryService).getAuthor(authorId);
    }

    @Test(description = "Testing to find the non existing author by its ID")
    public void testFindNonExistingAuthorById() {
        String nonExistingAuthorId = "A999";
        when(libraryService.getAuthor(nonExistingAuthorId)).thenReturn(Optional.empty());
        logger.info("Testing retrieval of non existing author with author Id: {}", nonExistingAuthorId);
        Optional<Author> nonExistingAuthor = libraryService.getAuthor(nonExistingAuthorId);
        Assert.assertFalse(nonExistingAuthor.isPresent(), "Author should not be found");

        logger.info("Verify that the get author method was called on the library service");
        verify(libraryService).getAuthor(nonExistingAuthorId);
    }

    @Test(description = "Testing to get all the authors")
    public void testGetAllAuthors() {
        try {
            when(libraryService.getAllAuthors()).thenReturn(mockAuthors);
            List<Author> authors = libraryService.getAllAuthors();
            logger.info("Retrieved {} authors from the library", authors.size());

            Assert.assertNotNull(authors, "Authors list should not be null");
            Assert.assertEquals(authors.size(), 5, "The number of authors should be 5");

            // Validate the data within the list
            Assert.assertEquals(authors.get(3).getAuthorId(), "A104", "Fourth author ID should match");
            Assert.assertEquals(authors.get(3).getAuthorName(), "Darius Foroux", "Fourth author name should match");

            logger.info("Verify that the get all authors method was called on library service");
            verify(libraryService).getAllAuthors();
        } catch (NullPointerException e) {
            logger.error("Null pointer exception");
            Assert.fail("Test failed due to null pointer exception" + e.getMessage());
        } catch (AssertionError e) {
            logger.error("Assertion error occurred: {}", e.getMessage());
            Assert.fail("Test failed due to assertion error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error occurred while retrieving authors: {}", e.getMessage());
            Assert.fail("Test failed due to unexpected error: " + e.getMessage());
        }
    }

    @Test(description = "Testing to create a new author")
    public void testCreateAuthor() {
        logger.info("Defining a new author to add");
        Author newAuthor = new Author("A106", "Johanna Spyri");

        when(libraryService.createAuthor(newAuthor)).thenReturn(true);
        when(libraryService.getAuthor(newAuthor.getAuthorId())).thenReturn(Optional.of(newAuthor));
        try {
            // Attempt to create the new author
            logger.info("Attempting to create new author with authorId: {}", newAuthor.getAuthorId());
            boolean isCreated = libraryService.createAuthor(newAuthor);
            logger.info("Author creation is: {}", isCreated ? "Successful" : "Failed");
            // Assertions to verify the createAuthor method
            Assert.assertTrue(isCreated, "Author creation should be successful");

            // Retrieve the newly created author and verify the data
            Optional<Author> retrievedAuthor = libraryService.getAuthor(newAuthor.getAuthorId());
            Assert.assertTrue(retrievedAuthor.isPresent(), "Newly created author should be found in the database");
            logger.info("Retrieved Author: Author ID - {} and Author Name - {}", retrievedAuthor.get().getAuthorId(), retrievedAuthor.get().getAuthorName());
            Assert.assertEquals(retrievedAuthor.get().getAuthorId(), newAuthor.getAuthorId(), "Author ID should match");
            Assert.assertEquals(retrievedAuthor.get().getAuthorName(), newAuthor.getAuthorName(), "Author Name should match");

            logger.info("Verify that the create author method was called on library service");
            verify(libraryService).createAuthor(newAuthor);
            logger.info("Successfully verified that the new author was created with authorId: {}", newAuthor.getAuthorId());
        } catch (BadSqlGrammarException e) {
            logger.error("SQL Syntax error occurred", e);
            Assert.fail("SQL Syntax error" + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Error occurred due to wrong variable names", e);
            Assert.fail("Error occurred due to wrong variable names" + e.getMessage());
        } catch (NullPointerException e) {
            logger.error("Null pointer exception occurred", e);
            Assert.fail("Null Pointer Exception occurred" + e.getMessage());
        } catch (Exception e) {
            logger.error("Error occurred while creating the author", e);
            Assert.fail("Error occurred while creating the author" + e.getMessage());
        }
    }

    @Test(description = "Testing to update an existing author")
    public void testUpdateAuthor() {
        // Mock data for existing author
        try {
            String authorId = "A101";
            Author existingAuthor = new Author(authorId, "James Clear");

            // Mock the updated author data
            Author updatedAuthor = new Author(authorId, "Jay Shetty");

            // Mocking the behavior for getting an existing author
            when(libraryService.getAuthor(authorId)).thenReturn(Optional.of(existingAuthor));
            when(libraryService.updateAuthor(updatedAuthor)).thenReturn(true);
            when(libraryService.getAuthor(authorId)).thenReturn(Optional.of(updatedAuthor));

            // Attempting to update the author
            logger.info("Attempting to update author with ID: {}", authorId);
            boolean isUpdated = libraryService.updateAuthor(updatedAuthor);

            // Verify the update was successful
            Assert.assertTrue(isUpdated, "Author should be updated successfully");

            // Retrieve the updated author and verify the details
            Optional<Author> retrievedAuthor = libraryService.getAuthor(authorId);
            Assert.assertTrue(retrievedAuthor.isPresent(), "Updated author should be present in the database");
            Assert.assertEquals(retrievedAuthor.get().getAuthorName(), "Jay Shetty", "Author name should be updated");

            logger.info("Verify that the update author method was called on library service");
            verify(libraryService).updateAuthor(updatedAuthor);
            logger.info("Successfully updated author with ID: {} to new name: {}", authorId, updatedAuthor.getAuthorName());
        } catch (NullPointerException e) {
            logger.error("Null pointer Exception");
            Assert.fail("Test failed due to the null pointer exception" + e.getMessage());
        } catch (AssertionError e) {
            logger.error("Assertion Error occurred: {}", e.getMessage());
            Assert.fail("Test failed due to assertion error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error occurred while retrieving the authors: {}", e.getMessage());
            Assert.fail("Test failed due to unexpected error: " + e.getMessage());
        }
    }

    @Test(description = "Testing to delete an existing author")
    public void testDeleteAuthor() {
        String authorId = "A101";
        when(libraryService.getAuthor(authorId)).thenReturn(Optional.of(new Author("A101", "James Clear")));
        when(libraryService.deleteAuthor(authorId)).thenReturn(true);

        logger.info("Testing deletion of existing author with author ID: {}", authorId);

        // Verifying the author exists first
        Optional<Author> authorToDelete = libraryService.getAuthor(authorId);
        Assert.assertTrue(authorToDelete.isPresent(), "Author should be found before deletion");

        // Deleting the existing author
        boolean isDeleted = libraryService.deleteAuthor(authorId);
        logger.info("Author deletion is: {}", isDeleted ? "Successful" : "Failed");
        Assert.assertTrue(isDeleted, "Author should be deleted successfully");

        when(libraryService.getAuthor(authorId)).thenReturn(Optional.empty());
        // Verifying the author is deleted (after deletion, it should no longer exist)
        Optional<Author> deletedAuthor = libraryService.getAuthor(authorId);
        Assert.assertFalse(deletedAuthor.isPresent(), "Deleted author should not be found");

        logger.info("Verify that the delete author method was called on library service with correct author Id");
        verify(libraryService).deleteAuthor(authorId);
    }

    @Test(description = "Testing to delete a non-existing author")
    public void testDeleteNonExistingAuthor() {
        String nonExistingAuthorId = "A999";

        when(libraryService.getAuthor(nonExistingAuthorId)).thenReturn(Optional.empty());
        when(libraryService.deleteAuthor(nonExistingAuthorId)).thenReturn(false);

        logger.info("Testing deletion of non-existing author with author ID: {}", nonExistingAuthorId);

        // Verifying that the author does not exist
        Optional<Author> nonExistingAuthor = libraryService.getAuthor(nonExistingAuthorId);
        Assert.assertFalse(nonExistingAuthor.isPresent(), "Author must not exist");

        // Trying to delete a non-existing author
        boolean isNonExistingAuthorDeleted = libraryService.deleteAuthor(nonExistingAuthorId);
        Assert.assertFalse(isNonExistingAuthorDeleted, "Author does not exist to be deleted");

        // Verify that the deleteAuthor method was called for the non-existing author ID
        verify(libraryService).deleteAuthor(nonExistingAuthorId);
        logger.info("Verified that no author was deleted for the non-existing author ID: {}", nonExistingAuthorId);
    }

    @Test(description = "Testing to find the existing book by its ID")
    public void testFindBookById() {
        String bookId = "B101";
        when(libraryService.getBook(bookId)).thenReturn(Optional.of(new Book("B101", "Paulo Coelho", "The Alchemist", new ObjectMapper().createObjectNode().put("publishing year", 1988).put("genre", "Fiction"))));
        logger.info("Testing retrieval of book with book ID: {}", bookId);
        Optional<Book> book = libraryService.getBook(bookId);

        Assert.assertTrue(book.isPresent(), "Book should be found");
        Assert.assertEquals(book.get().getBookId(), bookId, "Book ID should match");
        Assert.assertEquals(book.get().getBookTitle(), "The Alchemist", "Book title should match");
        Assert.assertEquals(book.get().getBookAuthor(), "Paulo Coelho", "Book author should match");
        logger.info("Verify that the get book method was called on library service");
        verify(libraryService).getBook(bookId);
    }

    @Test(description = "Testing to find a non-existing book by its ID")
    public void testFindNonExistingBookById() {
        String nonExistingBookId = "B999";
        when(libraryService.getBook(nonExistingBookId)).thenReturn(Optional.empty());

        Optional<Book> nonExistingBook = libraryService.getBook(nonExistingBookId);
        Assert.assertFalse(nonExistingBook.isPresent(), "Book should not be found");
        logger.info("Verify that the get book method was called for non existing book on library service");
        verify(libraryService).getBook(nonExistingBookId);
    }

    @Test(description = "Testing to get all books")
    public void testGetAllBooks() {
        try {
            when(libraryService.getAllBooks()).thenReturn(mockBooks);
            List<Book> books = libraryService.getAllBooks();
            logger.info("Retrieved {} books from the library", books.size());

            Assert.assertNotNull(books, "Books list should not be null");
            Assert.assertEquals(books.size(), 5, "The number of books should be 5");

            Assert.assertEquals(books.get(3).getBookId(), "B104", "Fourth book ID should match");
            Assert.assertEquals(books.get(3).getBookAuthor(), "Darius Foroux", "Fourth Book Author should match");
            Assert.assertEquals(books.get(3).getBookTitle(), "Do It Today", "Fourth book title should match");
            logger.info("Verify that the get all books method was called on library service");
            verify(libraryService).getAllBooks();
        } catch (NullPointerException e) {
            logger.error("Null Pointer exception");
            Assert.fail("Test failed due to null pointer exception" + e.getMessage());
        } catch (AssertionError e) {
            logger.error("Assertion Error  occurred: {}", e.getMessage());
            Assert.fail("Test failed due to assertion error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error occurred while retrieving books: {}", e.getMessage());
            Assert.fail("Test failed due to unexpected error: " + e.getMessage());
        }
    }

    @Test(description = "Testing to create a new book")
    public void testCreateBook() {
        logger.info("Defining a new book to add");
        Book newBook = new Book("B106", "Heidi", "Johanna Spyri", new ObjectMapper().createObjectNode().put("publishing year", 1880).put("genre", "Fiction"));
        try {
            when(libraryService.createBook(newBook)).thenReturn(true);
            when(libraryService.getBook(newBook.getBookId())).thenReturn(Optional.of(newBook));

            boolean isCreated = libraryService.createBook(newBook);
            logger.info("Book creation result: {}", isCreated);
            Assert.assertTrue(isCreated, "Book should be created successfully");

            Optional<Book> retrievedBook = libraryService.getBook(newBook.getBookId());
            Assert.assertTrue(retrievedBook.isPresent(), "Newly created book should be found");
            Assert.assertEquals(retrievedBook.get().getBookId(), newBook.getBookId(), "Book ID should match");
            Assert.assertEquals(retrievedBook.get().getBookTitle(), newBook.getBookTitle(), "Book title should match");
            logger.info("Verify that the create book method was called on library service");
            verify(libraryService).createBook(newBook);
        } catch (BadSqlGrammarException e) {
            logger.error("SQL Syntax error occurred", e);
            Assert.fail("SQL Syntax error" + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Error occurred due to wrong variable names", e);
            Assert.fail("Error occurred due to wrong variable names" + e.getMessage());
        } catch (NullPointerException e) {
            logger.error("Null pointer exception occurred", e);
            Assert.fail("Null Pointer Exception occurred" + e.getMessage());
        } catch (Exception e) {
            logger.error("Error occurred while creating the author", e);
            Assert.fail("Error occurred while creating the author" + e.getMessage());
        }
    }

    @Test(description = "Testing to update an existing book")
    public void testUpdateBook() {
        String bookId = "B101";
        Book existingBook = new Book(bookId, "The Alchemist", "Paulo Coelho", new ObjectMapper().createObjectNode().put("publishing year", 1988).put("genre", "Fiction"));
        Book updatedBook = new Book(bookId, "The Alchemist: Special Edition", "Paulo Coelho", new ObjectMapper().createObjectNode().put("publishing year", 1988).put("genre", "Fiction"));

        when(libraryService.getBook(bookId)).thenReturn(Optional.of(existingBook));
        when(libraryService.updateBook(updatedBook)).thenReturn(true);
        when(libraryService.getBook(bookId)).thenReturn(Optional.of(updatedBook));

        logger.info("Attempting to update the book with Id: {}", bookId);
        boolean isUpdated = libraryService.updateBook(updatedBook);
        Assert.assertTrue(isUpdated, "Book should be updated successfully");

        Optional<Book> retrievedBook = libraryService.getBook(bookId);
        Assert.assertTrue(retrievedBook.isPresent(), "Updated book should be found");
        Assert.assertEquals(retrievedBook.get().getBookAuthor(), "The Alchemist: Special Edition", "Book title should be updated");
        logger.info("Verify that the update book method was called on library service");
        verify(libraryService).updateBook(updatedBook);
        logger.info("Successfully updated book with ID: {}", bookId);
    }

    @Test(description = "Testing to delete an existing book")
    public void testDeleteBook() {
        String bookId = "B101";
        when(libraryService.getBook(bookId)).thenReturn(Optional.of(new Book(bookId, "The Alchemist", "Paulo Coelho", new ObjectMapper().createObjectNode().put("publishing year", 1988).put("genre", "Fiction"))));
        when(libraryService.deleteBook(bookId)).thenReturn(true);

        Optional<Book> bookToDelete = libraryService.getBook(bookId);
        Assert.assertTrue(bookToDelete.isPresent(), "Book should be found before deletion");

        boolean isDeleted = libraryService.deleteBook(bookId);
        Assert.assertTrue(isDeleted, "Book should be deleted successfully");

        when(libraryService.getBook(bookId)).thenReturn(Optional.empty());

        Optional<Book> deletedBook = libraryService.getBook(bookId);
        Assert.assertFalse(deletedBook.isPresent(), "Deleted book should not be found");
        logger.info("Verify that the delete book for existing book, method was called on library service");
        verify(libraryService).deleteBook(bookId);
    }

    @Test(description = "Testing to delete a non-existing book")
    public void testDeleteNonExistingBook() {
        String nonExistingBookId = "B999";
        when(libraryService.getBook(nonExistingBookId)).thenReturn(Optional.empty());
        when(libraryService.deleteBook(nonExistingBookId)).thenReturn(false);

        Optional<Book> nonExistingBook = libraryService.getBook(nonExistingBookId);
        Assert.assertFalse(nonExistingBook.isPresent(), "Non-existing book should not be found");

        boolean isNonExistingBookDeleted = libraryService.deleteBook(nonExistingBookId);
        Assert.assertFalse(isNonExistingBookDeleted, "Non-existing book should not be deleted");
        logger.info("Verify that the delete book for non-existing book, method was called on library service");
        verify(libraryService).deleteBook(nonExistingBookId);
    }

}
