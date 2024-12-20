package com.libraryManagement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryManagement.controller.LibraryController;
import com.libraryManagement.model.Author;
import com.libraryManagement.model.Book;
import com.libraryManagement.repository.AuthorRepository;
import com.libraryManagement.service.LibraryService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
class LibraryServiceTests {
    private static final Logger logger = LoggerFactory.getLogger(LibraryServiceTests.class);

    @Autowired
    private LibraryController libraryController;

    @Autowired
//    @Mock
    private LibraryService libraryService;

    /* @InjectMocks
    private AuthorRepository authorRepository;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    } */

    @Test
    public void testCreateAuthor() {
        Author newAuthor = new Author("A106", "Johanna Spyri");
        logger.info("Attempting to create a new author with authorId: {}", newAuthor.getAuthorId());

        //Adding the author
        boolean isAdded = libraryService.createAuthor(newAuthor);
        logger.info("Author creation is {} ", isAdded ? "Successful" : "Failed");

        //Verifying if the author has been added successfully
        Optional<Author> retrieveAuthor = libraryService.getAuthor("A106");
        if(retrieveAuthor.isPresent()) {
            Author addedAuthor = retrieveAuthor.get();
            logger.info("Retrieved Author is {} ", addedAuthor.getAuthorId());
            assertThat(addedAuthor.getAuthorId()).isEqualTo("A106");
            assertThat(addedAuthor.getAuthorName()).isEqualTo("Johanna Spyri");
        } else {
            logger.error("Added author is not found in the database");
        }
    }

    @Test
    public void testCreateExistingAuthor() {
        Author newAuthor = new Author("A101", "Johanna Spyri");
        logger.info("Attempting to create new author with authorId: {}", newAuthor.getAuthorId());

        //Adding the author
        boolean isAdded = libraryService.createAuthor(newAuthor);
        logger.info("Author already exists so author creation is {} ", isAdded ? "Successful" : "Failed");
    }

    @Test
    public void testFindAllAuthors() {
        //Test the total number of authors from data.sql
        assertThat(libraryService.getAllAuthors()).hasSize(5);
    }

    @Test
    public void testFindAuthorById() {
        String authorId = "A101";

        //Checking if the author exists in the database with the given authorId
        Optional<Author> author = libraryService.getAuthor(authorId);
        if(author.isPresent()) {
            Author retrievedAuthor = author.get();
            assertThat(retrievedAuthor.getAuthorId()).isEqualTo(authorId);
            assertThat(retrievedAuthor.getAuthorName()).isEqualTo("James Clear");
            logger.info("Author found with authorId: {}", authorId);
        } else {
            logger.info("Author with authorId: {}, does not exist", authorId);
        }
    }

    @Test
    public void testFindAuthorByNonExistingId() {
        String authorId = "A000";

        //Checking if the author exists in the database with the given authorId
        Optional<Author> author = libraryService.getAuthor(authorId);
        if(author.isPresent()) {
            Author retrievedAuthor = author.get();
            assertThat(retrievedAuthor.getAuthorId()).isEqualTo(authorId);
            assertThat(retrievedAuthor.getAuthorName()).isEqualTo("James Clear");
            logger.info("Author found with authorId: {}", authorId);
        } else {
            logger.info("Author with authorId: {}, does not exist", authorId);
        }
    }

    @Test
    public void testDeleteAuthor() {
        String authorId = "A103";

        //Verify if the author exists in the records
        boolean isDeleted = libraryService.deleteAuthor(authorId);
        assertThat(isDeleted).isTrue();

        //Verifying the author no longer exists
        Optional<Author> deletedAuthor = libraryService.getAuthor(authorId);
        assertThat(deletedAuthor).isNotPresent();
        System.out.println("Author deleted successfully");
    }

    //Tried using Mockito for testing
    /* @Test
    public void testDeleteAUTHOR() {
        String authorId = "A111";
        Author mockAuthor = new Author(authorId, "J K Rowling");

        //Mocking the behaviour
        when(libraryService.getAuthor(authorId)).thenReturn(Optional.of(mockAuthor));
        when(libraryService.deleteAuthor(authorId)).thenReturn(true);

        //Testing logic
        Optional<Author> author = libraryService.getAuthor(authorId);
        assertThat(author).isPresent();
        boolean isDeleted = libraryService.deleteAuthor(authorId);
        assertThat(isDeleted).isTrue();

        //Verifying while the author has been deleted
        verify(libraryService).deleteAuthor(authorId);
    } */

    @Test
    public void testDeleteNonExistingAuthor() {
        String authorId = "A223";

        //Deleting the Author
        boolean isDeleted = libraryService.deleteAuthor(authorId);
        assertThat(isDeleted).isFalse();
    }

    @Test
    public void testUpdateAuthor() {
        String authorId = "A101";
        try {
            //Updating the author's detail
            Author updatedAuthor = new Author(authorId, "Jay Shetty");
            boolean isUpdated = libraryService.updateAuthor(updatedAuthor);
            assertThat(isUpdated).isTrue();

            //Retrieving the updated author and verifying the changes
            Optional<Author> retrievedAuthor = libraryService.getAuthor(authorId);
            assertThat(retrievedAuthor).isPresent();
            assertThat(retrievedAuthor.get().getAuthorName()).isEqualTo("Jay Shetty");
        } catch (IllegalArgumentException e) {
            fail("Invalid argument provided");
        }
    }

    @Test
    public void testUpdateNonExistingAuthor() {
        String authorId = "A512";
        //Updating the author's detail
        Author updatedAuthor = new Author(authorId, "Jay Shetty");
        boolean isUpdated = libraryService.updateAuthor(updatedAuthor);
        assertThat(isUpdated).isFalse();
    }

    @Test
    public void testCreateBook() throws JsonProcessingException {
        JsonNode bookDetail = new ObjectMapper().createObjectNode()
                .put("publishing year", 2015)
                .put("genre", "Fiction");
        Book newBook= new Book("B106", "Heidi", "Johanna Spyri", bookDetail);
        logger.info("Attempting to create a new book with bookId: {}", newBook.getBookId());

        //Adding the book
        boolean isAdded = libraryService.createBook(newBook);
        logger.info("Book creation is {}", isAdded ? "Successful" : "Failed");

        //Verifying if the book was added successfully
        Optional<Book> retrievedBook = libraryService.getBook("B106");
        if(retrievedBook.isPresent()) {
            Book addedBook = retrievedBook.get();
            logger.info("Retrieved book: {}", addedBook);

            assertThat(addedBook.getBookId()).isEqualTo("B106");
            assertThat(addedBook.getBookAuthor()).isEqualTo("Heidi");
            assertThat(addedBook.getBookTitle()).isEqualTo("Johanna Spyri");

//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode retrievedBookDetail = objectMapper.readTree(addedBook.getBookDetail().toString());
//            assertThat(retrievedBookDetail).isEqualTo(bookDetail);
        } else {
            logger.error("Added book is not found in the database");
        }
    }

    @Test
    public void testFindAllBooks() {
        //Testing the total number of books from data.sql
        assertThat(libraryService.getAllBooks()).hasSize(5);
    }

    @Test
    public void testFindBookById() throws JsonProcessingException {
        String bookId = "B105";

        //Verifying if the book record exists with the given bookId
        Optional<Book> book = libraryService.getBook(bookId);

        if(book.isPresent()) {
            Book retrievedBook = book.get();
            logger.info("Book found with ID: {}. Validating the book details", bookId);
            assertThat(retrievedBook.getBookId()).isEqualTo(bookId);
            assertThat(retrievedBook.getBookAuthor()).isEqualTo("Darius Foroux");
            assertThat(retrievedBook.getBookTitle()).isEqualTo("Do It Today");
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode expectedDetails = objectMapper.readTree("{\"publishing year\": \"2018\", \"genre\": \"Motivational\"}");
//            assertThat(retrievedBook.getBookDetail()).isEqualTo(expectedDetails);
            logger.info("Book details successfully validated: {}, retrievedBook", bookId);
        } else {
            logger.warn("Book with Id: {} does not exist", bookId);
        }
    }

    @Test
    public void testFindBookByNonExistingId() {
        String bookId = "B000";
        Optional<Book> book = libraryService.getBook(bookId);
//
        if(book.isPresent()) {
            Book retrievedBook = book.get();
            assertThat(retrievedBook.getBookId()).isEqualTo(bookId);
            assertThat(retrievedBook.getBookAuthor()).isEqualTo("Darius Foroux");
            assertThat(retrievedBook.getBookTitle()).isEqualTo("Do It Today");

            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode expectedDetails = objectMapper.createObjectNode()
                    .put("publishing year", "2018")
                    .put("genre", "Motivational");
            assertThat(retrievedBook.getBookDetail()).isEqualTo(expectedDetails);
            System.out.println("Book found");
        } else {
            logger.info("Book with book ID: {} does not exist", bookId);
        }
    }

    @Test
    public void testDeleteBook() {
        String bookId = "B103";
        Optional<Book> book = libraryService.getBook(bookId);

        if(book.isPresent()) {
            //Verify if the book exists in the database
            logger.info("Book found with ID: {}. Proceeding to delete", bookId);
            boolean isDeleted = libraryService.deleteBook(bookId);
            assertThat(isDeleted).isTrue();
            //Verify the book no longer exists in the database
            logger.info("Delete operation for Book with ID: {} returned: {}", bookId, true);
            Optional<Book> deletedBook = libraryService.getBook(bookId);
            assertThat(deletedBook).isNotPresent();
            logger.info("Verified that the book with book ID: {} has been successfully deleted", bookId);
        } else {
            logger.info("Book with book ID: {}, not found. Cannot proceed with deletion", bookId);
            System.out.println("Book not found");
        }
    }

    @Test
    public void testDeleteNonExistingBook() {
        String bookId = "B213";
        Optional<Book> book = libraryService.getBook(bookId);

        if(book.isPresent()) {
            //Verify if the book exists in the database
            boolean isDeleted = libraryService.deleteBook(bookId);
            logger.info("Delete operation for book with ID:{}, returned: {}", bookId, isDeleted);
            assertThat(isDeleted).isTrue();
            //Verify the book no longer exists in the database
            Optional<Book> deletedBook = libraryService.getBook(bookId);
            assertThat(deletedBook).isNotPresent();
            logger.info("Verify that book with ID: {} has been successfully deleted", bookId);
        } else {
            logger.info("Book not found");
        }
    }

    @Test
    public void testUpdateBook() {
        String bookId = "B101";

        //Verify if the book exists in the database
        Optional<Book> existingBook = libraryService.getBook(bookId);
        if(existingBook.isPresent()) {
            //Updating the book's details
            logger.info("Book with ID: {} exists in the database. Proceeding to update", bookId);
            JsonNode updatedBookDetail = new ObjectMapper().createObjectNode()
                    .put("publishing year", "2022")
                    .put("genre", "Entrepreneurship");
            Book updatedBook = new Book(bookId, "Ankur Warikoo", "Do Epic Shit", updatedBookDetail);
            boolean isUpdated = libraryService.updateBook(updatedBook);
            logger.info("Update operation for the bookId: {}, returned: {}", bookId, isUpdated);
            assertThat(isUpdated).isTrue();

            //Fetching the updated book and verifying the changes
            Optional<Book> retrievedBook= libraryService.getBook(bookId);
            assertThat(retrievedBook).isPresent();
            logger.info("Updated book retrieved successfully from the database");
            assertThat(retrievedBook.get().getBookAuthor()).isEqualTo(updatedBook.getBookAuthor());
            assertThat(retrievedBook.get().getBookTitle()).isEqualTo(updatedBook.getBookTitle());
            assertThat(retrievedBook.get().getBookDetail()).isEqualTo(updatedBook.getBookDetail());
            logger.info("Verified all updated values for the book with book ID: {}", bookId);

        } else {
            logger.warn("Book not found with book ID: {}. Update operation failed", bookId);
        }
    }

    @Test
    public void testUpdateNonExistingBook() {
        String bookId = "B213";

        //Verify if the book exists in the database
        Optional<Book> existingBook = libraryService.getBook(bookId);
        if(existingBook.isPresent()) {
            //Updating the book's details
            JsonNode updatedBookDetail = new ObjectMapper().createObjectNode()
                    .put("publishing year", "2022")
                    .put("genre", "Entrepreneurship");
            Book updatedBook = new Book(bookId, "Ankur Warikoo", "Do Epic Shit", updatedBookDetail);
            boolean isUpdated = libraryService.updateBook(updatedBook);
            assertThat(isUpdated).isTrue();

            //Fetching the updated book and verifying the changes
            Optional<Book> retrievedBook= libraryService.getBook(bookId);
            assertThat(retrievedBook).isPresent();
            assertThat(retrievedBook.get().getBookAuthor()).isEqualTo(updatedBook.getBookAuthor());
            assertThat(retrievedBook.get().getBookTitle()).isEqualTo(updatedBook.getBookTitle());
            assertThat(retrievedBook.get().getBookDetail()).isEqualTo(updatedBook.getBookDetail());
            System.out.println("Book updated successfully with all the values verified");
        } else {
            System.out.println("Book not found in the database, update failed");
        }
    }

    void contextLoads() {
        assertThat(libraryService).isNotNull();
        assertThat(libraryController).isNotNull();
    }

}
