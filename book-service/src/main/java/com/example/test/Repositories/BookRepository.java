package com.example.test.Repositories;

import com.example.test.Models.Book;
import com.example.test.Models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findBookByTagsContains(Tag tag);
}