package com.libraryManagement;

import com.beust.ah.A;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryManagement.model.Author;
import com.libraryManagement.model.Book;
import com.libraryManagement.service.LibraryService;
import org.junit.jupiter.api.Test;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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
    private LibraryService libraryService;

    @Test
    void testGetAuthorDetails() throws Exception {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        }

        String authorId = "A101";
        Optional<Author> author = libraryService.getAuthor(authorId);
        if(author.isPresent()) {
            Author retrievedAuthor = author.get();
            assertThat(retrievedAuthor.getAuthorId()).isEqualTo(authorId);
            assertThat(retrievedAuthor.getAuthorName()).isEqualTo("James Clear");
            logger.info("Author found: {}", retrievedAuthor);
        } else {
            logger.warn("Author with authorId: {} does not exist", authorId);
        }
//        Mockito.when(libraryService.getAuthor(authorId)).thenReturn(author);

        mockMvc.perform(get("/library/authors/A101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorId").value("A101"))
                .andExpect(jsonPath("$.authorName").value("James Clear"));
    }

    @Test
    void testGetAllAuthors() throws Exception{
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        }

        List<Author> authors = libraryService.getAllAuthors();
        if(!authors.isEmpty()) {
            assertThat(authors.size()).isGreaterThan(0);

            Author firstAuthor = authors.get(0);
            assertThat(firstAuthor.getAuthorId()).isEqualTo("A101");
            assertThat(firstAuthor.getAuthorName()).isEqualTo("James Clear");
            logger.info("Author list retrieved successfully");
        } else {
            logger.warn("No authors available in the database");
        }

        mockMvc.perform(get("/library/authors"))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").isGreaterThan(0))
                .andExpect(jsonPath("$[0].authorId").value("A101"))
                .andExpect(jsonPath("$[0].authorName").value("James Clear"));
    }

    @Test
    void testCreateAuthor() throws Exception {
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        }

        //Creating a new Author to be added in the database
        Author newAuthor = new Author("A106", "Johanna Spyri");
        logger.info("Attempting to create a new author: {}", newAuthor);

        //Mocking the service behaviour for creating an author
//        Mockito.when(libraryService.createAuthor(newAuthor)).thenReturn(true);
//        ObjectMapper objectMapper = new ObjectMapper();

        //Adding the author
        boolean isAdded = libraryService.createAuthor(newAuthor);
        logger.info("Author creation is {} ", isAdded ? "Successful" : "Failed");

        mockMvc.perform(post("/library/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(newAuthor)))
                .andExpect(status().isOk())
                .andExpect(content().string("Author Created Successfully"));

        //Verifying if the author has been added successfully
        Optional<Author> retrievedAuthor = libraryService.getAuthor("A106");
        if(retrievedAuthor.isPresent()) {
            Author addedAuthor = retrievedAuthor.get();
            logger.info("Retrieved Author is {} ", addedAuthor);
            assertThat(addedAuthor.getAuthorId()).isEqualTo("A106");
            assertThat(addedAuthor.getAuthorName()).isEqualTo("Johanna Spyri");
        } else {
            logger.error("Added author is not found in the database");
        }
    }


//    @Test
//    void testCreateAuthor() throws Exception {
//        Author author = new Author("A001", "James Clear");
//        Mockito.when(libraryService.createAuthor(author)).thenReturn(true);
//        mockMvc.perform(post("/library/authors")
//                .contentType("application/json")
//                .content(objectMapper.writeValueAsString(author)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Author Created Successfully"));
//    }


    @Test
    void testDeleteAuthor() throws Exception {
        if(mockMvc == null) {
            mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        }

        Optional<Author> newAuthor = libraryService.getAuthor("A101");
        if(newAuthor.isPresent()) {
            Author authorToDelete = newAuthor.get();
            logger.info("Author to be deleted: {}", authorToDelete);

            mockMvc.perform(delete("/library/authors/A101"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Author Deleted Successfully"));

            Optional<Author> deletedAuthor = libraryService.getAuthor("A101");
            assertThat(deletedAuthor.isEmpty());
        } else {
            logger.warn("Issue deleting the author");
        }

    }
}
