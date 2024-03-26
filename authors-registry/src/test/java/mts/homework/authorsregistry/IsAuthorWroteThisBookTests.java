package mts.homework.authorsregistry;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import mts.homework.authorsregistry.controllers.reponses.isAuthorOfBook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IsAuthorWroteThisBookTests {
  @Autowired private TestRestTemplate http;

  @Test
  public void testIsWrote() {
    var result =
        http.getForEntity(
            "/api/authors-registry/is-wrote-this-book?firstName={firstName}&lastName={lastName}&bookName={bookName}",
            isAuthorOfBook.class,
            Map.of("firstName", "Уильям", "lastName", "Шекспир", "bookName", "Ромео и Джульетта"));

    assertTrue(result.getStatusCode().is2xxSuccessful());
    assertTrue(result.hasBody());
    assertNotNull(result.getBody());
    assertTrue(result.getBody().isWrote());
  }

  @Test
  public void testIsNotWrote() {
    var result =
        http.getForEntity(
            "/api/authors-registry/is-wrote-this-book?firstName={firstName}&lastName={lastName}&bookName={bookName}",
            isAuthorOfBook.class,
            Map.of(
                "firstName",
                "Уильям",
                "lastName",
                "Шекспир",
                "bookName",
                "Ромео и Джульетта - NON EXISTING"));

    assertTrue(result.getStatusCode().is2xxSuccessful());
    assertTrue(result.hasBody());
    assertNotNull(result.getBody());
    assertFalse(result.getBody().isWrote());
  }
}
