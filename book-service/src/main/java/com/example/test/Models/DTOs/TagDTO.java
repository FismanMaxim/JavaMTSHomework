package com.example.test.Models.DTOs;

import com.example.test.Models.Tag;
import lombok.Getter;

import java.util.Objects;

@Getter
public final class TagDTO extends EntityDTO<Tag> {
    private final String name;

    public TagDTO(String name) {
        this.name = name;
    }

    @Override
    protected Tag buildEntity() {
        return new Tag(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (TagDTO) obj;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}