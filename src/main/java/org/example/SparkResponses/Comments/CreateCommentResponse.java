package org.example.SparkResponses.Comments;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.Entities.EntitiesIds.CommentId;

public record CreateCommentResponse (CommentId commentId) {
    @JsonCreator
    public CreateCommentResponse(@JsonProperty CommentId commentId) {
        this.commentId = commentId;
    }
}
