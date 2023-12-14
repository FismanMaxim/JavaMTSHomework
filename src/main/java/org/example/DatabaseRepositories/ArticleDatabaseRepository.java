package org.example.DatabaseRepositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.CustomExceptions.ArticlesExceptions.ArticleDuplicatedException;
import org.example.CustomExceptions.ArticlesExceptions.ArticleIdNotFoundException;
import org.example.CustomExceptions.CommentsExceptions.CommentIdDuplicatedException;
import org.example.CustomExceptions.CommentsExceptions.CommentIdNotFoundException;
import org.example.Entities.Article;
import org.example.Entities.Comment;
import org.example.Entities.EntitiesIds.ArticleId;
import org.example.Entities.EntitiesIds.CommentId;
import org.example.EntitiesRepositories.ArticleRepository;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArticleDatabaseRepository implements ArticleRepository {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final Jdbi jdbi;
    private final ArticleId nextId = new ArticleId(0);


    public ArticleDatabaseRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    public ArticleId generateId() {
        return nextId.getAndIncrement();
    }

    @Override
    public List<Article> getArticles() throws JsonProcessingException {
        return jdbi.inTransaction((Handle handle) -> {
            List<Map<String, Object>> results =
                    handle.createQuery("SELECT * FROM article")
                            .mapToMap()
                            .list();

            List<Article> articles = new ArrayList<>();
            for (Map<String, Object> result : results) {
                CommentId[] commentsIds = null;
                try {
                    commentsIds = mapper.readValue((String) result.get("commentsids"), CommentId[].class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                List<Comment> comments = new ArrayList<>();
                for (CommentId commentId : commentsIds)
                    comments.add(new Comment(commentId));

                articles.add(new Article(
                        new ArticleId((long) result.get("id")),
                        (String) result.get("name"),
                        mapper.readValue((String) result.get("tags"), Set.class),
                        comments,
                        (boolean) result.get("trending")));
            }

            return articles;
        });
    }

    @Override
    public void createArticle(Article article) throws JsonProcessingException {
        jdbi.useTransaction((Handle handle) -> {
            handle
                    .createUpdate("INSERT INTO article (id, name, tags, commentsids, trending) VALUES (:id, :name, :tags, :comments, :trending)")
                    .bind("id", article.id().getId())
                    .bind("name", article.name())
                    .bind("tags", mapper.writeValueAsString(article.tags()))
                    .bind("comments", mapper.writeValueAsString(article.getCommentsIds()))
                    .bind("trending", article.trending())
                    .execute();
        });
    }

    public void createArticles(String[] articlesJsons) throws JsonProcessingException {
        List<Article> articles = new ArrayList<>();
        for (String json : articlesJsons) articles.add(mapper.readValue(json, Article.class));

        jdbi.useTransaction((Handle handle) -> {
            for (Article article : articles) {
                jdbi.useTransaction((Handle handle2) -> {
                    handle2
                            .createUpdate("INSERT INTO article (id, name, tags, commentsids, trending) VALUES (:id, :name, :tags, :comments, :trending)")
                            .bind("id", article.id().getId())
                            .bind("name", article.name())
                            .bind("tags", mapper.writeValueAsString(article.tags()))
                            .bind("comments", mapper.writeValueAsString(article.getCommentsIds()))
                            .bind("trending", article.trending())
                            .execute();
                });
            }
        });
    }

    public Article getArticleById(ArticleId articleId, boolean forUpdate) throws ArticleIdNotFoundException {
        try {
            return jdbi.inTransaction((Handle handle) -> {
                Map<String, Object> result =
                        handle.createQuery("SELECT * FROM article WHERE id = :id " + (forUpdate ? "" : "FOR UPDATE"))
                                .bind("id", articleId.getId())
                                .mapToMap()
                                .first();

                CommentId[] commentsIds = mapper.readValue((String) result.get("commentsids"), CommentId[].class);
                List<Comment> comments = new ArrayList<>();
                for (var commentIdJson : commentsIds)
                    comments.add(
                            new Comment(commentIdJson));

                return new Article(
                        new ArticleId((long) result.get("id")),
                        (String) result.get("name"),
                        mapper.readValue((String) result.get("tags"), Set.class),
                        comments,
                        (boolean) result.get("trending")
                );
            });
        } catch (Exception e) {
            throw new ArticleIdNotFoundException();
        }
    }

    @Override
    public Article getArticleById(ArticleId articleId) throws ArticleIdNotFoundException, JsonProcessingException {
        return getArticleById(articleId, false);
    }

    @Override
    public void updateArticle(Article article) throws ArticleIdNotFoundException, JsonProcessingException {
        List<CommentId> commentsIds = new ArrayList<>();
        for (Comment comment : article.comments()) commentsIds.add(comment.id());

        jdbi.useTransaction((Handle handle) -> {
            handle.createUpdate("UPDATE article " +
                            "SET name = :name, tags = :tags, commentsids = :comments " +
                            "WHERE id = :id")
                    .bind("id", article.id().getId())
                    .bind("name", article.name())
                    .bind("tags", mapper.writeValueAsString(article.tags()))
                    .bind("comments", mapper.writeValueAsString(commentsIds))
                    .execute();
        });
    }

    @Override
    public void deleteArticle(ArticleId articleId) {
        jdbi.useTransaction((Handle handle) -> {
            handle.createUpdate("DELETE FROM article WHERE id = :id")
                    .bind("id", articleId.getId())
                    .execute();
        });
    }

    @Override
    public void addCommentToArticle(ArticleId articleId, Comment comment)
            throws CommentIdDuplicatedException, ArticleIdNotFoundException, ArticleDuplicatedException, JsonProcessingException {
        Article article = getArticleById(articleId);

        if (article.comments().stream().anyMatch(comment1 -> comment1.id() == comment.id()))
            throw new CommentIdDuplicatedException();

        deleteArticle(articleId);
        article = article.withNewComment(comment);
        createArticle(article);
    }

    @Override
    public void removeCommentFromArticle(ArticleId articleId, CommentId commentId)
            throws CommentIdNotFoundException, ArticleIdNotFoundException, JsonProcessingException, ArticleDuplicatedException {
        Article article = getArticleById(articleId);

        if (article.comments().stream().noneMatch(comment1 -> comment1.id().equals(commentId)))
            throw new CommentIdNotFoundException();

        deleteArticle(articleId);
        article = article.withoutComment(commentId);
        createArticle(article);
    }
}
