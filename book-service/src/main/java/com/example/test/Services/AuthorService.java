package com.example.test.Services;

import com.example.test.CustomExceptions.ItemNotFoundException;
import com.example.test.Models.Author;
import com.example.test.Models.DTOs.CreateAuthorRequest;
import com.example.test.Repositories.AuthorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    public Optional<Author> findById(long authorId) {
        return authorRepository.findById(authorId);
    }

    @Transactional
    public Author createAuthor(Author author) {
        return authorRepository.save(author);
    }

    @Transactional
    public void updateAuthor(long authorId, CreateAuthorRequest updatedCreateAuthorRequest) {
        Author author = authorRepository.findById(authorId).orElseThrow(
                () -> new ItemNotFoundException("Cannot update author with given id because it does not exist: id=" + authorId));
        if (updatedCreateAuthorRequest.firstName().isBlank()) author.setFirstName(updatedCreateAuthorRequest.firstName());
        if (!updatedCreateAuthorRequest.lastName().isBlank()) author.setLastName(updatedCreateAuthorRequest.lastName());
        authorRepository.save(author);
    }

    @Transactional
    public void deleteAuthorById(long authorId) {
        authorRepository.deleteById(authorId);
    }
}
