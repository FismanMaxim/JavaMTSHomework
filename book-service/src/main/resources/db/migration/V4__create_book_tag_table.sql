CREATE TABLE book_tag (
    book_id BIGSERIAL NOT NULL,
    tag_id BIGSERIAL NOT NULL,
    PRIMARY KEY (book_id, tag_id),
    FOREIGN KEY (book_id) REFERENCES Book(id),
    FOREIGN KEY (tag_id) REFERENCES Tag(id)
);