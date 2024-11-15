package com.thinkconstructive.rest_demo.controller;

import com.thinkconstructive.rest_demo.model.Book;
import com.thinkconstructive.rest_demo.model.Author;
import com.thinkconstructive.rest_demo.service.LibraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/library")
public class LibraryController {

    LibraryService libraryService;
    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping("/books/{book_id}")
    public ResponseEntity<Book> getBookDetails(@PathVariable("book_id") String book_id) {
        Optional<Book> book = libraryService.getBook(book_id);
        return book.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/books")
    public List<Book> getAllBook() {
        return libraryService.getAllBooks();
    }

    @PostMapping("/books")
    public String createBook(@RequestBody Book book) {
        libraryService.createBook(book);
        return "Book Created Successfully";
    }

    @PutMapping("/books")
    public String updateBook(@RequestBody Book book) {
        libraryService.updateBook(book);
        return "Book Updated Successfully";
    }

    @DeleteMapping("/books/{book_id}")
    public String deleteBookDetails(@PathVariable("book_id") String book_id) {
        libraryService.deleteBook(book_id);
        return "Book Deleted Successfully";
    }

    @GetMapping("/authors/{author_id}")
    public ResponseEntity<Author> getAuthorDetails(@PathVariable("author_id") String author_id) {
        Optional<Author> author = libraryService.getAuthor(author_id);
        return author.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/authors")
    public List<Author> getAllAuthor() {
        return libraryService.getAllAuthors();
    }

    @PostMapping("/authors")
    public String createAuthor(@RequestBody Author author) {
        libraryService.createAuthor(author);
        return "Author Created Successfully";
    }

    @PutMapping("/authors")
    public String updateAuthor(@RequestBody Author author) {
        libraryService.updateAuthor(author);
        return "Author Updated Successfully";
    }

    @DeleteMapping("/authors/{author_id}")
    public String deleteAuthorDetails(@PathVariable("author_id") String author_id) {
        libraryService.deleteAuthor(author_id);
        return "Author Deleted Successfully";
    }
}
