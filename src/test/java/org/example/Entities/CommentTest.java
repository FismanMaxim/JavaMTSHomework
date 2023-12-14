package org.example.Entities;

import org.example.Entities.EntitiesIds.ArticleId;
import org.example.Entities.EntitiesIds.CommentId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentTest {
     @Test
    void verifyMethodsWith() {
         // Assign
         Comment comment = new Comment(new CommentId(1), new ArticleId(1), "comment content");

         // Act
         comment = comment.withContent("new content");

         // Assert
         assertEquals("new content", comment.content());
     }

     @Test
    void equals() {
         // Assign
         Comment comment1 = new Comment(new CommentId(1), new ArticleId(1), "comment content");
         Comment comment2 = new Comment(new CommentId(1), new ArticleId(2), "different content");

         // Assert
         assertEquals(comment1, comment2); // Comments are considered equal iff their ids match
     }
}