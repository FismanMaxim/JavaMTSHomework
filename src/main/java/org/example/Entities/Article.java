package org.example.Entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.CustomExceptions.CommentsExceptions.CommentIdDuplicatedException;
import org.example.Entities.EntitiesIds.ArticleId;
import org.example.Entities.EntitiesIds.CommentId;

import java.util.*;

public record Article(ArticleId id, String name, Set<String> tags, List<Comment> comments) {
    @JsonCreator
    public Article(
            @JsonProperty("id") ArticleId id,
            @JsonProperty("name") String name,
            @JsonProperty("tags") Set<String> tags,
            @JsonProperty("comments") List<Comment> comments) {
        this.id = id;
        this.name = name;
        this.tags = tags;
        this.comments = comments;
    }

    public Article withName(String name) {
        return new Article(id, name, new HashSet<>(tags), new ArrayList<>(comments));
    }

    public Article WithTags(Set<String> tags) {
        return new Article(id, name, tags, new ArrayList<>(comments));
    }

    public Article WithComments(List<Comment> comments) {
        return new Article(id, name, new HashSet<>(tags), comments);
    }

    public Article withNewComment(Comment comment) throws CommentIdDuplicatedException {
        if (comments.stream().anyMatch(com -> com.id() == comment.id()))
            throw new CommentIdDuplicatedException("Comment with given id already exists: " + comment.id());

        List<Comment> newComments = new ArrayList<>(comments);
        newComments.add(comment);
        return new Article(id, name, new HashSet<>(tags), newComments);
    }

    public Article withoutComment(CommentId commentId) {
        List<Comment> newComments = new ArrayList<>(comments);
        if (!newComments.removeIf(comment -> comment.id().equals(commentId)))
            throw new IllegalArgumentException("Comment to be removed is not in the list of comments");
        return new Article(id, name, new HashSet<>(tags), newComments);
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tags=" + tags +
                ", comments=" + comments +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return id.equals(article.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
