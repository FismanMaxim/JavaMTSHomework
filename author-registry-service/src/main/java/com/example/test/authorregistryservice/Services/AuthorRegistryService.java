package com.example.test.authorregistryservice.Services;

import com.example.test.authorregistryservice.FullName;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthorRegistryService {
  private final Map<FullName, Set<String>> authorBookRegistry = new HashMap<>();

  public void addRegistry(String firstName, String lastName, String bookName) {
    FullName authorName = new FullName(firstName, lastName);
    if (!authorBookRegistry.containsKey(authorName)) authorBookRegistry.put(authorName, new HashSet<>());
    authorBookRegistry.get(authorName).add(bookName);
  }

  public boolean isAuthor(FullName authorName, String bookName) {
    return authorBookRegistry.containsKey(authorName)
        && authorBookRegistry.get(authorName).contains(bookName);
  }
}
