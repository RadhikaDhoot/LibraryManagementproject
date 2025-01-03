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

    public boolean createAuthor(Author author) {
        //Validating while the author to be created contains all the required field or not
        validateAuthor(author);
        Optional<Author> newAuthor = getAuthor(author.getAuthorId());
        if(newAuthor.isPresent()) {
            return false;
        } else {
            authorRepository.createAuthor(author);
            return true;
        }
    }

    private void validateAuthor(Author author) {
        //Validating the author objects
        if(author.getAuthorId() == null || author.getAuthorId().isEmpty()) {
            throw new IllegalArgumentException("Error: Author Id is required as authorId and it cannot be null or empty");
        }
        if(author.getAuthorName() == null || author.getAuthorName().isEmpty()) {
            throw new IllegalArgumentException("Error: Author Name is required as authorName and it cannot be null or empty");
        }
    }

    public boolean updateAuthor(Author author) {

       //Validating while the author to be updated contains all the required field or not
        validateAuthor(author);
        Optional<Author> updatingAuthor = getAuthor(author.getAuthorId());
        //Checking while the author to be updated exists or not
        if(updatingAuthor.isPresent()) {
            authorRepository.updateAuthor(author);
            return true;
        }
        return false;
    }

    public boolean deleteAuthor(String authorId) {
        //Checking while the author to be deleted exists or not
        Optional<Author> deletingAuthor = getAuthor(authorId);
        if(deletingAuthor.isPresent()) {
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

    public boolean createBook(Book book) {
        //Validating while the book to be created contains all the required field or not
        validateBook(book);
        Optional<Book> newBook = getBook(book.getBookId());
        if(newBook.isPresent()) {
            return false;
        } else {
            bookRepository.createBook(book);
            return true;
        }
    }

    private void validateBook(Book book) {
        //Validating the book objects
        if(book.getBookId() == null || book.getBookId().isEmpty()) {
            throw new IllegalArgumentException("Error: Book ID is required as bookId and it cannot be null or empty");
        }
        if(book.getBookAuthor() == null || book.getBookAuthor().isEmpty()) {
            throw new IllegalArgumentException("Error: Book Author is required as bookAuthor and it cannot be null or empty");
        }
        if(book.getBookTitle() == null || book.getBookTitle().isEmpty()) {
            throw new IllegalArgumentException("Error: Book Title is required as bookTitle and it cannot be null or empty");
        }
        if(book.getBookDetail() == null || book.getBookDetail().isEmpty()) {
            throw new IllegalArgumentException("Error: Book Detail is required as bookDetail and it cannot be null or empty");
        }
    }

    public boolean updateBook(Book book) {
        //Validating while the book to be updated contains all the required field or not
        validateBook(book);
        //Checking while the book to be updated exists or not
        Optional<Book> updatingBook = getBook(book.getBookId());
        if(updatingBook.isPresent()) {
            bookRepository.updateBook(book);
            return true;
        }
        return false;
    }

    public boolean deleteBook(String bookId) {
        //Checking while the book to be deleted exists or not
        Optional<Book> deletingBook = getBook(bookId);
        if (deletingBook.isPresent()) {
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

    public List<Map<String, Object>> booksJoinAuthors() {
        String sql = "SELECT b.book_id, a.author_id, b.book_title " +
                "FROM books b JOIN authors a " +
                "ON b.book_author = a.author_name";
        return jdbcTemplate.queryForList(sql);
    }
}
