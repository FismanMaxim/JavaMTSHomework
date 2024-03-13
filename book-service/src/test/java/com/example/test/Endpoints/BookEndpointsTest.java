package com.example.test.Endpoints;

import com.example.test.Controllers.BookController;
import com.example.test.CustomExceptions.ItemNotFoundException;
import com.example.test.Models.Author;
import com.example.test.Models.Book;
import com.example.test.Models.DTOs.BookDTO;
import com.example.test.Services.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(BookController.class)
class BookEndpointsTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void testCreateBookEndpoint() throws Exception {
    BookDTO bookDTO = new BookDTO(new Author("A", "B"), "Title", Set.of());
        Book book = bookDTO.get();
        String json = objectMapper.writeValueAsString(bookDTO);

        when(bookService.create(any(Book.class))).thenReturn(book);

        mockMvc.perform(
                        post("/api/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        verify(bookService).create(any(Book.class));
    }

    @Test
    void testGetAllBooksEndpoint() throws Exception {
        when(bookService.getAll()).thenReturn(null);

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andReturn();

        verify(bookService).getAll();
    }

    @Test
    void testGetBookEndpoint() throws Exception {
        final long bookId = 1L;
        final String title = "Test title";
        Book dummyBook = new Book(null, title, null);

        when(bookService.findById(bookId)).thenReturn(Optional.of(dummyBook));

        mockMvc.perform(get("/api/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        verify(bookService).findById(bookId);
    }

    @Test
    void testGetBookEndpointNotFound() throws Exception {
        long nonExistingBookId = 1L;

        when(bookService.findById(nonExistingBookId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/books/{id}", nonExistingBookId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""))
                .andReturn();

        verify(bookService).findById(nonExistingBookId);
    }

    @Test
    void testDeleteBookEndpoint() throws Exception {
        long bookId = 1L;

        when(bookService.findById(any())).thenReturn(Optional.of(new Book(new Author("A", "B"), "", Set.of())));
        doNothing().when(bookService).delete(bookId);

        mockMvc.perform(delete("/api/books/{id}", bookId))
                .andExpect(status().isOk())
                .andReturn();

        verify(bookService).delete(bookId);
    }

    @Test
    void testFindBooksWithTagEndpoint() throws Exception {
        long tagId = 1L;
        List<Book> booksWithTag = Arrays.asList(new Book(null, "Title1", null), new Book(null, "Title2", null));

        when(bookService.findBooksByTag(tagId)).thenReturn(booksWithTag);

        mockMvc.perform(get("/api/books/tagged/{tagId}", tagId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Title1"))
                .andExpect(jsonPath("$[1].title").value("Title2"))
                .andReturn();

        verify(bookService).findBooksByTag(tagId);
    }

    @Test
    void testChangeAuthorEndpoint() throws Exception {
        long bookId = 1L;
        long newAuthorId = 10L;

        doNothing().when(bookService).changeBookAuthor(bookId, newAuthorId);

        mockMvc.perform(put("/api/books/{bookId}/changeAuthor/{newAuthorId}", bookId, newAuthorId))
                .andExpect(status().isOk())
                .andReturn();

        verify(bookService).changeBookAuthor(bookId, newAuthorId);
    }

    @Test
    void testChangeAuthorEndpointFailure() throws Exception {
        long nonExistingBookId = 99L;
        long newAuthorId = 10L;

        doThrow(new ItemNotFoundException()).when(bookService).changeBookAuthor(nonExistingBookId, newAuthorId);

        mockMvc.perform(put("/api/books/{bookId}/changeAuthor/{newAuthorId}", nonExistingBookId, newAuthorId))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(bookService).changeBookAuthor(nonExistingBookId, newAuthorId);
    }
}