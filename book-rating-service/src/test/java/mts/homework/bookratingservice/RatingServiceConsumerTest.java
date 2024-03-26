//package com.example.test.bookratingservice;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Import;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.testcontainers.containers.KafkaContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.testcontainers.utility.DockerImageName;
//
//import java.time.Duration;
//
//import static org.awaitility.Awaitility.await;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.times;
//
//@SpringBootTest(
//    classes = {RatingService.class},
//    properties = {
////        "topic-to-get-get-rating-request=get-rating-request-test",
////        "topic-to-send-get-rating-response=get-rating-response-test"
//    })
//@Import({KafkaAutoConfiguration.class, RatingRepository.class, RatingServiceConsumerTest.ObjectMapperTestConfig.class})
//@Testcontainers
//public class RatingServiceConsumerTest {
//  @TestConfiguration
//  static class ObjectMapperTestConfig {
//    @Bean
//    public ObjectMapper objectMapper() {
//      return new ObjectMapper();
//    }
//  }
//
//  @Container
//  @ServiceConnection
//  public static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));
//
//  @MockBean
//  private RatingRepository ratingRepository;
//  @Autowired
//  private KafkaTemplate<String, String> kafkaTemplate;
//  @Autowired
//  private ObjectMapper objectMapper;
//
//  @Test
//  void shouldReceiveMessageToGetBookRating() throws JsonProcessingException {
//    String messageToReceive = objectMapper.writeValueAsString(new RatingService.CalculateBookRatingRequest(56L));
//    kafkaTemplate.send("get-rating-request", messageToReceive);
//
//    await().atMost(Duration.ofSeconds(5))
//        .pollDelay(Duration.ofSeconds(1))
//        .untilAsserted(() ->  Mockito.verify(
//                ratingRepository, times(1))
//            .calculateBookRating(anyLong())
//        );
//  }
//}
