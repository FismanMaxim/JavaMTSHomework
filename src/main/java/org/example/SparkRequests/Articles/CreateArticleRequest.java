package org.example.SparkRequests.Articles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.Entities.EntitiesIds.CommentId;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CreateArticleRequest {
    private final String name;
    private final Set<String> tags;
    private final List<CommentId> comments;

    @JsonCreator
    public CreateArticleRequest(
            @JsonProperty("name") String name,
            @JsonProperty("tags") Set<String> tags,
            @JsonProperty("comments") List<Long> comments
    ) {
      this.name = name;
      this.tags = tags;

      this.comments = new ArrayList<>();
        for (Long comment : comments) {
            this.comments.add(new CommentId(comment));
        }
    }

    public String getName() {
        return name;
    }

    public Set<String> getTags() {
        return tags;
    }

    public List<CommentId> getCommentsIds() {
        return comments;
    }
}
