package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Entities.Article;
import org.example.Entities.EntitiesIds.ArticleId;
import org.example.Entities.EntitiesIds.CommentId;
import org.example.EntitiesControllers.ArticleController;
import org.example.EntitiesControllers.CommentController;
import org.example.EntitiesRepositories.ArticleRepository;
import org.example.EntitiesRepositories.InMemoryArticleRepository;
import org.example.EntitiesRepositories.InMemoryCommentRepository;
import org.example.EntitiesServices.ArticleService;
import org.example.EntitiesServices.CommentService;
import org.example.SparkResponses.Articles.CreateArticleResponse;
import org.example.SparkResponses.Articles.FindArticleResponse;
import org.example.SparkResponses.Comments.CreateCommentResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        ArticleRepository articleRepository = new InMemoryArticleRepository();
        ArticleService articleService = new ArticleService(articleRepository);
        CommentService commentService = new CommentService(new InMemoryCommentRepository(), articleRepository);
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
    }
}
