package org.example.EntitiesServices;

import org.example.CustomExceptions.ArticlesExceptions.*;
import org.example.Entities.Article;
import org.example.Entities.Comment;
import org.example.Entities.EntitiesIds.ArticleId;
import org.example.EntitiesRepositories.ArticleRepository;

import java.util.List;
import java.util.Set;

public class ArticleService {
    private final ArticleRepository articleRepository;


    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public List<Article> getAll() {
        return articleRepository.getArticles();
    }

    public Article getById(ArticleId articleId) {
        try {
            return articleRepository.getArticleById(articleId);
        } catch (ArticleIdNotFoundException e) {
            throw new ArticleFindException("Failed to find article with id: " + articleId.getId(), e);
        }
    }

    public ArticleId createArticle(String name, Set<String> tags, List<Comment> comments) {
        ArticleId articleId = articleRepository.generateId();
        Article article = new Article(articleId, name, tags, comments);

        try {
            articleRepository.createArticle(article);
        } catch (ArticleDuplicatedException e) {
            throw new ArticleCreateException("Cannot create article", e);
        }

        return articleId;
    }

    public void updateArticle(ArticleId articleId, String name, Set<String> tags) {
        Article article;

        try {
            article = articleRepository.getArticleById(articleId);
        } catch (ArticleIdNotFoundException e) {
            throw new UpdateArticleException("Cannot find article with id: " + articleId.getId(), e);
        }

        try {
            articleRepository.updateArticle(
                    article.withName(name)
                            .WithTags(tags)
            );
        } catch (ArticleIdNotFoundException e) {
            throw new UpdateArticleException("Cannot update article with id: " + articleId.getId(), e);
        }
    }

    public void deleteArticle(ArticleId articleId) {
        try {
            articleRepository.deleteArticle(articleId);
        } catch (ArticleIdNotFoundException e) {
            throw new DeleteArticleException("Cannot delete article with id: " + articleId.getId(), e);
        }
    }
}
