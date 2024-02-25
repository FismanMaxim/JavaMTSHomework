package com.example.test.Controllers;

import com.example.test.Models.Author;
import com.example.test.Models.Book;
import com.example.test.Models.Tag;
import com.example.test.Services.AuthorService;
import com.example.test.Services.BookService;
import com.example.test.Services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ViewController {
    private final BookService bookService;
    private final AuthorService authorService;
    private final TagService tagService;

    @Autowired
    public ViewController(BookService bookService, AuthorService authorService, TagService tagService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.tagService = tagService;
    }

    @GetMapping("/books")
    public String viewBooks(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "books";
    }

    @GetMapping("/authors")
    public String viewAuthors(Model model) {
        List<Author> authors = authorService.getAllAuthors();
        model.addAttribute("authors", authors);
        return "authors";
    }

    @GetMapping("/tags")
    public String viewTags(Model model) {
        List<Tag> tags = tagService.getAllTags();
        model.addAttribute("tags", tags);
        return "tags";
    }
}
