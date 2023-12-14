package org.example.SparkRequests.Comments;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.example.Entities.EntitiesIds.ArticleId;

public class CreateCommentRequest {
    private ArticleId articleId;
    private String content;


    @JsonCreator
    public CreateCommentRequest(
            @JsonProperty("articleId") long articleId,
            @JsonProperty("content") String content
    ) {
        this.articleId = new ArticleId(articleId);
        this.content = content;
    }

    public ArticleId getArticleId() {
        return articleId;
    }

    public String getContent() {
        return content;
    }
}
