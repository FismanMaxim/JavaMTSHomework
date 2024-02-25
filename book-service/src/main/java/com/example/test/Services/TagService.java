package com.example.test.Services;

import com.example.test.CustomExceptions.ItemNotFoundException;
import com.example.test.Models.Tag;
import com.example.test.Models.DTOs.TagDTO;
import com.example.test.Repositories.TagRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TagService {
    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();

    }

    public Optional<Tag> findById(long tagId) {
        return tagRepository.findById(tagId);
    }

    @Transactional
    public Tag createTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Transactional
    public void updateTag(long tagId, TagDTO updatedTagDTO) {
        Tag tag = tagRepository.findById(tagId).orElseThrow(
                () -> new ItemNotFoundException("Cannot update tag with given id because it does not exist: id=" + tagId));
        if (updatedTagDTO.name().isBlank()) tag.setName(updatedTagDTO.name());
        tagRepository.save(tag);
    }

    @Transactional
    public void deleteTagById(long tagId) {
        tagRepository.deleteById(tagId);
    }
}
