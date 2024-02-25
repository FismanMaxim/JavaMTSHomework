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
public class AuthorService implements CrudService<Author, Long> {
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public List<Author> getAll() {
        return authorRepository.findAll();
    }

    @Override
    public Optional<Author> findById(Long authorId) {
        return authorRepository.findById(authorId);
    }

    @Transactional
    @Override
    public Author create(Author author) {
        return authorRepository.save(author);
    }

    @Transactional
    public void updateAuthor(long authorId, CreateAuthorRequest updatedCreateAuthorRequest) {
        Author author = authorRepository.findById(authorId).orElseThrow(
                () -> new ItemNotFoundException("Cannot update author with given id because it does not exist: id=" + authorId));
        if (updatedCreateAuthorRequest.getFirstName().isBlank())
            author.setFirstName(updatedCreateAuthorRequest.getFirstName());
        if (!updatedCreateAuthorRequest.getLastName().isBlank())
            author.setLastName(updatedCreateAuthorRequest.getLastName());
        authorRepository.save(author);
    }

    @Transactional
    @Override
    public void delete(Long authorId) {
        authorRepository.deleteById(authorId);
    }
}
