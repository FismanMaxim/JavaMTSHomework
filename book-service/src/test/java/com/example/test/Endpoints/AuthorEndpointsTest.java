package com.example.test.Endpoints;

import com.example.test.Controllers.AuthorController;
import com.example.test.CustomExceptions.ItemNotFoundException;
import com.example.test.Models.Author;
import com.example.test.Models.DTOs.CreateAuthorRequest;
import com.example.test.Services.AuthorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorController.class)
public class AuthorEndpointsTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorService authorService;
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testGetAuthorEndpoint() throws Exception {
        Author dummyAuthor = new Author("John", "Doe", new ArrayList<>());

        when(authorService.findById(anyLong())).thenReturn(Optional.of(dummyAuthor));

        mockMvc.perform(get("/api/authors/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andReturn();

        verify(authorService).findById(anyLong());
    }

    @Test
    void testGetAuthorEndpointNotFound() throws Exception {
        long nonExistingAuthorId = 99L;

        when(authorService.findById(nonExistingAuthorId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/authors/{id}", nonExistingAuthorId))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(authorService).findById(nonExistingAuthorId);
    }

    @Test
    void testCreateAuthorEndpoint() throws Exception {
        CreateAuthorRequest createAuthorRequest = new CreateAuthorRequest("John", "Doe");
        String json = objectMapper.writeValueAsString(createAuthorRequest);

        when(authorService.createAuthor(any(Author.class))).thenReturn(createAuthorRequest.getAuthor());

        mockMvc.perform(
                        post("/api/authors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        verify(authorService).createAuthor(any(Author.class));
    }

    @Test
    void testUpdateAuthorEndpoint() throws Exception {
        long authorId = 1L;
        CreateAuthorRequest createAuthorRequest = new CreateAuthorRequest("newName", "newLastName");
        String json = objectMapper.writeValueAsString(createAuthorRequest);

        doNothing().when(authorService).updateAuthor(authorId, createAuthorRequest);

        mockMvc.perform(
                        put("/api/authors/{id}", authorId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isOk())
                .andReturn();

        verify(authorService).updateAuthor(authorId, createAuthorRequest);
    }

    @Test
    void testDeleteAuthorEndpoint() throws Exception {
        long authorId = 1L;

        doNothing().when(authorService).deleteAuthorById(authorId);

        mockMvc.perform(delete("/api/authors/{id}", authorId))
                .andExpect(status().isOk())
                .andReturn();

        verify(authorService).deleteAuthorById(authorId);
    }

    @Test
    void testDeleteAuthorEndpointNotFound() throws Exception {
        long nonExistingAuthorId = 99L;

        doThrow(new ItemNotFoundException("Author not found")).when(authorService).deleteAuthorById(nonExistingAuthorId);

        mockMvc.perform(delete("/api/authors/{id}", nonExistingAuthorId))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(authorService).deleteAuthorById(nonExistingAuthorId);
    }
}
