package com.libraryManagement.service;

import com.libraryManagement.model.Author;
import com.libraryManagement.model.Book;
import com.libraryManagement.repository.AuthorRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import com.libraryManagement.repository.BookRepository;
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

    public boolean createBook(Book book) {
        bookRepository.createBook(book);
        return false;
    }

    public boolean updateBook(Book book) {
        Optional<Book> existingBook = getBook(book.getBookId());
        if(existingBook.isPresent()) {
            bookRepository.updateBook(book);
            return true;
        }
        return false;
    }

    public boolean deleteBook(String bookId) {
        Optional<Book> existingBook = getBook(bookId);
        if (existingBook.isPresent()) {
            bookRepository.deleteBook(bookId);
            return true;
        }
        return false;
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
        Optional<Author> existingAuthor = getAuthor(author.getAuthorId());
        if(existingAuthor.isPresent()) {
            authorRepository.updateAuthor(author);
            return true;
        }
        return false;
    }

    public boolean deleteAuthor(String authorId) {
        Optional<Author> existingAuthor = getAuthor(authorId);
        if(existingAuthor.isPresent()) {
            authorRepository.deleteAuthor(authorId);
            return true;
        }
        return false;
    }

    public Optional<Author> getAuthor(String authorId) {
        return authorRepository.getAuthor(authorId);
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
