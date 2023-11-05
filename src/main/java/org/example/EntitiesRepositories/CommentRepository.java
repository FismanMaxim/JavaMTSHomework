package org.example.EntitiesRepositories;

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

    void createComment(Comment comment) throws CommentIdDuplicatedException;

    /**
     *
     * @throws CommentIdNotFoundException if there is no comment with given id
     * @throws ArticleIdNotFoundException if there is no article with the idea that the given comment corresponds to
     */
    Comment getCommentById(CommentId commentId) throws CommentIdNotFoundException, ArticleIdNotFoundException;

    /**
     *
     * @throws CommentIdNotFoundException if there is no comment with given id
     * @throws ArticleIdNotFoundException if there is no article with the idea that the given comment corresponds to
     */
    void deleteComment(CommentId commentId) throws CommentIdNotFoundException, ArticleIdNotFoundException;
}
