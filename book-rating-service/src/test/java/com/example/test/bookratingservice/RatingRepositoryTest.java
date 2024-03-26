package com.example.test.bookratingservice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RatingRepositoryTest {
  @Test
  void testCalculateBookRating() {
    RatingRepository ratingRepository = new RatingRepository();

    assertEquals(0, ratingRepository.countSavedRatings());
    final int countTestBooks = 10;
    for (int i = 0; i < countTestBooks; i++) {
      long savedRating = ratingRepository.calculateBookRating(i);
      assertEquals(savedRating, ratingRepository.calculateBookRating(i));
    }
    assertEquals(10, ratingRepository.countSavedRatings());

  }
}
