package org.example.Entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.Entities.EntitiesIds.ArticleId;
import org.example.Entities.EntitiesIds.CommentId;

import java.util.Objects;

public record Comment(CommentId id, ArticleId articleId, String content) {
    @JsonCreator
    public Comment(
            @JsonProperty("id") CommentId id,
            @JsonProperty("articleId") ArticleId articleId,
            @JsonProperty("content") String content) {
        this.id = id;
        this.articleId = articleId;
        this.content = content;
    }

    public Comment(CommentId id) {
        this(id, null, "");
    }

    public Comment withContent(String content) {
        return new Comment(id, articleId, content);
    }

    @Override
    public String toString() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return id.equals(comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
