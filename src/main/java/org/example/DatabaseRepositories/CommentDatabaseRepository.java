package org.example.DatabaseRepositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.CustomExceptions.ArticlesExceptions.ArticleIdNotFoundException;
import org.example.CustomExceptions.CommentsExceptions.CommentIdDuplicatedException;
import org.example.CustomExceptions.CommentsExceptions.CommentIdNotFoundException;
import org.example.Entities.Article;
import org.example.Entities.Comment;
import org.example.Entities.EntitiesIds.ArticleId;
import org.example.Entities.EntitiesIds.CommentId;
import org.example.EntitiesRepositories.CommentRepository;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommentDatabaseRepository implements CommentRepository {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final Jdbi jdbi;
    private final CommentId nextId = new CommentId(0);

    public CommentDatabaseRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    public CommentId generateId() {
        return nextId.getAndIncrement();
    }

    @Override
    public void createComment(Comment comment) throws JsonProcessingException {
        jdbi.useTransaction((Handle handle) -> {
            handle
                    .createUpdate("INSERT INTO comment (id, articleId, content) VALUES (:id, :articleId, :content)")
                    .bind("id", comment.id().getId())
                    .bind("articleId", comment.articleId().getId())
                    .bind("content", comment.content())
                    .execute();
        });
    }

    @Override
    public Comment getCommentById(CommentId commentId, boolean forUpdate) throws CommentIdNotFoundException {
        try {
            return jdbi.inTransaction((Handle handle) -> {
                Map<String, Object> result =
                        handle.createQuery("SELECT * FROM comment WHERE id = :id " + (forUpdate ? "" : "FOR UPDATE"))
                                .bind("id", commentId.getId())
                                .mapToMap()
                                .first();

                Comment comment = new Comment(
                        new CommentId((long) result.get("id")),
                        new ArticleId((int) result.get("articleid")),
                        (String) result.get("content")
                );
                return comment;
            });
        } catch (Exception e) {
            throw new CommentIdNotFoundException();
        }
    }

    @Override
    public Comment getCommentById(CommentId commentId) throws CommentIdNotFoundException {
        return getCommentById(commentId, false);
    }

    @Override
    public void deleteComment(CommentId commentId) {
        jdbi.useTransaction((Handle handle) -> {
            handle.createUpdate("DELETE FROM comment WHERE id = :id")
                    .bind("id", commentId.getId())
                    .execute();
        });
    }
}
