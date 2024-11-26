package com.thinkconstructive.rest_demo.controller;

import com.thinkconstructive.rest_demo.model.Book;
import com.thinkconstructive.rest_demo.model.Author;
import com.thinkconstructive.rest_demo.service.LibraryService;
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
    public List<Book> getAllBook() {
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
    public String deleteBookDetails(@PathVariable("bookId") String bookId) {
        libraryService.deleteBook(bookId);
        return "Book Deleted Successfully";
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
    @PutMapping("/authors")
    public String updateAuthor(@RequestBody Author author) {
        libraryService.updateAuthor(author);
        return "Author Updated Successfully";
    }

    //Deleting a author record using its id
    @DeleteMapping("/authors/{authorId}")
    public String deleteAuthorDetails(@PathVariable("authorId") String authorId) {
        libraryService.deleteAuthor(authorId);
        return "Author Deleted Successfully";
    }

    //Using join between books and authors
    @GetMapping("books-join-authors")
    public List<Map< String, Object>> booksJoinAuthors() {
        return libraryService.booksJoinAuthors();
    }
}
