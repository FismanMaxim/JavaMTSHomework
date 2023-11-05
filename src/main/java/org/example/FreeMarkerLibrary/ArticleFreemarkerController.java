package org.example.FreeMarkerLibrary;

import org.example.Entities.Article;
import org.example.EntitiesControllers.EntityController;
import org.example.EntitiesServices.ArticleService;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Service;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticleFreemarkerController implements EntityController {
    private final Service service;
    private final ArticleService articleService;
    private final FreeMarkerEngine freeMarkerEngine;

    public ArticleFreemarkerController(
            Service service,
            ArticleService articleService,
            FreeMarkerEngine freeMarkerEngine
    ) {
        this.service = service;
        this.articleService = articleService;
        this.freeMarkerEngine = freeMarkerEngine;
    }

    @Override
    public void initializeEndpoints() {
        getAllArticlesWithNumberOfComments();
    }

    private void getAllArticlesWithNumberOfComments() {
        service.get(
                "/",
                (Request request, Response response) -> {
                    response.type("text/html; charset=utf-8");
                    List<Article> articles = articleService.getAll();
                    List<Map<String, String>> articlesMap = articles.stream()
                            .map(article -> Map.of(
                                    "name", article.name(),
                                    "countComments", Integer.toString(article.comments().size()))
                            )
                            .toList();

                    Map<String, Object> model = new HashMap<>();
                    model.put("articles", articlesMap);
                    return freeMarkerEngine.render(new ModelAndView(model, "articlesCountComments.ftl"));
                }
        );
    }
}
