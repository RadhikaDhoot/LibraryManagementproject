package com.libraryManagement.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.libraryManagement.model.Author;
import com.libraryManagement.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.jpa.repository.query.BadJpqlGrammarException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class BookRepository  {
    private static final Logger logger = LoggerFactory.getLogger(BookRepository.class);

    @Autowired
    private final JdbcTemplate jdbcTemplate;
    private ObjectMapper objectMapper = new ObjectMapper();

    public BookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //Inserting or Creating new book into the database
    public void createBook(Book book) {
        String sql = "INSERT INTO books(book_id, book_author, book_title, book_detail) VALUES (?, ?, ?, ?::jsonb)";
        logger.info("Book details to insert - bookId: {}, bookAuthor: {}, bookTitle: {}, bookDetail: {}",
                book.getBookId(), book.getBookAuthor(), book.getBookTitle(), book.getBookDetail());
        try {
            String bookDetailJson = book.getBookDetail().toString();
            jdbcTemplate.update(sql, book.getBookId(), book.getBookAuthor(), book.getBookTitle(), bookDetailJson);
            logger.info("SQL executed successfully. Book created in the database");
        } catch (DuplicateKeyException e) {
            logger.error("Error: book ID is a primary key and it already exists. {}", e.getMessage(), e);
            e.printStackTrace();
        } catch (DataIntegrityViolationException e) {
            logger.error("Error: Missing required fields: {}", e.getMessage());
        } catch (DataAccessException e) {
            logger.error("Error while creating the author: {}", e.getMessage());
        }
    }

    //Retrieving book by ID
//    public Optional<Book> getBook(String bookId) {
//        String sql = "SELECT * FROM books where book_id = ?";
//        logger.info("Executing SQL query to fetch the book ID: {}", bookId);
//        Book books = jdbcTemplate.query(sql, rs -> {
//            if (rs.next()) {
//                Book extractedBook = new Book();
//                extractedBook.setBookId(rs.getString("book_id"));
//                extractedBook.setBookAuthor(rs.getString("book_author"));
//                extractedBook.setBookTitle(rs.getString("book_title"));
//                String bookDetailJson = rs.getString("book_detail");
//
//                LobHandler lobHandler = new DefaultLobHandler();
//                bookDetailJson = lobHandler.getClobAsString(rs, "book_detail");
//                logger.info("Raw JSON String retrieved from database: {}", bookDetailJson);
//                try {
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    JsonNode bookDetail = objectMapper.readTree(bookDetailJson);
//                    extractedBook.setBookDetail(bookDetail);
//                    logger.info("Parsed JSON Node: {}", bookDetail);
//                } catch (Exception e) {
//                    throw new RuntimeException("Error while reading book_detail from JSON", e);
//                }
//                logger.info("Successfully mapped book with bookDetail: {}", extractedBook.getBookDetail());
//                return extractedBook;
//            }
//            return null;
//        }, bookId);
//
//        if(books == null) {
//            logger.warn("No book found with Id: {}", bookId);
//            return Optional.empty();
//        } else {
//            logger.info("Book retrieved successfully: {}", books);
//            return Optional.of(books);
//        }
//    }

    //Retrieving book by ID
    public Optional<Book> getBook(String bookId) {
        String sql = "SELECT * FROM books where book_id = ?";
        logger.info("Executing SQL query to fetch the book ID: {}", bookId);
        List<Book> books = jdbcTemplate.query(sql, new BookRowMapper(new ObjectMapper()), bookId);
        logger.debug("SQL query executed successfully. Number of books retrieved: {}", books.size());
        if(books.isEmpty()) {
            logger.warn("No book found with Id: {}", bookId);
            return Optional.empty();
        } else {
            Book retrievedBook = books.get(0);
            logger.info("Book retrieved successfully");
            return Optional.of(retrievedBook);
        }
    }

//    public Optional<Author> getAuthor(String authorId) {
//        String sql = "SELECT * FROM authors WHERE author_id = ?";
//        try {
//            logger.info("Executing SQL query to fetch the author with ID: {}", authorId);
//            List<Author> authors = jdbcTemplate.query(sql, new AuthorRepository.AuthorRowMapper(), authorId);
//            logger.debug("SQL query executed successfully. Number of authors retrieved: {}", authors.size());
//            if (authors.isEmpty()) {
//                logger.warn("No author found with Id: {}", authorId);
//                return Optional.empty();
//            } else {
//                Author retrievedAuthor = authors.get(0);
//                logger.info("Author retrieved successfully");
//                return Optional.of(retrievedAuthor);
//            }
//        } catch (DataAccessException e) {
//            throw new RuntimeException("Error occurred while fetching the author", e);
//        }
//    }

   // Retrieving all the books available in the database
    public List<Book> getAllBooks() {
        String sql = "SELECT * FROM books ORDER BY book_id";
        try {
            logger.info("Fetching all books from the database");
            List<Book> books = jdbcTemplate.query(sql, new BookRowMapper(new ObjectMapper()));
            logger.info("Successfully retrieved {} books", books.size());
            return books;
        } catch (DataAccessException e) {
            logger.error("Error occurred while fetching all books: {}", e.getMessage());
            throw new RuntimeException("Error occurred while fetching all books: {}", e);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while fetching all books: {}", e.getMessage());
            throw new RuntimeException("Unexpected error occurred while fetching all the book");
        }
    }

    //Updating an existing book
    public void updateBook(Book book) {
        String sql = "UPDATE books SET book_author = ?, book_title = ?, book_detail = ?::jsonb WHERE book_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, book.getBookAuthor(), book.getBookTitle(), book.getBookDetail(), book.getBookId());
            if (rowsAffected == 0) {
                throw new RuntimeException("No book found with ID: " + book.getBookId());
            }
        } catch (Exception e) {
            System.err.println("Error updating the book:" + e.getMessage());
            e.printStackTrace();
        }
    }

//    //Updating the author
//    public void updateAuthor(Author author) {
//        String sql = "UPDATE authors SET author_name = ? WHERE author_id = ?";
//        try {
//            int rowsAffected = jdbcTemplate.update(sql, author.getAuthorName(), author.getAuthorId());
//            if (rowsAffected == 0) {
//                logger.warn("No author found with ID: {}", author.getAuthorId());
//                throw new RuntimeException("No author found with ID: " + author.getAuthorId());
//            }
//            logger.info("Author updated successfully {}", author);
//        } catch (DataIntegrityViolationException e) {
//            logger.error("Error: Missing required fields for author '{}' : {}", author, e.getMessage());
//        } catch (DataAccessException e) {
//            logger.error("Error occurred while updating the author '{}' : {}", author, e.getMessage());
//        } catch (Exception e) {
//            logger.error("Error updating the author: {}",e.getMessage());
//        }
//    }
    //Deleting a book from database
    public void deleteBook(String bookId) {
        String sql = "DELETE FROM books WHERE book_id = ?";
        try {
            jdbcTemplate.update(sql, bookId);
        } catch (BadSqlGrammarException e) {
            logger.error("SQL syntax error while deleting the book: {}", e.getMessage());
        } catch (DataAccessException e) {
            logger.error("Error deleting the author with book ID {}", bookId);
        } catch (Exception e) {
            logger.error("Unexpected error while deleting the book", e);
        }
    }

    private static class BookRowMapper implements RowMapper<Book> {
        private final ObjectMapper objectMapper;

        public BookRowMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Book book = new Book();
            book.setBookId(rs.getString("book_id"));
            book.setBookAuthor(rs.getString("book_author"));
            book.setBookTitle(rs.getString("book_title"));
            String bookDetailJson = rs.getString("book_detail");
            LobHandler lobHandler = new DefaultLobHandler();
            bookDetailJson = lobHandler.getClobAsString(rs,"book_detail");
            logger.info("Raw JSON String retrieved from database: {}", bookDetailJson);
            try {
                JsonNode bookDetail = objectMapper.readTree(bookDetailJson);
                book.setBookDetail(bookDetail);
                logger.info("Parsed JSON Node: {}", bookDetail);
            } catch (Exception e) {
                throw new RuntimeException("Error while reading book_detail from JSON", e);
            }
            logger.info("Successfully mapped book with bookDetail: {}", book.getBookDetail());
            return book;
        }
    }
}