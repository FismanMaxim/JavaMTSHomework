package org.example.EntitiesRepositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.CustomExceptions.ArticlesExceptions.ArticleIdNotFoundException;
import org.example.CustomExceptions.CommentsExceptions.CommentIdDuplicatedException;
import org.example.CustomExceptions.CommentsExceptions.CommentIdNotFoundException;
import org.example.Entities.Comment;
import org.example.Entities.EntitiesIds.CommentId;

public interface CommentRepository {
    CommentId generateId();

    /**
     * @throws CommentIdDuplicatedException
     */

    void createComment(Comment comment) throws CommentIdDuplicatedException, JsonProcessingException;

    /**
     *
     * @throws CommentIdNotFoundException if there is no comment with given id
     */
    Comment getCommentById(CommentId commentId, boolean forUpdate) throws CommentIdNotFoundException;

    /**
     * gets comment by id with forUpdate set to false
     */
    Comment getCommentById(CommentId commentId) throws CommentIdNotFoundException;

    /**
     *
     * @throws CommentIdNotFoundException if there is no comment with given id
     * @throws ArticleIdNotFoundException if there is no article with the idea that the given comment corresponds to
     */
    void deleteComment(CommentId commentId) throws CommentIdNotFoundException, ArticleIdNotFoundException;
}
