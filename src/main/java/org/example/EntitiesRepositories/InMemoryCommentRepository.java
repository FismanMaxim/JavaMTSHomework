package org.example.EntitiesRepositories;

import org.example.CustomExceptions.CommentsExceptions.CommentIdDuplicatedException;
import org.example.CustomExceptions.CommentsExceptions.CommentIdNotFoundException;
import org.example.Entities.Comment;
import org.example.Entities.EntitiesIds.CommentId;

import java.util.HashMap;
import java.util.Map;

public class InMemoryCommentRepository implements CommentRepository {
    public CommentId nextId = new CommentId(0);
    public Map<CommentId, Comment> comments = new HashMap<>();

    @Override
    public CommentId generateId() {
        return nextId.getAndIncrement();
    }

    @Override
    public void createComment(Comment comment) throws CommentIdDuplicatedException {
        if (comments.get(comment.id()) != null)
            throw new CommentIdDuplicatedException("Comment with given id already exists: " + comment.id());
        comments.put(comment.id(), comment);
    }

    @Override
    public Comment getCommentById(CommentId commentId, boolean forUpdate) throws CommentIdNotFoundException {
        Comment comment = comments.get(commentId);
        if (comment == null)
            throw new CommentIdNotFoundException("Comment with given id not found: " + commentId);
        return comment;
    }

    @Override
    public Comment getCommentById(CommentId commentId) throws CommentIdNotFoundException {
        return getCommentById(commentId, false);
    }

    @Override
    public void deleteComment(CommentId commentId) throws CommentIdNotFoundException {
        if (comments.get(commentId) == null)
            throw new CommentIdNotFoundException("Comment with given id not found: " + commentId);
        comments.remove(commentId);
    }
}
