package mts.homework.bookService.controllers.tags;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import mts.homework.bookService.DatabaseSuite;
import mts.homework.bookService.TestHelper;
import mts.homework.bookService.controllers.requests.TagCreationRequest;
import mts.homework.bookService.controllers.responses.TagApiEntity;
import mts.homework.bookService.data.entities.Tag;
import mts.homework.bookService.data.repositories.jpa.JpaTagsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class TagsControllerCreateTagTest extends DatabaseSuite {
  @Autowired private JpaTagsRepository jpaTagsRepository;

  @Autowired private TestRestTemplate http;

  private Tag testTag;

  @BeforeEach
  public void setUp() {
    jpaTagsRepository.deleteAll();
    testTag = jpaTagsRepository.save(new Tag("Test Tag"));
  }

  @Test
  public void testCreateTag() {
    var creationRequest = new TagCreationRequest("Tag");
    var requestEntity = new HttpEntity<>(creationRequest);

    var result = http.postForEntity("/api/tags", requestEntity, TagApiEntity.class);
    TestHelper.assert2xxAndGetBody(result);

    assertEquals(2, jpaTagsRepository.findAll().size());
  }

  @Test
  public void testCreateTagWithNameWhichAlreadyExists() {
    var creationRequest = new TagCreationRequest(testTag.getName());
    var requestEntity = new HttpEntity<>(creationRequest);

    var result = http.postForEntity("/api/tags", requestEntity, TagApiEntity.class);
    assertTrue(result.getStatusCode().is4xxClientError());
  }
}
