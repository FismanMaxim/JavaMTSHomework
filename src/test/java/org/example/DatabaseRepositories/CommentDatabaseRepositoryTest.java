package org.example.DatabaseRepositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.CustomExceptions.CommentsExceptions.CommentIdNotFoundException;
import org.example.Entities.Comment;
import org.example.Entities.EntitiesIds.ArticleId;
import org.example.Entities.EntitiesIds.CommentId;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommentDatabaseRepositoryTest {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static Jdbi jdbi;

    @BeforeAll
    static void beforeAll() {
        String h2Url = "jdbc:h2:~/test";
        Flyway flyway =
                Flyway.configure()
                        .outOfOrder(true)
                        .locations("classpath:db/migrations")
                        .dataSource(h2Url, "sa", "")
                        .load();
        flyway.migrate();
        jdbi = Jdbi.create(h2Url, "sa", "");
    }

    @BeforeEach
    void beforeEach() {
        jdbi.useTransaction(handle -> {
            handle.createUpdate("DELETE FROM article").execute();
            handle.createUpdate("DELETE FROM comment").execute();
        });
    }

    @Test
    void createComment() throws Exception {
        CommentDatabaseRepository commentRepo = new CommentDatabaseRepository(jdbi);

        final String content1 = "contents of comment 1";
        final String content2 = "contents of comment 2";

        Comment comment1 = new Comment(new CommentId(0), new ArticleId(0), content1);
        Comment comment2 = new Comment(new CommentId(1), new ArticleId(1), content2);

        commentRepo.createComment(comment1);
        commentRepo.createComment(comment2);

        Comment retrievedComment = commentRepo.getCommentById(new CommentId(0));
        Comment retrievedComment1 = commentRepo.getCommentById(new CommentId(1));

        assertEquals(0, retrievedComment.id().getId());
        assertEquals(0, retrievedComment.articleId().getId());
        assertEquals(content1, retrievedComment.content());
        assertEquals(1, retrievedComment1.id().getId());
        assertEquals(1, retrievedComment1.articleId().getId());
        assertEquals(content2, retrievedComment1.content());
    }

    @Test
    void removeComment() throws Exception {
        CommentDatabaseRepository commentRepo = new CommentDatabaseRepository(jdbi);

        final String content1 = "contents of comment 1";
        final String content2 = "contents of comment 2";

        Comment comment1 = new Comment(new CommentId(0), new ArticleId(0), content1);
        Comment comment2 = new Comment(new CommentId(1), new ArticleId(1), content2);

        commentRepo.createComment(comment1);
        commentRepo.createComment(comment2);

        commentRepo.deleteComment(comment1.id());

        assertThrows(CommentIdNotFoundException.class, () -> commentRepo.getCommentById(comment1.id()));
        assertNotNull(commentRepo.getCommentById(comment2.id()));
    }
}
