package com.example.test.Models.DTOs;

import com.example.test.Models.Tag;
import com.fasterxml.jackson.annotation.JsonIgnore;

public record TagDTO(String name) {
    @JsonIgnore
    public Tag getTag() {
        return new Tag(name);
    }
}