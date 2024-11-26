package com.thinkconstructive.rest_demo.service;

import com.thinkconstructive.rest_demo.model.Author;
import com.thinkconstructive.rest_demo.model.Book;
import com.thinkconstructive.rest_demo.repository.AuthorRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import com.thinkconstructive.rest_demo.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LibraryService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final JdbcTemplate jdbcTemplate;
    public LibraryService(BookRepository bookRepository, AuthorRepository authorRepository, JdbcTemplate jdbcTemplate) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createBook(Book book) {
        bookRepository.createBook(book);
    }

    public boolean updateBook(Book book) {
        bookRepository.updateBook(book);
        return true;
    }

    public boolean deleteBook(String bookId) {
        bookRepository.deleteBook(bookId);
        return true;
    }

    public Optional<Book> getBook(String bookId) {
        return bookRepository.getBook(bookId);
    }

    public List<Book> getAllBooks() {
        return bookRepository.getAllBooks();
    }

    public void createAuthor(Author author) {
        authorRepository.createAuthor(author);
    }

    public boolean updateAuthor(Author author) {
        authorRepository.updateAuthor(author);
        return true;
    }

    public boolean deleteAuthor(String author_id) {
        authorRepository.deleteAuthor(author_id);
        return true;
    }

    public Optional<Author> getAuthor(String author_id) {
        return authorRepository.getAuthor(author_id);
    }

    public List<Author> getAllAuthors() {
        return authorRepository.getAllAuthor();
    }

    public List<Map<String, Object>> booksJoinAuthors() {
        String sql = "SELECT b.book_id, a.author_id, b.book_title " +
                "FROM books b JOIN authors a " +
                "ON b.book_author = a.author_name";
        return jdbcTemplate.queryForList(sql);
    }
}

