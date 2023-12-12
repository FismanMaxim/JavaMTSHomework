package org.example.DatabaseRepositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.CustomExceptions.ArticlesExceptions.ArticleDuplicatedException;
import org.example.CustomExceptions.ArticlesExceptions.ArticleIdNotFoundException;
import org.example.Entities.Article;
import org.example.Entities.Comment;
import org.example.Entities.EntitiesIds.ArticleId;
import org.example.Entities.EntitiesIds.CommentId;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ArticleDatabaseRepositoryTest {
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
        jdbi.useTransaction(handle -> handle.createUpdate("DELETE FROM article").execute());
    }

    @Test
    void createArticle() throws ArticleDuplicatedException, JsonProcessingException, ArticleIdNotFoundException {
        ArticleDatabaseRepository controller  = new ArticleDatabaseRepository(jdbi);

        Comment comment1 = new Comment(new CommentId(0), new ArticleId(0), "contents of comment 1");
        Comment comment2 = new Comment(new CommentId(1), new ArticleId(1), "contents of comment 1");

        Article article = new Article(new ArticleId(0), "name", new HashSet<>(List.of("tag1", "tag2")), List.of(comment1, comment2), false);

        controller.createArticle(article);
        Article articleGet = controller.getArticleById(new ArticleId(0));

        assertEquals("name", articleGet.name());
        assertEquals(2, articleGet.tags().size());
        assertEquals(2, articleGet.comments().size());
    }

    @Test
    void createArticles() throws JsonProcessingException, ArticleIdNotFoundException {
        ArticleDatabaseRepository controller  = new ArticleDatabaseRepository(jdbi);

        Comment comment1 = new Comment(new CommentId(0), new ArticleId(0), "contents of comment 1");
        Comment comment2 = new Comment(new CommentId(1), new ArticleId(0), "contents of comment 0");

        Article[] articlesToAdd = {
                new Article(new ArticleId(0), "name", new HashSet<>(List.of("tag1", "tag2")), List.of(comment1, comment2), false)
        };
        String[] articlesJsons = new String[articlesToAdd.length];
        for (int i = 0; i < articlesToAdd.length; i++) articlesJsons[i] = mapper.writeValueAsString(articlesToAdd[i]);

        controller.createArticles(articlesJsons);
        Article article = controller.getArticleById(new ArticleId(0));

        assertEquals("name", article.name());
        assertEquals(2, article.tags().size());
        assertEquals(2, article.comments().size());
    }

    @Test
    void getAllArticles() throws JsonProcessingException {
        ArticleDatabaseRepository controller  = new ArticleDatabaseRepository(jdbi);

        Comment comment1 = new Comment(new CommentId(0), new ArticleId(0), "contents of comment 1");
        Comment comment2 = new Comment(new CommentId(1), new ArticleId(0), "contents of comment 2");
        Comment comment3 = new Comment(new CommentId(2), new ArticleId(1), "contents of comment 3");


        Article[] articlesToAdd = {
                new Article(new ArticleId(0), "name", new HashSet<>(List.of("tag1", "tag2")), List.of(comment1, comment2), false),
                new Article(new ArticleId(1), "name1", new HashSet<>(List.of("tag3")), List.of(comment3), false)
        };
        String[] articlesJsons = new String[articlesToAdd.length];
        for (int i = 0; i < articlesToAdd.length; i++) articlesJsons[i] = mapper.writeValueAsString(articlesToAdd[i]);

        controller.createArticles(articlesJsons);
        var articles = controller.getArticles();

        assertEquals(2, articles.size());
        assertEquals("name", articles.get(0).name());
        assertEquals(2, articles.get(0).tags().size());
        assertEquals(2, articles.get(0).comments().size());
        assertEquals("name1", articles.get(1).name());
        assertEquals(1, articles.get(1).tags().size());
        assertEquals(1, articles.get(1).comments().size());
    }

    @Test
    void removeArticle() throws JsonProcessingException, ArticleIdNotFoundException {
        ArticleDatabaseRepository controller  = new ArticleDatabaseRepository(jdbi);

        Comment comment1 = new Comment(new CommentId(0), new ArticleId(0), "contents of comment 1");
        Article[] articlesToAdd = {
                new Article(new ArticleId(0), "name", new HashSet<>(List.of("tag1", "tag2")), List.of(comment1), false)
        };
        String[] articlesJsons = new String[articlesToAdd.length];
        for (int i = 0; i < articlesToAdd.length; i++) articlesJsons[i] = mapper.writeValueAsString(articlesToAdd[i]);

        controller.createArticles(articlesJsons);
        controller.deleteArticle(articlesToAdd[0].id());

        assertThrows(ArticleIdNotFoundException.class, () -> controller.getArticleById(new ArticleId(0)));
    }

    @Test
    void updateArticle() throws JsonProcessingException, ArticleIdNotFoundException {
        ArticleDatabaseRepository controller  = new ArticleDatabaseRepository(jdbi);

        Comment comment1 = new Comment(new CommentId(0), new ArticleId(0), "contents of comment 1");
        Comment comment2 = new Comment(new CommentId(1), new ArticleId(1), "contents of comment 1");

        Article[] articlesToAdd = {
                new Article(new ArticleId(0), "name", new HashSet<>(List.of("tag1", "tag2")), List.of(comment1, comment2), false)
        };
        String[] articlesJsons = new String[articlesToAdd.length];
        for (int i = 0; i < articlesToAdd.length; i++) articlesJsons[i] = mapper.writeValueAsString(articlesToAdd[i]);

        controller.createArticles(articlesJsons);
        controller.updateArticle(articlesToAdd[0].withName("newName").WithTags(Set.of("newTag1")));
        Article article = controller.getArticleById(new ArticleId(0));

        assertEquals("newName", article.name());
        assertEquals(1, article.tags().size());
        assertTrue(article.tags().contains("newTag1"));
        assertEquals(2, article.comments().size());
    }
}