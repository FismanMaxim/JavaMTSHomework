package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.EntitiesControllers.ArticleController;
import org.example.EntitiesControllers.CommentController;
import org.example.EntitiesRepositories.ArticleRepository;
import org.example.EntitiesRepositories.InMemoryArticleRepository;
import org.example.EntitiesRepositories.InMemoryCommentRepository;
import org.example.EntitiesServices.ArticleService;
import org.example.EntitiesServices.CommentService;
import org.example.FreeMarkerLibrary.ArticleFreemarkerController;
import org.example.FreeMarkerLibrary.FreeMarkerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;

import java.util.List;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Service service = Service.ignite();
        ObjectMapper mapper =  new ObjectMapper();

        ArticleRepository articleRepository = new InMemoryArticleRepository();
        final ArticleService articleService = new ArticleService(
                articleRepository
        );
        final CommentService commentService = new CommentService(
                new InMemoryCommentRepository(), articleRepository
        );

        Application application = new Application(
                List.of(
                        new ArticleController(
                                service,
                                articleService,
                                commentService,
                                mapper
                        ),
                        new CommentController(
                                service,
                                commentService,
                                mapper
                        ),
                        new ArticleFreemarkerController(
                                service,
                                articleService,
                                FreeMarkerTemplate.freeMarkerEngine()
                        )
                )
        );

        application.start();


    }
}