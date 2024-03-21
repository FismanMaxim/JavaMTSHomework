package mts.homework.bookService.controllers.tags;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import mts.homework.bookService.DatabaseSuite;
import mts.homework.bookService.TestHelper;
import mts.homework.bookService.controllers.responses.TagApiEntity;
import mts.homework.bookService.data.entities.Tag;
import mts.homework.bookService.data.repositories.jpa.JpaTagsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class TagsControllerFindTagTest extends DatabaseSuite {
  @Autowired private JpaTagsRepository jpaTagsRepository;

  @Autowired private TestRestTemplate http;

  private Tag testTag;

  @BeforeEach
  public void setUp() {
    jpaTagsRepository.deleteAll();
    testTag = jpaTagsRepository.save(new Tag("Test Tag"));
  }

  @Test
  public void testTagFind() {
    var responseResult =
        http.getForEntity("/api/tags/{id}", TagApiEntity.class, Map.of("id", testTag.getId()));

    var body = TestHelper.assert2xxAndGetBody(responseResult);

    assertEquals(testTag.getId(), body.id());
    assertEquals(testTag.getName(), body.name());
  }

  @Test
  public void testTagFindNotFound() {
    var responseResult =
        http.getForEntity("/api/tags/{id}", TagApiEntity.class, Map.of("id", testTag.getId() + 1));

    assertTrue(responseResult.getStatusCode().is4xxClientError());
  }
}
