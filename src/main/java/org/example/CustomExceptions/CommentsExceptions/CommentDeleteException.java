package org.example.CustomExceptions.CommentsExceptions;

public class CommentDeleteException extends RuntimeException {
    public CommentDeleteException() {
        super();
    }
    public CommentDeleteException(String message) {
        super(message);
    }
    public CommentDeleteException(String message, Exception innerException) {
        super(message, innerException);
    }
}
