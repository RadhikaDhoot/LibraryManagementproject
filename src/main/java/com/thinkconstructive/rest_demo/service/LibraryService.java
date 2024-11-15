package com.thinkconstructive.rest_demo.service;

import com.thinkconstructive.rest_demo.model.Author;
import com.thinkconstructive.rest_demo.model.Book;
import com.thinkconstructive.rest_demo.repository.AuthorRepository;
import com.thinkconstructive.rest_demo.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibraryService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    public LibraryService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }


    public void createBook(Book book) {
        bookRepository.createBook(book);
    }

    public void updateBook(Book book) {
        bookRepository.updateBook(book);
    }

    public void deleteBook(String book_id) {
        bookRepository.deleteBook(book_id);
    }

    public Optional<Book> getBook(String book_id) {
        return bookRepository.getBook(book_id);
    }

    public List<Book> getAllBooks() {
        return bookRepository.getAllBooks();
    }

    public void createAuthor(Author author) {
        authorRepository.createAuthor(author);
    }

    public void updateAuthor(Author author) {
        authorRepository.updateAuthor(author);
    }

    public void deleteAuthor(String author_id) {
        authorRepository.deleteAuthor(author_id);
    }

    public Optional<Author> getAuthor(String author_id) {
        return authorRepository.getAuthor(author_id);
    }

    public List<Author> getAllAuthors() {
        return authorRepository.getAllAuthor();
    }

}

