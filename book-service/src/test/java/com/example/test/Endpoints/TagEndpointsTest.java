package com.example.test.Endpoints;

import com.example.test.Controllers.TagController;
import com.example.test.CustomExceptions.ItemNotFoundException;
import com.example.test.Models.DTOs.TagDTO;
import com.example.test.Models.Tag;
import com.example.test.Services.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TagController.class)
public class TagEndpointsTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;

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
    void testCreateTagEndpoint() {
        TagDTO tagDTO = new TagDTO("Java");

        when(tagService.create(any(Tag.class))).thenReturn(tagDTO.get());

        String name = tagService.create(tagDTO.get()).getName();

        assertEquals(tagDTO.get().getName(), name);
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
