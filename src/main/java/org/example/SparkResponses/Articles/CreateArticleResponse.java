package org.example.SparkResponses.Articles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.Entities.EntitiesIds.ArticleId;

//public record CreateArticleResponse(ArticleId id) {
//}

public record CreateArticleResponse(ArticleId id) {
    @JsonCreator
    public CreateArticleResponse(
            @JsonProperty("id") ArticleId id
    ) {
        this.id = id;
    }
}