package com.example.test.Models.DTOs;

import com.example.test.Models.Tag;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public final class TagDTO extends EntityDTO<Tag> {
    private final String name;

    @Override
    protected Tag buildEntity() {
        return new Tag(name);
    }
}