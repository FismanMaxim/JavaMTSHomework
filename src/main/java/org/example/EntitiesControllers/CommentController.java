package org.example.EntitiesControllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.CustomExceptions.CommentsExceptions.CommentCreateException;
import org.example.CustomExceptions.CommentsExceptions.CommentDeleteException;
import org.example.CustomExceptions.CommentsExceptions.CommentFindException;
import org.example.Entities.Comment;
import org.example.Entities.EntitiesIds.CommentId;
import org.example.EntitiesServices.CommentService;
import org.example.SparkRequests.Comments.CreateCommentRequest;
import org.example.SparkResponses.Comments.CreateCommentResponse;
import org.example.SparkResponses.Comments.FindCommentResponse;
import org.example.SparkResponses.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

public class CommentController implements EntityController {
    private static final Logger LOG = LoggerFactory.getLogger(CommentController.class);

    private final Service service;
    private final CommentService commentService;
    private final ObjectMapper objectMapper;


    public CommentController(Service service, CommentService commentService, ObjectMapper objectMapper) {
        this.service = service;
        this.commentService = commentService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void initializeEndpoints() {
        createCommentEndpoint();
        getCommentById();
        deleteCommentEndpoint();
    }

    private void createCommentEndpoint() {
        service.post("/api/comments", (Request request, Response response) -> {
            response.type("application/json");

            CreateCommentRequest createRequest = objectMapper
                    .readValue(request.body(), CreateCommentRequest.class);

            try {
                CommentId commentId = commentService.createComment(
                        createRequest.getArticleId(),
                        createRequest.getContent()
                );

                response.status(201);
                LOG.debug("Create comment query handled successfully. Created comment with id={}", commentId);
                return objectMapper.writeValueAsString(new CreateCommentResponse(commentId));
            } catch (CommentCreateException e) {
                LOG.warn("Create comment query failed", e);
                response.status(400);
                return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
            }
        });
    }

    private void getCommentById() {
        service.get("/api/comments/:commentId", (Request request, Response response) -> {
            response.type("application/java");

            CommentId commentId = new CommentId(Long.parseLong(request.params("commentId")));

            try {
                Comment comment = commentService.getCommentById(commentId);
                response.status(200);
                LOG.debug("Get comment by id query handled successfully");
                return objectMapper.writeValueAsString(new FindCommentResponse(comment));
            } catch (CommentFindException e) {
                LOG.warn("Get comment by id query failed. Cannot find comment with id={}", commentId, e);
                response.status(404);
                return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
            }
        });
    }

    private void deleteCommentEndpoint() {
        service.delete("/api/comments/:commentId", (Request request, Response response) -> {
            response.type("application/json");

            CommentId commentId = new CommentId(Long.parseLong(request.params("commentId")));

            try {
                commentService.deleteComment(commentId);
                response.status(204);
                LOG.debug("Delete comment query handled successfully. Deleted comment with id={}", commentId);
                return 0;
            } catch (CommentDeleteException e) {
                LOG.warn("Delete comment query failed. Cannot delete comment with id={}", commentId, e);
                response.status(404);
                return objectMapper.writeValueAsString(new ErrorResponse(e.getMessage()));
            }
        });
    }
}
