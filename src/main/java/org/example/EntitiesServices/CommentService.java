package org.example.EntitiesServices;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.CustomExceptions.ArticlesExceptions.ArticleDuplicatedException;
import org.example.CustomExceptions.ArticlesExceptions.ArticleIdNotFoundException;
import org.example.CustomExceptions.CommentsExceptions.*;
import org.example.Entities.Comment;
import org.example.Entities.EntitiesIds.ArticleId;
import org.example.Entities.EntitiesIds.CommentId;
import org.example.EntitiesRepositories.ArticleRepository;
import org.example.EntitiesRepositories.CommentRepository;

public class CommentService {
    private final CommentRepository commentsRepository;
    private final ArticleRepository articlesRepository;

    public CommentService(CommentRepository commentsRepository, ArticleRepository articlesRepotisory) {
        this.commentsRepository = commentsRepository;
        this.articlesRepository = articlesRepotisory;
    }

    public synchronized CommentId createComment(ArticleId articleId, String content) {
        CommentId commentId = commentsRepository.generateId();
        Comment comment = new Comment(commentId, articleId, content);

        try {
            commentsRepository.createComment(comment);
            articlesRepository.addCommentToArticle(articleId, comment);
        } catch (CommentIdDuplicatedException e) {
            throw new CommentCreateException("Comment with given id already exists: " + commentId, e);
        } catch (ArticleIdNotFoundException e) {
            throw new CommentCreateException("Cannot find article with given id: " + articleId, e);
        } catch (ArticleDuplicatedException | JsonProcessingException e) {
            throw new CommentCreateException("Cannot find article: " + articleId, e);
        }

        return commentId;
    }

    public Comment getCommentById(CommentId commentId) {
        try {
            return commentsRepository.getCommentById(commentId);
        } catch (CommentIdNotFoundException e) {
            throw new CommentFindException("Cannot find comment with given id: " + commentId, e);
        }
    }

    public synchronized void deleteComment(CommentId commentId) {
        try {
            Comment comment = commentsRepository.getCommentById(commentId);
            commentsRepository.deleteComment(commentId);
            articlesRepository.removeCommentFromArticle(comment.articleId(), commentId);
        } catch (CommentIdNotFoundException | ArticleIdNotFoundException e) {
            throw new CommentDeleteException("Cannot delete comment with given id: " + commentId, e);
        } catch (ArticleDuplicatedException | JsonProcessingException e) {
            throw new CommentDeleteException();
        }
    }
}
