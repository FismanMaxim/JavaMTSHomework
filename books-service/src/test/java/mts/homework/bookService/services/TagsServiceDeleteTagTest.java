package mts.homework.bookService.services;

import mts.homework.bookService.DatabaseSuite;
import mts.homework.bookService.data.entities.Tag;
import mts.homework.bookService.data.repositories.jpa.JpaTagsRepository;
import mts.homework.bookService.exceptions.TagAlreadyExistsException;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import(TagsService.class)
public class TagsServiceDeleteTagTest extends DatabaseSuite {
  @Autowired private JpaTagsRepository tagsRepository;

  @Autowired private TagsService tagsService;

  private Tag testTag;

  @BeforeEach
  public void setUp() {
    tagsRepository.deleteAll();
    testTag = tagsRepository.save(new Tag("Test"));
  }

  @Test
  public void testDelete() {
    boolean isSuccess = tagsService.deleteTag(testTag.getId());

    assertTrue(isSuccess);
    assertTrue(tagsRepository.findAll().isEmpty());
  }

  @Test
  public void testSimpleFind() {
    Optional<Tag> result = tagsService.findTag(testTag.getId());

    Assertions.assertTrue(result.isPresent());
    Assertions.assertEquals(testTag.getId(), result.get().getId());
  }

  @Test
  public void testCreateTag() throws TagAlreadyExistsException {
    Tag tag = tagsService.createNew("Tag");

    assertEquals(2, tagsRepository.findAll().size());
    assertEquals(tagsRepository.findAll().get(1).getId(), tag.getId());
  }

  @Test
  public void testCreateTagWhichAlreadyExists() {
    tagsRepository.save(new Tag("Tag"));

    assertThrows(TagAlreadyExistsException.class, () -> tagsService.createNew("Tag"));
  }

  @Test
  public void testRename() throws TagAlreadyExistsException {
    Optional<Tag> target = tagsService.rename(testTag.getId(), "Renamed");

    assertTrue(target.isPresent());
    assertEquals(testTag.getId(), target.get().getId());
    assertEquals("Renamed", target.get().getName());
  }

  @Test
  public void testRenameAlreadyExists() {
    var anotherTag = new Tag("Another tag");
    tagsRepository.save(anotherTag);

    assertThrows(
        TagAlreadyExistsException.class, () -> tagsService.rename(anotherTag.getId(), "Test"));
  }
}
