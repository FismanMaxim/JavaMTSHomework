package org.example.CustomExceptions.ArticlesExceptions;

public class ArticleIdNotFoundException extends Exception {
    public ArticleIdNotFoundException() {
        super();
    }
    public ArticleIdNotFoundException(String message) {
        super(message);
    }
    public ArticleIdNotFoundException(String message, Exception innerException) {
        super(message, innerException);
    }
}
