package org.example.Entities.EntitiesIds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;


public class ArticleId {
    private long id;

    @JsonCreator
    public ArticleId(
            @JsonProperty("id") long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @JsonIgnore
    public ArticleId getAndIncrement() {

        ArticleId value = new ArticleId(id);

        id++;

        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArticleId articleId = (ArticleId) o;

        return id == articleId.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ArticleId{" +
                "id=" + id +
                '}';
    }
}
