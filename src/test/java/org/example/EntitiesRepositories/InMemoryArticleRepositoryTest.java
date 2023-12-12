package org.example.EntitiesRepositories;

import org.example.CustomExceptions.ArticlesExceptions.ArticleDuplicatedException;
import org.example.CustomExceptions.ArticlesExceptions.ArticleIdNotFoundException;
import org.example.Entities.Article;
import org.example.Entities.Comment;
import org.example.Entities.EntitiesIds.ArticleId;
import org.example.Entities.EntitiesIds.CommentId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryArticleRepositoryTest {
    @Test
    void generateConsecutiveIds() {
        InMemoryArticleRepository rep = new InMemoryArticleRepository();

        // Assert
        assertEquals(rep.generateId(), new ArticleId(0));
        assertEquals(rep.generateId(), new ArticleId(1));
        assertEquals(rep.generateId(), new ArticleId(2));
    }

    @Test
    void consecutiveArticlesListAccess() throws ArticleDuplicatedException, ArticleIdNotFoundException {
        InMemoryArticleRepository repo = new InMemoryArticleRepository();
        Comment comment = new Comment(new CommentId(1), new ArticleId(1), "comment content");
        Article article = new Article(new ArticleId(1), "articleName", Set.of("tag"), List.of(comment));

        repo.createArticle(article);
        repo.updateArticle(article.withName("newName"));
        repo.removeCommentFromArticle(new ArticleId(1), new CommentId(1));

        Article actualArticle = repo.getArticleById(new ArticleId(1));

        assertEquals(actualArticle.name(), "newName");
        assertEquals(actualArticle.comments(), List.of());
        assertEquals(actualArticle.tags(), Set.of("tag"));
    }


    void concurrentArticlesListAccess() throws InterruptedException, ArticleIdNotFoundException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        InMemoryArticleRepository repo = new InMemoryArticleRepository();

        CountDownLatch latch1 = new CountDownLatch(100);

        for (int i = 0; i < 100; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    repo.createArticle(generateTestArticle(finalI));
                } catch (ArticleDuplicatedException e) {
                    Assertions.fail();
                }
                latch1.countDown();
            });
        }
        latch1.await();

        CountDownLatch latch2 = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    repo.updateArticle(generateTestArticle(finalI).withName("newName%d".formatted(finalI)));
                } catch (ArticleIdNotFoundException e) {
                    Assertions.fail();
                }
                latch2.countDown();
            });
        }
        latch2.await();

        CountDownLatch latch3 = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    repo.removeCommentFromArticle(new ArticleId(finalI), new CommentId(finalI));
                } catch (ArticleIdNotFoundException e) {
                    Assertions.fail();
                }
                latch3.countDown();
            });
        }
        latch3.await();

        // Assert
        for (int i = 0; i < 100; i++) {
            Article article = repo.getArticleById(new ArticleId(i));
            assertNotNull(article);
            assertEquals(article.name(), "newName%d".formatted(i));
            assertEquals(article.comments(), List.of());
            assertEquals(article.tags(),  Set.of("tag1ForArticle%d".formatted(i), "tag2ForArticle%d".formatted(i)));
        }
    }

    private Article generateTestArticle(int id) {
        return new Article(
                new ArticleId(id),
                "articleName%d".formatted(id),
                Set.of("tag1ForArticle%d".formatted(id), "tag2ForArticle%d".formatted(id)),
                List.of(new Comment(new CommentId(id), new ArticleId(id), "testCommentContent")));
    }
}