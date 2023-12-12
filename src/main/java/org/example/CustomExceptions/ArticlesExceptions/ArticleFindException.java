package org.example.CustomExceptions.ArticlesExceptions;

public class ArticleFindException extends RuntimeException {
    public ArticleFindException() {
        super();
    }
    public ArticleFindException(String message) {
        super(message);
    }
    public ArticleFindException(String message, Exception innerException) {
        super(message, innerException);
    }
}
