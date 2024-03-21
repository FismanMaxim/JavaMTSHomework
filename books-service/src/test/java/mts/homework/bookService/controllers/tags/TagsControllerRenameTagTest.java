package mts.homework.bookService.controllers.tags;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import mts.homework.bookService.DatabaseSuite;
import mts.homework.bookService.TestHelper;
import mts.homework.bookService.controllers.requests.TagUpdateRequest;
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
import org.springframework.http.HttpMethod;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class TagsControllerRenameTagTest extends DatabaseSuite {
  @Autowired private JpaTagsRepository tagRepository;

  @Autowired private TestRestTemplate http;

  private Tag testTag;

  @BeforeEach
  public void setUp() {
    tagRepository.deleteAll();

    testTag = tagRepository.save(new Tag("Test"));
  }

  @Test
  public void testRenameTag() {
    var updateRequest = new TagUpdateRequest("Updated Test");
    var updateRequestEntity = new HttpEntity<>(updateRequest);
    var response =
        http.exchange(
            "/api/tags/{id}",
            HttpMethod.PATCH,
            updateRequestEntity,
            TagApiEntity.class,
            Map.of("id", testTag.getId()));
    var body = TestHelper.assert2xxAndGetBody(response);

    assertEquals(testTag.getId(), body.id());
    assertEquals("Updated Test", body.name());
  }

  @Test
  public void testRenameTagNotFound() {
    var updateRequest = new TagUpdateRequest("Updated Test");
    var updateRequestEntity = new HttpEntity<>(updateRequest);
    var response =
        http.exchange(
            "/api/tags/{id}",
            HttpMethod.PATCH,
            updateRequestEntity,
            TagApiEntity.class,
            Map.of("id", testTag.getId() + 1));
    assertTrue(response.getStatusCode().is4xxClientError());
  }

  @Test
  public void testRenameTagAlreadyExistsWithThisName() {
    tagRepository.save(new Tag("Updated Test"));

    var updateRequest = new TagUpdateRequest("Updated Test");
    var updateRequestEntity = new HttpEntity<>(updateRequest);
    var response =
        http.exchange(
            "/api/tags/{id}",
            HttpMethod.PATCH,
            updateRequestEntity,
            TagApiEntity.class,
            Map.of("id", testTag.getId()));
    assertTrue(response.getStatusCode().is4xxClientError());
  }
}
