package com.example.test.Services;

import com.example.test.CustomExceptions.ItemNotFoundException;
import com.example.test.Models.DTOs.TagDTO;
import com.example.test.Models.Tag;
import com.example.test.Repositories.TagRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagService implements CrudService<Tag, Long> {
    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Tag> getAll() {
        return tagRepository.findAll();

    }

    @Override
    public Optional<Tag> findById(Long tagId) {
        return tagRepository.findById(tagId);
    }

    @Transactional
    @Override
    public Tag create(Tag tag) {
        return tagRepository.save(tag);
    }

    @Transactional
    public void updateTag(long tagId, TagDTO updatedTagDTO) {
        Tag tag = tagRepository.findById(tagId).orElseThrow(
                () -> new ItemNotFoundException("Cannot update tag with given id because it does not exist: id=" + tagId));
        if (updatedTagDTO.getName().isBlank())
            tag.setName(updatedTagDTO.getName());
        tagRepository.save(tag);
    }

    @Transactional
    @Override
    public void delete(Long tagId) {
        tagRepository.deleteById(tagId);
    }
}
