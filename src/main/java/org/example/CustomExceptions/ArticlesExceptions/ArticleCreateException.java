package org.example.CustomExceptions.ArticlesExceptions;

public class ArticleCreateException extends RuntimeException {
    public ArticleCreateException() {
        super();
    }
    public ArticleCreateException(String message) {
        super(message);
    }
    public ArticleCreateException(String message, Exception innerException) {
        super(message, innerException);
    }
}
