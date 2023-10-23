package org.example;

import java.util.Map;

public class Message {
  private Map<String, String> content;

  private EnrichmentType enrichmentType;

  public Message(Map<String, String> content, EnrichmentType enrichmentType) {
    this.content = content;
    this.enrichmentType = enrichmentType;
  }

  public enum EnrichmentType {
    MSISDN, EMAIL
  }

  public Map<String, String> getContent() {
    return content;
  }

  public EnrichmentType getEnrichmentType() {
    return enrichmentType;
  }
}
