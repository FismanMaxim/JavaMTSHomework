package org.example.Entities;

import org.example.Entities.EntitiesIds.ArticleId;
import org.example.Entities.EntitiesIds.CommentId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArticleTest{
    @Test
    void verifyMethodsWith () {
        // Assign
        Article article = new Article(new ArticleId(1), "testName", Set.of(), List.of());
        Comment comment = new Comment(new CommentId(1), new ArticleId(1), "comment content");

        // Act
        article = article.withName("newName")
                .WithTags(Set.of("tag1", "tag2"))
                .WithComments(List.of(comment));

        // Assert
        assertEquals(article.name(), "newName");
        assertEquals(article.tags(), Set.of("tag1", "tag2"));
        assertEquals(article.comments(), List.of(comment));
    }

    @Test
    void equals() {
        // Assign
        Comment comment = new Comment(new CommentId(1), new ArticleId(1), "comment content");
        Article article1 = new Article(new ArticleId(1), "name", Set.of("tag1", "tag2"), List.of(comment));
        Article article2 = new Article(new ArticleId(1), "name2", Set.of("tag2", "tag3"), List.of(comment, comment));

        // Assert
        assertEquals(article1, article2); // Articles should be considered equal if their ids match
    }
}