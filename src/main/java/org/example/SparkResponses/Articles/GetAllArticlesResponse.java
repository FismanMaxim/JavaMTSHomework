package org.example.SparkResponses.Articles;

import org.example.Entities.Article;

import java.util.List;

public record GetAllArticlesResponse (List<Article> articles) {
}
