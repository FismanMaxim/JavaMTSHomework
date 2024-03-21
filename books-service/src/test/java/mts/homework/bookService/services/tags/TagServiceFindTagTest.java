package mts.homework.bookService.services.tags;

import java.util.Optional;
import mts.homework.bookService.DatabaseSuite;
import mts.homework.bookService.data.entities.Tag;
import mts.homework.bookService.data.repositories.jpa.JpaTagsRepository;
import mts.homework.bookService.services.TagsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import(TagsService.class)
public class TagServiceFindTagTest extends DatabaseSuite {
  @Autowired private JpaTagsRepository tagsRepository;

  @Autowired private TagsService tagsService;

  private Tag testTag;

  @BeforeEach
  public void setUp() {
    tagsRepository.deleteAll();

    testTag = new Tag("Test");
    tagsRepository.save(testTag);
  }

  @Test
  public void testSimpleFind() {
    Optional<Tag> result = tagsService.findTag(testTag.getId());

    Assertions.assertTrue(result.isPresent());
    Assertions.assertEquals(testTag.getId(), result.get().getId());
  }

  @Test
  public void testEmptyBook() {
    var result = tagsService.findTag(testTag.getId() + 1);

    Assertions.assertFalse(result.isPresent());
  }
}
