package com.example.test.Controllers;

import com.example.test.CustomExceptions.ApiError;
import com.example.test.CustomExceptions.ItemNotFoundException;
import com.example.test.Models.DTOs.TagDTO;
import com.example.test.Models.Tag;
import com.example.test.Services.TagService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Tag> getTag(@PathVariable long id) {
        var tagOpt = tagService.findById(id);
        return tagOpt.map(tag -> new ResponseEntity<>(tag, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Tag createTag(@Valid @RequestBody TagDTO tagDTO) {
        return tagService.create(tagDTO.get());
    }

    @PutMapping("{id}")
    public void updateTag(@PathVariable long id, @Valid @RequestBody TagDTO tagDTO) {
        tagService.updateTag(id, tagDTO);
    }

    @DeleteMapping("{id}")
    public void deleteBook(@PathVariable long id) {
        try {
            tagService.delete(id);
        } catch (ItemNotFoundException e) {
            throw new RestClientException("Cannot delete tag by id because it does not exist: id=" + id);
        }
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> itemNotFoundExceptionHandler(RestClientException e) {
        return new ResponseEntity<>(
                new ApiError(e.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }
}
