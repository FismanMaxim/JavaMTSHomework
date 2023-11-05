package org.example.EntitiesServices;

import org.example.CustomExceptions.ArticlesExceptions.ArticleFindException;
import org.example.CustomExceptions.ArticlesExceptions.DeleteArticleException;
import org.example.CustomExceptions.ArticlesExceptions.UpdateArticleException;
import org.example.Entities.EntitiesIds.ArticleId;
import org.example.EntitiesRepositories.InMemoryArticleRepository;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ArticleServiceTest {
    @Test
    void errorOnGetNonExistingArticle () {
        ArticleService service = new ArticleService(new InMemoryArticleRepository());
        assertThrows(ArticleFindException.class, () -> service.getById(new ArticleId(0)));
    }

    @Test
    void errorOnUpdateNonExistingArticle() {
        ArticleService service = new ArticleService(new InMemoryArticleRepository());

        assertThrows(UpdateArticleException.class, () -> service.updateArticle(new ArticleId(0), "name", Set.of()));
    }

    @Test
    void errorOnDeleteNonExistingArticle() {
        ArticleService service = new ArticleService(new InMemoryArticleRepository());

        assertThrows(DeleteArticleException.class, () -> service.deleteArticle(new ArticleId(0)));
    }
}