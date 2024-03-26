package mts.homework.bookratingservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class RatingService {
  private final ObjectMapper objectMapper;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final String responseTopic;
  private final RatingRepository ratingRepository;

  @Autowired
  public RatingService(
      ObjectMapper objectMapper,
      KafkaTemplate<String, String> kafkaTemplate,
      @Value("${topic-to-send-get-rating-response}") String responseTopic,
      RatingRepository ratingRepository) {
    this.objectMapper = objectMapper;
    this.kafkaTemplate = kafkaTemplate;
    this.responseTopic = responseTopic;
    this.ratingRepository = ratingRepository;
  }

  record CalculateBookRatingRequest(long bookId) {}

  record CalculateBookRatingResponse(long bookId, long rating) {}

  @KafkaListener(topics = {"${topic-to-get-get-rating-request}"})
  public void calculateBookRating(String message) throws JsonProcessingException {
    CalculateBookRatingRequest parsedMessage =
        objectMapper.readValue(message, CalculateBookRatingRequest.class);
    long bookId = parsedMessage.bookId();
    int rating = ratingRepository.calculateBookRating(bookId);

    sendGetRatingResponse(bookId, rating);
  }

  public void sendGetRatingResponse(long bookId, int rating) throws JsonProcessingException {
    CalculateBookRatingResponse response = new CalculateBookRatingResponse(bookId, rating);
    String responseMessage = objectMapper.writeValueAsString(response);
    kafkaTemplate.send(responseTopic, responseMessage);
  }
}
