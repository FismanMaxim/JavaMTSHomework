package org.example.CustomExceptions.ArticlesExceptions;

public class ArticleDuplicatedException extends Exception {
    public ArticleDuplicatedException() {
        super();
    }
    public ArticleDuplicatedException(String message) {
        super(message);
    }
    public ArticleDuplicatedException(String message, Exception innerException) {
        super(message, innerException);
    }
}
