CREATE TABLE comment (
    id BIGSERIAL PRIMARY KEY,
    articleId INTEGER,
    content TEXT
);