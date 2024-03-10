package com.example.test.authorregistryservice.Controllers;

import com.example.test.authorregistryservice.Requests.AuthorRegistryRequest;
import com.example.test.authorregistryservice.Services.AuthorRegistryService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/author-registry")
public class AuthorRegistryController {
  private final AuthorRegistryService authorRegistryService;

  private final Map<String, Boolean> isAuthorOfBookResponses = new ConcurrentHashMap<>();

  public AuthorRegistryController(AuthorRegistryService authorRegistryService) {
    this.authorRegistryService = authorRegistryService;
  }

  @PostMapping("")
  public void createAuthorRegistry(@RequestBody Map<String, String> request) {
    authorRegistryService.addRegistry(
        request.get("firstName"), request.get("secondName"), request.get("bookName"));
  }

  @GetMapping("/is-author")
  public boolean isAuthorOfBook(
      @RequestBody AuthorRegistryRequest request,
      @NotNull @RequestHeader("X-REQUEST-ID") String requestId) {
    return isAuthorOfBookResponses.computeIfAbsent(
        requestId, zzz -> authorRegistryService.isAuthor(request.authorName(), request.bookName()));
  }
}
