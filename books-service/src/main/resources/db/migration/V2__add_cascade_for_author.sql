ALTER TABLE books
    DROP CONSTRAINT books_author_id_fkey;

ALTER TABLE books
    ADD FOREIGN KEY (author_id) REFERENCES authors
        ON DELETE CASCADE;
