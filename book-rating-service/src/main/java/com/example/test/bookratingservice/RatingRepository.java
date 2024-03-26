package com.example.test.bookratingservice;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Repository
public class RatingRepository {
  private Random random = new Random();
  private Map<Long, Integer> ratingsOfBooks = new HashMap<>();

  public int calculateBookRating(long bookId) {
    if (!ratingsOfBooks.containsKey(bookId))
      ratingsOfBooks.put(bookId, random.nextInt(1, 101));
    return ratingsOfBooks.get(bookId);
  }

  public int countSavedRatings() {
    return ratingsOfBooks.size();
  }
}
