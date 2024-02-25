package com.example.test.Endpoints;

import com.example.test.Controllers.BookController;
import com.example.test.CustomExceptions.ItemNotFoundException;
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

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
        BookDTO bookDTO = new BookDTO(null, "", null);
        Book book = bookDTO.getBook();
        String json = objectMapper.writeValueAsString(bookDTO);

        when(bookService.createBook(any(Book.class))).thenReturn(book);

        mockMvc.perform(
                        post("/api/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        verify(bookService).createBook(any(Book.class));
    }

//    @Test
//    void testUpdateBookEndpoint() throws Exception {
//        long bookId = 1L;
//
//        doNothing().when(bookService).updateBookTitle(anyLong(), anyString());
//
//        mockMvc.perform(
//                        put("/api/books/{id}/title", bookId)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString("newTitle")))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        verify(bookService).updateBookTitle(eq(bookId), eq("newTitle"));
//    }

    @Test
    void testGetAllBooksEndpoint() throws Exception {
        when(bookService.getAllBooks()).thenReturn(null);

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andReturn();

        verify(bookService).getAllBooks();
    }

    @Test
    void testGetBookEndpoint() throws Exception {
        final long bookId = 1L;
        final String title = "Test title";
        Book dummyBook = new Book(null, title, null);

        when(bookService.getBook(bookId)).thenReturn(Optional.of(dummyBook));

        mockMvc.perform(get("/api/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        verify(bookService).getBook(bookId);
    }

    @Test
    void testGetBookEndpointNotFound() throws Exception {
        long nonExistingBookId = 1L;

        when(bookService.getBook(nonExistingBookId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/books/{id}", nonExistingBookId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""))
                .andReturn();

        verify(bookService).getBook(nonExistingBookId);
    }

    @Test
    void testDeleteBookEndpoint() throws Exception {
        long bookId = 1L;

        doNothing().when(bookService).deleteBook(bookId);

        mockMvc.perform(delete("/api/books/{id}", bookId))
                .andExpect(status().isOk())
                .andReturn();

        verify(bookService).deleteBook(bookId);
    }

    @Test
    void testDeleteBookEndpointNotFound() throws Exception {
        long nonExistingBookId = 99L;

        doThrow(new ItemNotFoundException()).when(bookService).deleteBook(nonExistingBookId);

        mockMvc.perform(delete("/api/books/{id}", nonExistingBookId))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(bookService).deleteBook(nonExistingBookId);
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