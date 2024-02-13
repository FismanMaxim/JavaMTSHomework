package com.example.javamtshomework.Controllers;

import com.example.javamtshomework.Models.Book;
import com.example.javamtshomework.Services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class BooksViewController {
    private final BookService bookService;

    @Autowired
    public BooksViewController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books")
    public String viewBooks(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "books";
    }
}
