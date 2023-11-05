package org.example.EntitiesRepositories;

import org.example.CustomExceptions.ArticlesExceptions.ArticleDuplicatedException;
import org.example.CustomExceptions.ArticlesExceptions.ArticleIdNotFoundException;
import org.example.CustomExceptions.CommentsExceptions.CommentIdDuplicatedException;
import org.example.Entities.Article;
import org.example.Entities.Comment;
import org.example.Entities.EntitiesIds.ArticleId;
import org.example.Entities.EntitiesIds.CommentId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryArticleRepository implements ArticleRepository {
    private final ArticleId nextId = new ArticleId(0);
    private final Map<ArticleId, Article> articles = new ConcurrentHashMap<>();

    @Override
    public ArticleId generateId() {
        return nextId.getAndIncrement();
    }

    @Override
    public List<Article> getArticles() {
        return new ArrayList<>(articles.values());
    }

    @Override
    public Article getArticleById(ArticleId articleId) throws ArticleIdNotFoundException {
        Article article = articles.get(articleId);
        if (article == null)
            throw new ArticleIdNotFoundException("Cannot find article with id " + articleId);
        return article;
    }

    @Override
    public synchronized void createArticle(Article article) throws ArticleDuplicatedException {
        if (articles.get(article.id()) != null)
            throw new ArticleDuplicatedException("Article with given id already exists: " + article.id());
        articles.put(article.id(), article);
    }

    @Override
    public synchronized void updateArticle(Article article)  throws ArticleIdNotFoundException {
        if (articles.get(article.id()) == null)
            throw new ArticleIdNotFoundException("Cannot find article with id " + article.id());
        articles.put(article.id(), article);
    }

    @Override
    public synchronized void deleteArticle(ArticleId articleId) throws ArticleIdNotFoundException {
        if (articles.get(articleId) == null)
            throw new ArticleIdNotFoundException("Cannot find article with id " + articleId);
        articles.remove(articleId);
    }

    @Override
    public void addCommentToArticle(ArticleId articleId, Comment comment)
            throws ArticleIdNotFoundException, CommentIdDuplicatedException {
        if (articles.get(articleId) == null)
            throw new ArticleIdNotFoundException("Cannot find article with id " + articleId);

        Article article = articles.get(articleId).withNewComment(comment);
        articles.put(articleId, article);
    }

    @Override
    public void removeCommentFromArticle(ArticleId articleId, CommentId commentId)
            throws ArticleIdNotFoundException {
        Article article = getArticleById(articleId);
        article = article.withoutComment(commentId);
        updateArticle(article);
    }
}
