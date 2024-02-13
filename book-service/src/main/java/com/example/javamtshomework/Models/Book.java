package com.example.javamtshomework.Models;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
public class Book {
    public final long id;

    @NotNull(message = "Author is not defined")
    public String author;

    @NotNull(message = "Title is not defined")
    public String title;

    @NotNull(message = "Tags are not defined")
    @Getter(AccessLevel.NONE)
    public Set<String> tags;


    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }
}
