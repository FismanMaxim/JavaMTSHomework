package com.example.test.authorregistryservice.Requests;

import com.example.test.authorregistryservice.FullName;

public record AuthorRegistryRequest(FullName authorName, String bookName) {}
