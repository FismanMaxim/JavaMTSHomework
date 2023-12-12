package org.example.EntitiesControllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.CustomExceptions.ArticlesExceptions.ArticleCreateException;
import org.example.CustomExceptions.ArticlesExceptions.ArticleFindException;
import org.example.CustomExceptions.ArticlesExceptions.DeleteArticleException;
import org.example.CustomExceptions.ArticlesExceptions.UpdateArticleException;
import org.example.CustomExceptions.CommentsExceptions.CommentFindException;
import org.example.Entities.Article;
import org.example.Entities.Comment;
import org.example.Entities.EntitiesIds.ArticleId;
import org.example.Entities.EntitiesIds.CommentId;
import org.example.EntitiesServices.ArticleService;
import org.example.EntitiesServices.CommentService;
import org.example.SparkRequests.Articles.CreateArticleRequest;
import org.example.SparkRequests.Articles.UpdateArticleRequest;
import org.example.SparkResponses.Articles.CreateArticleResponse;
import org.example.SparkResponses.Articles.FindArticleResponse;
import org.example.SparkResponses.Articles.GetAllArticlesResponse;
import org.example.SparkResponses.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

import java.util.ArrayList;
import java.util.List;

public class ArticleController implements EntityController {
    private static final Logger LOG = LoggerFactory.getLogger(ArticleController.class);

    private final Service service;
    private final ArticleService articleService;
    private final CommentService commentService;
    private final ObjectMapper objectMapper;

    public ArticleController(Service service, ArticleService articleService, CommentService commentService, ObjectMapper objectMapper) {
        this.service = service;
        this.articleService = articleService;
        this.commentService = commentService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void initializeEndpoints() {
        createArticleEndpoint();
        getAllArticlesEndpoint();
        findArticleEndpoint();
        updateArticleEndpoint();
        deleteArticleEndpoint();
    }

    private void createArticleEndpoint() {
        service.post("/api/articles", (Request request, Response response) -> {
            response.type("application/json");

            CreateArticleRequest createArticleRequest = objectMapper
                    .readValue(request.body(), CreateArticleRequest.class);

            List<Comment> comments = loadCommentsByTheirIds(createArticleRequest.getCommentsIds());

            try {
                ArticleId articleId = articleService.createArticle(
                        createArticleRequest.getName(),
                        createArticleRequest.getTags(),
                        comments
                );
                response.status(201);
                LOG.debug("Create article query handled successfully. Created article with id={}", articleId.getId());

                return objectMapper.writeValueAsString(new CreateArticleResponse(articleId));
            } catch (ArticleCreateException e) {
                LOG.warn("Failed to create article", e);
                response.status(400);
                return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
            }
        });
    }

    private void getAllArticlesEndpoint() {
        service.get("/api/articles", (Request request, Response response) -> {
            response.type("application/java");

            List<Article> articles = articleService.getAll();
            response.status(200);
            LOG.debug("Get all articles query handled successfully. Found {} articles", articles.size());
            return objectMapper.writeValueAsString(new GetAllArticlesResponse(articles));
        });
    }

    private void findArticleEndpoint() {
        service.get("/api/articles/:articleId", (Request request, Response response) -> {
            response.type("application/java");

            ArticleId articleId = new ArticleId(Long.parseLong(request.params("articleId")));

            try {
                Article article = articleService.getById(articleId);
                article = article.WithComments(loadCommentsByTheirIds(article.getCommentsIds()));
                response.status(200);
                LOG.debug("Find article with id query handled successfully. Found articles with id={}", articleId.getId());
                return objectMapper.writeValueAsString(new FindArticleResponse(article));
            } catch (ArticleFindException e) {
                LOG.warn("Find article with id query failed. Cannot find article with id={}", articleId.getId(), e);
                response.status(404);
                return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
            }
        });
    }

    private void updateArticleEndpoint() {
        service.patch("/api/articles/:articleId", (Request request, Response response) -> {
            response.type("application/json");

            ArticleId articleId = new ArticleId(Long.parseLong(request.params("articleId")));
            UpdateArticleRequest updateRequest = objectMapper
                    .readValue(request.body(), UpdateArticleRequest.class);

            try {
                articleService.updateArticle(
                        articleId,
                        updateRequest.name(),
                        updateRequest.tags()
                );
                response.status(204);
                LOG.warn("Update article query handled successfully. Updated article with id={}", articleId);
                return 0;
            } catch (UpdateArticleException e) {
                LOG.warn("Update article query failed for article id={}", articleId, e);
                response.status(400);
                return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
            }
        });
    }

    private void deleteArticleEndpoint() {
        service.delete("/api/articles/:articleId", (Request request, Response response) -> {
            response.type("application/json");

            ArticleId articleId = new ArticleId(Long.parseLong(request.params("articleId")));

            try {
                articleService.deleteArticle(articleId);
                response.status(204);
                LOG.debug("Delete article query handled successfully. Deleted article with id={}", articleId);
                return 0;
            } catch (DeleteArticleException e) {
                LOG.warn("Delete article query failed. Cannot delete article with id:{}", articleId, e);
                response.status(404);
                return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
            }
        });
    }

    private List<Comment> loadCommentsByTheirIds(List<CommentId> commentsIds, boolean printWarn) {
        // Extracting comments from their indexes
        List<Comment> comments = new ArrayList<>();
        List<CommentId> failedCommentsIds = new ArrayList<>();
        for (CommentId commentsId : commentsIds) {
            try {
                comments.add(commentService.getCommentById(commentsId));
            } catch (CommentFindException e) {
                failedCommentsIds.add(commentsId);
            }
        }

        if (printWarn && !failedCommentsIds.isEmpty()) {
            StringBuilder warnString = new StringBuilder("Failed to find comments with the following ids:\n");
            for (CommentId failedCommentsId : failedCommentsIds) warnString.append(failedCommentsId.getId());
            LOG.warn(warnString.toString());
        }

        return comments;
    }

    private List<Comment> loadCommentsByTheirIds(List<CommentId> commentsIds) {
        return loadCommentsByTheirIds(commentsIds, true);
    }
}
