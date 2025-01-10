package com.libraryManagement.controller;

import com.libraryManagement.model.Book;
import com.libraryManagement.model.Author;
import com.libraryManagement.service.LibraryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/library")
public class LibraryController {

    private final LibraryService libraryService;
    private static final Logger logger = LoggerFactory.getLogger(LibraryController.class);
    @Autowired
    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    // Fetching a specific author by its id
    @GetMapping("/authors/{authorId}")
    public ResponseEntity<?> getAuthorDetails(@PathVariable("authorId") String authorId) {
        try {
            Optional<Author> author = libraryService.getAuthor(authorId);
            return author.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } catch (BadSqlGrammarException e) {
            logger.error("SQL syntax error {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SQL syntax error occurred: {}" + e.getMessage());
        } catch (Exception e) {
            logger.error("Error fetching the author: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred" + e.getMessage());
        }
    }

    //Fetching the list of authors
    @GetMapping("/authors")
    public ResponseEntity<?> getAllAuthor() {
        try {
            List<Author> authors = libraryService.getAllAuthors();
            return ResponseEntity.ok(authors);
        } catch (BadSqlGrammarException e) {
            logger.error("SQL syntax error {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SQL syntax error occurred: {}" + e.getMessage());
        } catch (Exception e) {
            logger.error("Error while fetching the authors: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred" + e.getMessage());
        }
    }

    //Creating a new author record
    @PostMapping("/authors")
    public ResponseEntity<String> createAuthor(@RequestBody Author author) {
        try {
            boolean isCreated = libraryService.createAuthor(author);
            if (isCreated) {
                return ResponseEntity.ok("Author Created Successfully");
            } else {
                logger.error("Author already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Author already exist");
            }
        } catch (BadSqlGrammarException e) {
            logger.error("SQL Syntax error {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SQL syntax error occurred: {}" + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Check the variables name");
        } catch (DataAccessException e) {
            logger.error("Error occurred while creating the author '{}' : {}", author, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while creating the author: {}" + e.getMessage());
        } catch (Exception e) {
            logger.error("Error while creating the author: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //Updating details of an existing author
    @PutMapping("/authors/{authorId}")
    public ResponseEntity<String> updateAuthor(@PathVariable("authorId") String authorId, @RequestBody Author author) {
        try {
            //Check if the author exists
            Optional<Author> existingAuthor = libraryService.getAuthor(authorId);
            if(existingAuthor.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Author not found with ID: " + authorId);
            boolean isUpdated = libraryService.updateAuthor(author);
            if (isUpdated) {
                return ResponseEntity.ok("Author Updated Successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Author Not Found or Update Failed");
            }
        } catch (BadSqlGrammarException e) {
            logger.error("SQL Syntax error {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SQL syntax error occurred: {}" + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Error: Missing required fields for author '{}' : {}", author, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (DataAccessException e) {
            logger.error("Error occurred while updating the author '{}' : {}", author, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while updating the author: {}" + e.getMessage());
        } catch (Exception e) {
            logger.error("Error while updating the author: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //Deleting an author record using its id
    @DeleteMapping("/authors/{authorId}")
    public ResponseEntity<String> deleteAuthorDetails(@PathVariable("authorId") String authorId) {
        try {
            boolean isDeleted = libraryService.deleteAuthor(authorId);
            if (isDeleted) {
                return ResponseEntity.ok("Author Deleted Successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Delete Failed");
            }
        } catch (BadSqlGrammarException e) {
            logger.error("SQL syntax error while deleting author with ID {}:{}", authorId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SQL syntax error occurred: " + e.getMessage());
        } catch (DataAccessException e) {
            logger.error("Database error occurred while deleting the author with ID {}:{}", authorId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error occurred: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error occurred while deleting the author with ID {}:{}", authorId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred" + e.getMessage());
        }
    }

    //Fetching details of a specific book by its id
    @GetMapping("/books/{bookId}")
    public ResponseEntity<?> getBookDetails(@PathVariable("bookId") String bookId) {
        try {
            Optional<Book> book = libraryService.getBook(bookId);
            return book.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } catch (BadSqlGrammarException e) {
            logger.error("SQL syntax error {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SQL syntax error occurred: {}" + e.getMessage());
        } catch (Exception e) {
            logger.error("Error fetching the book: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred" + e.getMessage());
        }
    }

    //Fetching the list of books
    @GetMapping("/books")
    public ResponseEntity<?> getAllBooks() {
        try {
            List<Book> books = libraryService.getAllBooks();
            return ResponseEntity.ok(books);
        } catch (BadSqlGrammarException e) {
            logger.error("SQL syntax error {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SQL syntax error occurred: {}" + e.getMessage());
        } catch (Exception e) {
            logger.error("Error while fetching the books: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred" + e.getMessage());
        }
    }
    //Creating a new book record
    @PostMapping("/books")
    public ResponseEntity<String> createBook(@RequestBody Book book) {
        try {
            boolean isCreated = libraryService.createBook(book);
            if (isCreated) {
                return ResponseEntity.ok("Book Created Successfully");
            } else {
                logger.error("Book already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Book already exist");
            }
        } catch (BadSqlGrammarException e) {
            logger.error("SQL Syntax error {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SQL syntax error occurred: {}" + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Check the variables name");
        } catch (DataAccessException e) {
            logger.error("Error occurred while creating the book '{}' : {}", book, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while creating the book: {}" + e.getMessage());
        } catch (Exception e) {
            logger.error("Error while creating the book: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    //Updating details of an existing book
    @PutMapping("/books/{bookId}")
    public ResponseEntity<String> updateBook(@PathVariable("bookId") String bookId, @RequestBody Book book) {
        try {
            //Check if the book exists
            Optional<Book> existingBook = libraryService.getBook(bookId);
            if(existingBook.isPresent())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found with ID: " + bookId);
            book.setBookId(bookId);
            boolean isUpdated = libraryService.updateBook(book);
            if(isUpdated) {
                return ResponseEntity.ok("Book Updated Successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book Not Found or Update Failed");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //Deleting a book record using its id
    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<String> deleteBookDetails(@PathVariable("bookId") String bookId) {
        try {
            boolean isDeleted = libraryService.deleteBook(bookId);
            if (isDeleted) {
                return ResponseEntity.ok("Book Deleted Successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Delete Failed");
            }
        } catch (BadSqlGrammarException e) {
            logger.error("SQL syntax error while deleting book with ID {}:{}", bookId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SQL syntax error occurred on deleting the book: " + e.getMessage());
        } catch (DataAccessException e) {
            logger.error("Database error occurred while deleting the book with ID {}:{}", bookId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error occurred: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error occurred while deleting the book with ID {}:{}", bookId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred" + e.getMessage());
        }
    }

    //Using join between books and authors
    @GetMapping("books-join-authors")
    public List<Map<String, Object>> booksJoinAuthors() {
        return libraryService.booksJoinAuthors();
    }

    @DeleteMapping("/deleteBooksByAuthorName/{authorName}")
    public ResponseEntity<String> deleteBooksByAuthorName(@PathVariable String authorName) {
        try {
            int rowsDeleted = libraryService.deleteBooksByAuthorName(authorName);
            if (rowsDeleted > 0) {
                return ResponseEntity.ok("Successfully deleted " + rowsDeleted + " books for author: " + authorName);
            } else {
                return ResponseEntity.ok("No books found for author: " + authorName);
            }
        } catch (BadSqlGrammarException e) {
            logger.error("SQL syntax error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SQL syntax error: " + e.getMessage());
        } catch (DataAccessException e) {
            logger.error("Data access error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Data access error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }

}