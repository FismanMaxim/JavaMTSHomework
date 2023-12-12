package org.example.CustomExceptions.CommentsExceptions;

public class CommentIdNotFoundException extends Exception {
    public CommentIdNotFoundException() {
        super();
    }
    public CommentIdNotFoundException(String message) {
        super(message);
    }
    public CommentIdNotFoundException(String message, Exception innerException) {
        super(message, innerException);
    }
}
