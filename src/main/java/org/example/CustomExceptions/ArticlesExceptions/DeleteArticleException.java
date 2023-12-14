package org.example.CustomExceptions.ArticlesExceptions;

public class DeleteArticleException extends RuntimeException {
    public DeleteArticleException() {
        super();
    }
    public DeleteArticleException(String message) {
        super(message);
    }
    public DeleteArticleException(String message, Exception innerException) {
        super(message, innerException);
    }
}
