package mts.homework.authorsregistry.controllers;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import mts.homework.authorsregistry.Pair;
import mts.homework.authorsregistry.controllers.reponses.isAuthorOfBook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorsRegistryController {
  private final Set<Pair<String, String>> authorsBooks =
      Set.of(
          new Pair<>("Уилльям Шекспир", "Ромео и Джульетта"),
          new Pair<>("Лев Толстой", "Война и мир"));

  private final HashSet<String> requests = new HashSet<>();

  @GetMapping("/api/authors-registry/is-wrote-this-book")
  public ResponseEntity<isAuthorOfBook> isWrote(
      @RequestParam("firstName") String firstName,
      @RequestParam("lastName") String lastName,
      @RequestParam("bookName") String bookName,
      @RequestHeader("X-REQUEST-ID") Optional<String> uuid) {
    if (uuid.isPresent()) {
      if (requests.contains(uuid.get())) {
        requests.add(uuid.get());
        return ResponseEntity.ok(new isAuthorOfBook(false));
      }

      requests.add(uuid.get());
    }

    return ResponseEntity.ok(
        new isAuthorOfBook(
            authorsBooks.contains(new Pair<>(firstName + " " + lastName, bookName))));
  }
}
