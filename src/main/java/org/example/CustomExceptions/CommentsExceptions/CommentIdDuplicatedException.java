package org.example.CustomExceptions.CommentsExceptions;

public class CommentIdDuplicatedException extends Exception {
    public CommentIdDuplicatedException() {
        super();
    }
    public CommentIdDuplicatedException(String message) {
        super(message);
    }
    public CommentIdDuplicatedException(String message, Exception innerException) {
        super(message, innerException);
    }
}
