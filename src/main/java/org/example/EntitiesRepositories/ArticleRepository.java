package org.example.EntitiesRepositories;

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

    List<Article> getArticles();

    /**
     * @throws ArticleDuplicatedException
     */
    void createArticle(Article article) throws ArticleDuplicatedException;

    /**
     * @throws ArticleIdNotFoundException
     */
    Article getArticleById(ArticleId articleId) throws ArticleIdNotFoundException;

    /**
     * @throws ArticleIdNotFoundException
     */
    void updateArticle(Article article) throws ArticleIdNotFoundException;

    /**
     * @throws ArticleIdNotFoundException
     */
    void deleteArticle(ArticleId articleId) throws ArticleIdNotFoundException;

    /**
     * @throws CommentIdDuplicatedException
     * @throws ArticleIdNotFoundException
     */
    void addCommentToArticle(ArticleId articleId, Comment comment) throws CommentIdDuplicatedException, ArticleIdNotFoundException;

    /**
     * @throws CommentIdNotFoundException
     */
    void removeCommentFromArticle(ArticleId articleId, CommentId commentId) throws CommentIdNotFoundException, ArticleIdNotFoundException;
}
