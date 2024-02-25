package com.example.test.Endpoints;

import com.example.test.Controllers.TagController;
import com.example.test.CustomExceptions.ItemNotFoundException;
import com.example.test.Models.DTOs.TagDTO;
import com.example.test.Models.Tag;
import com.example.test.Services.TagService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TagController.class)
public class TagEndpointsTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testGetTagEndpoint() throws Exception {
        Tag dummyTag = new Tag("name");

        when(tagService.findById(anyLong())).thenReturn(Optional.of(dummyTag));

        mockMvc.perform(get("/api/tags/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("name"))
                .andReturn();

        verify(tagService).findById(anyLong());
    }

    @Test
    void testGetTagEndpointNotFound() throws Exception {
        long nonExistingTagId = 99L;

        when(tagService.findById(nonExistingTagId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tags/{id}", nonExistingTagId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""))
                .andReturn();

        verify(tagService).findById(nonExistingTagId);
    }

    @Test
    void testCreateTagEndpoint() throws Exception {
        TagDTO tagDTO = new TagDTO("Java");
        String json = objectMapper.writeValueAsString(tagDTO);

        mockMvc.perform(
                        post("/api/tags")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        verify(tagService).create(any(Tag.class));
    }

    @Test
    void testUpdateTagEndpoint() throws Exception {
        long tagId = 1L;
        TagDTO tagDTO = new TagDTO("Updated Tag");
        String json = objectMapper.writeValueAsString(tagDTO);

        mockMvc.perform(
                        put("/api/tags/{id}", tagId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isOk())
                .andReturn();

        verify(tagService).updateTag(eq(tagId), any(TagDTO.class));
    }

    @Test
    void testDeleteTagEndpoint() throws Exception {
        long tagId = 1L;

        mockMvc.perform(delete("/api/tags/{id}", tagId))
                .andExpect(status().isOk())
                .andReturn();

        verify(tagService).delete(tagId);
    }

    @Test
    void testDeleteTagEndpointNotFound() throws Exception {
        long nonExistingTagId = 99L;

        doThrow(new ItemNotFoundException("Tag not found")).when(tagService).delete(nonExistingTagId);

        mockMvc.perform(delete("/api/tags/{id}", nonExistingTagId))
                .andExpect(status().isNotFound()) // Assuming you want to return 400 for failure
                .andReturn();

        verify(tagService).delete(nonExistingTagId);
    }
}
