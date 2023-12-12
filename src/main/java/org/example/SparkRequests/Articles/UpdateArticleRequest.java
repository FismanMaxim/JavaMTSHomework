package org.example.SparkRequests.Articles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record UpdateArticleRequest(String name, Set<String> tags) {
    @JsonCreator
    public UpdateArticleRequest(
            @JsonProperty("name") String name,
            @JsonProperty("tags") Set<String> tags
    ) {
        this.name = name;
        this.tags = tags;
    }
}
