package org.example.SparkResponses.Articles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.Entities.Article;

public record FindArticleResponse (Article article) {
    @JsonCreator
    public FindArticleResponse(@JsonProperty("article") Article article) {
        this.article = article;
    }
}
