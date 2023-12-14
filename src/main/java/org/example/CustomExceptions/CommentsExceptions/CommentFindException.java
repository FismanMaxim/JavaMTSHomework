package org.example.CustomExceptions.CommentsExceptions;

public class CommentFindException extends RuntimeException {
    public CommentFindException() {
        super();
    }
    public CommentFindException(String message) {
        super(message);
    }
    public CommentFindException(String message, Exception innerException) {
        super(message, innerException);
    }
}
