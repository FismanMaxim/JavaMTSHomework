package org.example.Entities.EntitiesIds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class CommentId {
    private final AtomicLong id;

    public CommentId(AtomicLong id) {
        this.id = id;
    }

    @JsonCreator
    public CommentId(@JsonProperty("id") long id) {
        this.id = new AtomicLong(id);
    }

    public CommentId(CommentId commentId) {
        this.id = new AtomicLong(commentId.id.get());
    }

    public long getId() {
        return id.get();
    }

    @JsonIgnore
    public synchronized CommentId getAndIncrement() {

        CommentId value = new CommentId(id.get());

        id.getAndIncrement();

        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentId commentId = (CommentId) o;
        return id.get() == commentId.id.get();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id.get());
    }

    @Override
    public String toString() {
        return "CommentId{" +
                "id=" + id +
                '}';
    }
}
