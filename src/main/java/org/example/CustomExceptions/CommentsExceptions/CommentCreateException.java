package org.example.CustomExceptions.CommentsExceptions;

public class CommentCreateException extends RuntimeException {
    public CommentCreateException() {
        super();
    }
    public CommentCreateException(String message) {
        super(message);
    }
    public CommentCreateException(String message, Exception innerException) {
        super(message, innerException);
    }
}
