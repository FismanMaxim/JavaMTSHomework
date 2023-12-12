package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.DatabaseRepositories.ArticleDatabaseRepository;
import org.example.DatabaseRepositories.CommentDatabaseRepository;
import org.example.Entities.Article;
import org.example.Entities.EntitiesIds.ArticleId;
import org.example.Entities.EntitiesIds.CommentId;
import org.example.EntitiesControllers.ArticleController;
import org.example.EntitiesControllers.CommentController;
import org.example.EntitiesRepositories.ArticleRepository;
import org.example.EntitiesRepositories.CommentRepository;
import org.example.EntitiesServices.ArticleService;
import org.example.EntitiesServices.CommentService;
import org.example.SparkResponses.Articles.CreateArticleResponse;
import org.example.SparkResponses.Articles.FindArticleResponse;
import org.example.SparkResponses.Comments.CreateCommentResponse;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.*;
import spark.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

public class Edge2EdgeTests {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Service service;
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
    void clearH2() {
        jdbi.useTransaction(handle -> {
            handle.createUpdate("DELETE FROM article").execute();
            handle.createUpdate("DELETE FROM comment").execute();
        });
    }

    @BeforeEach
    void igniteServer() {
        service = Service.ignite();
    }

    @AfterEach
    void stopServer() {
        service.stop();
        service.awaitStop();
    }

    @Test
    void e2eCreateEditDelete() throws IOException, InterruptedException {
        // Assign
        ArticleRepository articleRepository = new ArticleDatabaseRepository(jdbi);
        CommentRepository commentRepository = new CommentDatabaseRepository(jdbi);

        ArticleService articleService = new ArticleService(articleRepository);
        CommentService commentService = new CommentService(commentRepository, articleRepository);
        Application application = new Application(List.of(
                new ArticleController(service, articleService, commentService, objectMapper),
                new CommentController(service, commentService, objectMapper)
        ));
        application.start();
        service.awaitInitialization();
        HttpClient httpClient = HttpClient.newHttpClient();

        // region Create article
        HttpRequest request = HttpRequest.newBuilder()
                .POST(
                        HttpRequest.BodyPublishers.ofString(
                                """
                                          { "name":  "articleName", "tags": ["tag1", "tag2"], "comments": [] }
                                        """))
                .uri(URI.create("http://localhost:%d/api/articles".formatted(service.port())))
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        Assertions.assertEquals(201, response.statusCode());
        CreateArticleResponse createArticleResponse = objectMapper.readValue(response.body(), CreateArticleResponse.class);
        Assertions.assertEquals(new ArticleId(0), createArticleResponse.id());
        // endregion

        // region Add comment to the article
        request = HttpRequest.newBuilder()
                .POST(
                        HttpRequest.BodyPublishers.ofString(
                                """
                                        { "articleId": 0, "content": "my comment content" }"""
                        )
                )
                .uri(URI.create("http://localhost:%d/api/comments".formatted(service.port())))
                .build();


        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        Assertions.assertEquals(201, response.statusCode());
        CreateCommentResponse createCommentResponse = objectMapper.readValue(response.body(), CreateCommentResponse.class);
        Assertions.assertEquals(new CommentId(0), createCommentResponse.commentId());
        // endregion

        // region Edit article
        request = HttpRequest.newBuilder()
                .method("PATCH",
                        HttpRequest.BodyPublishers.ofString(
                                """
                                          { "name":  "changedName", "tags": ["tag1", "tag2", "tag3"] }
                                        """))
                .uri(URI.create("http://localhost:%d/api/articles/0".formatted(service.port())))
                .build();

        response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        Assertions.assertEquals(204, response.statusCode());
        // endregion

        // region Delete comment
        request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:%d/api/comments/0".formatted(service.port())))
                .build();

        response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        Assertions.assertEquals(204, response.statusCode());
        // endregion

        // region Get article
        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:%d/api/articles/0".formatted(service.port())))
                .build();

        response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        Assertions.assertEquals(200, response.statusCode());
        FindArticleResponse findArticleResponse = objectMapper.readValue(response.body(), FindArticleResponse.class);
        Article article = findArticleResponse.article();
        assert article.name().equals("changedName");
        Assertions.assertEquals(article.tags(), Set.of("tag1", "tag2", "tag3"));
        assert article.comments().isEmpty();
        // endregion

        // region Delete article
        request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:%d/api/articles/0".formatted(service.port())))
                .build();

        final HttpRequest finalRequest = request;
        Assertions.assertDoesNotThrow(() -> httpClient.send(finalRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)));
        // endregion
    }
}
