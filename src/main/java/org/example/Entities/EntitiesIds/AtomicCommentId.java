package org.example.Entities.EntitiesIds;

public class AtomicCommentId {
    private CommentId commentId;

    public AtomicCommentId(CommentId articleId) {
        this.commentId = articleId;
    }

    public synchronized CommentId getAndIncrement() {
        CommentId value = new CommentId(commentId);

        commentId = new CommentId(commentId.getId().incrementAndGet());

        return value;
    }
}
