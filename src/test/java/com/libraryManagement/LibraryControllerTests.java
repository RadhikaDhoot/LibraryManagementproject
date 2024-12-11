package com.libraryManagement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.libraryManagement.model.Author;
import com.libraryManagement.model.Book;
import com.libraryManagement.service.LibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
public class LibraryControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testGetAuthorDetails() throws Exception {
        MvcResult result = mockMvc.perform(get("/authors/A101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author_id").value("A101"))
                .andExpect(jsonPath("$.author_name").value("James Clear"))
                .andReturn();
        System.out.println("Response: " + result.getResponse().getContentAsString());
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
}
