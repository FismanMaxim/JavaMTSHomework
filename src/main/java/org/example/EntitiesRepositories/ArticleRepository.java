package org.example.EntitiesRepositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.CustomExceptions.ArticlesExceptions.ArticleDuplicatedException;
import org.example.CustomExceptions.ArticlesExceptions.ArticleIdNotFoundException;
import org.example.CustomExceptions.CommentsExceptions.CommentIdDuplicatedException;
import org.example.CustomExceptions.CommentsExceptions.CommentIdNotFoundException;
import org.example.Entities.Article;
import org.example.Entities.Comment;
import org.example.Entities.EntitiesIds.ArticleId;
import org.example.Entities.EntitiesIds.CommentId;

import java.util.List;

public interface ArticleRepository {
    ArticleId generateId();

    List<Article> getArticles() throws JsonProcessingException;

    /**
     * @throws ArticleDuplicatedException
     */
    void createArticle(Article article) throws ArticleDuplicatedException, JsonProcessingException;

    /**
     * @throws ArticleIdNotFoundException
     */
    Article getArticleById(ArticleId articleId, boolean forUpdate) throws ArticleIdNotFoundException, JsonProcessingException;

    /**
     * gets article by if with forUpdate set to false
     */
    Article getArticleById(ArticleId articleId) throws ArticleIdNotFoundException, JsonProcessingException;


    /**
     * @throws ArticleIdNotFoundException
     */
    void updateArticle(Article article) throws ArticleIdNotFoundException, JsonProcessingException;

    /**
     * @throws ArticleIdNotFoundException
     */
    void deleteArticle(ArticleId articleId) throws ArticleIdNotFoundException;

    /**
     * @throws CommentIdDuplicatedException
     * @throws ArticleIdNotFoundException
     */
    void addCommentToArticle(ArticleId articleId, Comment comment) throws CommentIdDuplicatedException, ArticleIdNotFoundException, ArticleDuplicatedException, JsonProcessingException;

    /**
     * @throws CommentIdNotFoundException
     */
    void removeCommentFromArticle(ArticleId articleId, CommentId commentId) throws CommentIdNotFoundException, ArticleIdNotFoundException, JsonProcessingException, ArticleDuplicatedException;
}
