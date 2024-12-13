package com.libraryManagement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryManagement.model.Author;
import com.libraryManagement.model.Book;
import com.libraryManagement.service.LibraryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Type;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@ActiveProfiles("test")
public class LibraryControllerTests {

    private static final Logger logger = LoggerFactory.getLogger(LibraryControllerTests.class);
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    @Mock
    private LibraryService libraryService;

    @Test
    void testGetAuthorDetails() throws Exception {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        }
        String authorId = "A102";
        Optional<Author> checkAuthor = libraryService.getAuthor(authorId);
        if(checkAuthor.isPresent()) {
            logger.info("Attempting to fetch details author with authorId: {}", authorId);
            mockMvc.perform(get("/library/authors/{authorId}", authorId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.authorId").value(authorId))
                    .andExpect(jsonPath("$.authorName").value("James Clear"));

            Optional<Author> deletedAuthor = libraryService.getAuthor(authorId);
            assertThat(deletedAuthor.isEmpty());
            logger.info("Author details fetched successfully with authorId: {}", authorId);
        } else {
            logger.warn("Issue fetching the author details with authorId: {}. Author does not exist", authorId);
        }
//     Mockito.when(libraryService.getAuthor(authorId)).thenReturn(author);
    }

    @Test
    void testGetNonExistingAuthorDetails() throws Exception {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        }
        String authorId = "A100";
        Optional<Author> checkAuthor = libraryService.getAuthor(authorId);
        if(checkAuthor.isPresent()) {
            logger.info("Attempting to fetch details author with authorId: {}", authorId);
            mockMvc.perform(get("/library/authors/{authorId}", authorId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.authorId").value(authorId))
                    .andExpect(jsonPath("$.authorName").value("James Clear"));

            Optional<Author> deletedAuthor = libraryService.getAuthor(authorId);
            assertThat(deletedAuthor.isEmpty());
            logger.info("Author details fetched successfully with authorId: {}", authorId);
        } else {
            logger.warn("Issue fetching the author details with authorId: {}. Author does not exist", authorId);
        }
    }

    @Test
    void testGetAllAuthors() throws Exception{
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        }
        logger.info("Attempting to fetch all Authors");
        mockMvc.perform(get("/library/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
        logger.info("Successfully fetched all the authors from the database");
    }

    @Test
    void testCreateAuthor() throws Exception {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        }

        //Creating a new Author to be added in the database
        Author newAuthor = new Author("A106", "Johanna Spyri");
        logger.info("Attempting to create a new author{}", newAuthor);

        mockMvc.perform(post("/library/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(newAuthor)))
                .andExpect(status().isOk())
                .andExpect(content().string("Author Created Successfully"));
        logger.info("Successfully created a new author: {}", newAuthor);
    }

    @Test
    void testUpdateAuthor() throws Exception {
        if(mockMvc == null) {
            mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        }

        //Updating an existing author in the database
        String authorId = "A103";
        Author updatedAuthor = new Author(authorId, "J. K. Rowling");
        logger.info("Attempting to update an existing author with authorId: {}", authorId);

        mockMvc.perform(put("/library/authors/{authorId}", authorId)
                .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedAuthor)))
                .andExpect(status().isOk())
                .andExpect(content().string("Author Updated Successfully"));
        logger.info("Successfully updated the author with authorId: {}", authorId);
    }

    @Test
    void testDeleteAuthor() throws Exception {
        if(mockMvc == null) {
            mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        }
        String authorId = "A105";
        Optional<Author> checkAuthor = libraryService.getAuthor(authorId);
        if(checkAuthor.isPresent()) {
            logger.info("Attempting to delete author with authorId: {}", authorId);

            mockMvc.perform(delete("/library/authors/{authorID}", authorId))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Author Deleted Successfully"));

            Optional<Author> deletedAuthor = libraryService.getAuthor(authorId);
            assertThat(deletedAuthor.isEmpty());
            logger.info("Author deleted successfully with authorId: {}", authorId);
        } else {
            logger.warn("Issue deleting the author, not exist");
        }
    }

    @Test
    void testDeleteNonExistingAuthor() throws Exception {
        if(mockMvc == null) {
            mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        }
        String authorId = "A100";
        Optional<Author> checkAuthor = libraryService.getAuthor(authorId);
        if(checkAuthor.isPresent()) {
            logger.info("Attempting to delete author with authorId: {}", authorId);

            mockMvc.perform(delete("/library/authors/{authorId}", authorId))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Author Deleted Successfully"));

            Optional<Author> deletedAuthor = libraryService.getAuthor(authorId);
            assertThat(deletedAuthor.isEmpty());
            logger.info("Author deleted successfully with authorId: {}", authorId);
        } else {
            logger.warn("Issue deleting the author, not exist");
        }
    }

    @Test
    void testGetBookDetails() throws Exception {
        if(mockMvc == null) {
            mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        }
        String bookId = "B103";
        Optional<Book> book = libraryService.getBook(bookId);
        if(book.isPresent()) {
            logger.info("Attempting to fetch book with bookId: {}",bookId);
            mockMvc.perform(get("/library/books/{bookId}", bookId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.bookId").value(bookId))
                    .andExpect(jsonPath("$.bookAuthor").value("Brain Tracy"))
                    .andExpect(jsonPath("$.bookTitle").value("Eat That Frog"));
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode expectedDetails = objectMapper.readTree("{\"publishing year\": \"2018\", \"genre\": \"Motivational\"}");
//            assertThat(retrievedBook.getBookDetail()).isEqualTo(expectedDetails);
        } else {
            logger.warn("Book with bookId: {} does not exist", bookId);
        }
    }

    @Test
    void testGetNonExistingBookDetails() throws Exception {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        }
        String bookId = "B100";
        Optional<Book> book = libraryService.getBook(bookId);
        if (book.isPresent()) {
            logger.info("Attempting to fetch book with bookId: {}", bookId);
            mockMvc.perform(get("/library/books/{bookId}", bookId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.bookId").value(bookId))
                    .andExpect(jsonPath("$.bookAuthor").value("Brain Tracy"))
                    .andExpect(jsonPath("$.bookTitle").value("Eat That Frog"));
        } else {
            logger.warn("Book with bookId: {} does not exist", bookId);
        }
    }

    @Test
    void testDeleteBook() throws Exception {
        if(mockMvc == null) {
            mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        }
        String bookId = "B105";
        Optional<Book> checkBook = libraryService.getBook(bookId);
        if(checkBook.isPresent()) {
            logger.info("Attempting to delete book with bookId: {}", bookId);

            mockMvc.perform(delete("/library/books/{bookId}", bookId))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Book Deleted Successfully"));

            Optional<Book> deletedBook = libraryService.getBook(bookId);
            assertThat(deletedBook.isEmpty());
            logger.info("Book deleted successfully with bookId: {}", bookId);
        } else {
            logger.warn("Issue deleting the book, not exist");
        }
    }

    @Test
    void testDeleteNonExistingBook() throws Exception {
        if(mockMvc == null) {
            mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        }
        String bookId = "B100";
        Optional<Book> checkBook = libraryService.getBook(bookId);
        if(checkBook.isPresent()) {
            logger.info("Attempting to delete book with bookId: {}", bookId);

            mockMvc.perform(delete("/library/books/{bookId}", bookId))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Book Deleted Successfully"));

            Optional<Book> deletedBook = libraryService.getBook(bookId);
            assertThat(deletedBook.isEmpty());
            logger.info("Book deleted successfully with bookId: {}", bookId);
        } else {
            logger.warn("Issue deleting the book, not exist");
        }
    }

    @Test
    void testGetAllBooks() throws Exception{
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        }
        logger.info("Attempting to fetch all Books");
        mockMvc.perform(get("/library/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
        logger.info("Successfully fetched all the books from the database");
    }
}
