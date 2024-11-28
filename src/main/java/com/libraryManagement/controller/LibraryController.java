package com.libraryManagement.controller;

import com.libraryManagement.model.Book;
import com.libraryManagement.model.Author;
import com.libraryManagement.service.LibraryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/library")
public class LibraryController {

    LibraryService libraryService;
    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    //Fetching details of a specific book by its id
    @GetMapping("/books/{bookId}")
    public ResponseEntity<Book> getBookDetails(@PathVariable("bookId") String bookId) {
        Optional<Book> book = libraryService.getBook(bookId);
        return book.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //Fetching the list of books
    @GetMapping("/books")
    public List<Book> getAllBooks() {
        return libraryService.getAllBooks();
    }

    //Creating a new book record
    @PostMapping("/books")
    public String createBook(@RequestBody Book book) {
        libraryService.createBook(book);
        return "Book Created Successfully";
    }

    //Updating details of an existing book
    @PutMapping("/books/{bookId}")
    public ResponseEntity<String> updateBook(@PathVariable("bookId") String bookId, @RequestBody Book book) {
        book.setBookId(bookId);
        boolean isUpdated = libraryService.updateBook(book);
        if(isUpdated) {
            return ResponseEntity.ok("Book Updated Successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book Not Found or Update Failed");
        }
    }

    //Deleting a book record using its id
    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<String> deleteBookDetails(@PathVariable("bookId") String bookId) {
        boolean isDeleted = libraryService.deleteBook(bookId);
        if (isDeleted) {
            return ResponseEntity.ok("Book Deleted Successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book Not Found");
        }
    }

    // Fetching a specific author by its id
    @GetMapping("/authors/{authorId}")
    public ResponseEntity<Author> getAuthorDetails(@PathVariable("authorId") String authorId) {
        Optional<Author> author = libraryService.getAuthor(authorId);
        return author.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //Fetching the list of authors
    @GetMapping("/authors")
    public List<Author> getAllAuthor() {
        return libraryService.getAllAuthors();
    }

    //Creating a new author record
    @PostMapping("/authors")
    public String createAuthor(@RequestBody Author author) {
        libraryService.createAuthor(author);
        return "Author Created Successfully";
    }

    //Updating details of an existing author
    @PutMapping("/authors/{authorId}")
    public ResponseEntity<String> updateAuthor(@PathVariable("authorId") String authorId, @RequestBody Author author) {
        author.setAuthorId(authorId);
        boolean isUpdated = libraryService.updateAuthor(author);
        if(isUpdated) {
            return ResponseEntity.ok("Author Updated Successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Author Not Found or Update Failed");
            }
    }

    //Deleting a author record using its id
    @DeleteMapping("/authors/{authorId}")
    public ResponseEntity<String> deleteAuthorDetails(@PathVariable("authorId") String authorId) {
        boolean isDeleted = libraryService.deleteAuthor(authorId);
        if(isDeleted) {
            return ResponseEntity.ok("Author Deleted Successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Author Not Found");
        }
    }

    //Using join between books and authors
    @GetMapping("books-join-authors")
    public List<Map< String, Object>> booksJoinAuthors() {
        return libraryService.booksJoinAuthors();
    }

}
