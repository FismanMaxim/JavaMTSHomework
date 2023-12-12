package org.example.CustomExceptions.ArticlesExceptions;

public class UpdateArticleException extends RuntimeException {
    public UpdateArticleException() {
        super();
    }
    public UpdateArticleException(String message) {
        super(message);
    }
    public UpdateArticleException(String message, Exception innerException) {
        super(message, innerException);
    }
}
