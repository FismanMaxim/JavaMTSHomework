package mts.homework.bookService.services;

import java.util.Optional;
import mts.homework.bookService.data.entities.Tag;
import mts.homework.bookService.data.repositories.jpa.JpaTagsRepository;
import mts.homework.bookService.exceptions.TagAlreadyExistsException;
import org.springframework.stereotype.Service;

@Service
public class TagsService {

  private final JpaTagsRepository jpaTagsRepository;

  public TagsService(JpaTagsRepository jpaTagsRepository) {
    this.jpaTagsRepository = jpaTagsRepository;
  }

  public Optional<Tag> rename(long id, String newName) throws TagAlreadyExistsException {
    var tagWithSameNameOpt = jpaTagsRepository.findByName(newName);

    if (tagWithSameNameOpt.isPresent() && tagWithSameNameOpt.get().getId() != id) {
      throw new TagAlreadyExistsException();
    }

    var target = jpaTagsRepository.findById(id);

    if (target.isEmpty()) return Optional.empty();

    var targetTag = target.get();

    targetTag.setName(newName);
    return target;
  }

  public Tag createNew(String tagName) throws TagAlreadyExistsException {
    if (jpaTagsRepository.findByName(tagName).isPresent()) {
      throw new TagAlreadyExistsException();
    }

    return jpaTagsRepository.save(new Tag(tagName));
  }

  public Optional<Tag> findTag(Long id) {
    return jpaTagsRepository.findById(id);
  }

  public boolean deleteTag(long id) {
    jpaTagsRepository.deleteById(id);

    return !jpaTagsRepository.existsById(id);
  }
}
