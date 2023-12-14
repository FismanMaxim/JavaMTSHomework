package org.example.Entities.EntitiesIds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;


public class ArticleId {
    private final AtomicLong id;

    public ArticleId(AtomicLong id) {
        this.id = id;
    }

    @JsonCreator
    public ArticleId(
            @JsonProperty("id") long id) {
        this.id = new AtomicLong(id);
    }

    public long getId() {
        return id.get();
    }

    @JsonIgnore
    public synchronized ArticleId getAndIncrement() {

        ArticleId value = new ArticleId(id.get());

        id.getAndIncrement();

        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArticleId articleId = (ArticleId) o;

        return id.get()  == articleId.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id.get());
    }

    @Override
    public String toString() {
        return "ArticleId{" +
                "id=" + id +
                '}';
    }
}
