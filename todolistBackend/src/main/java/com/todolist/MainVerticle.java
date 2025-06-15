package com.todolist;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todolist.model.Todo;
import com.todolist.service.TodoService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class MainVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);
    private final TodoService todoService = new TodoService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void start(Promise<Void> startPromise) {
        logger.info("Starting TodoList application...");
        Router router = Router.router(vertx);

        // Enable CORS
        router.route().handler(CorsHandler.create("*")
                .allowedMethod(io.vertx.core.http.HttpMethod.GET)
                .allowedMethod(io.vertx.core.http.HttpMethod.POST)
                .allowedMethod(io.vertx.core.http.HttpMethod.PUT)
                .allowedMethod(io.vertx.core.http.HttpMethod.DELETE)
                .allowedHeader("Content-Type"));

        // Parse request body
        router.route().handler(BodyHandler.create());

        // Routes
        router.get("/todos").handler(this::getAllTodos);
        router.get("/todos/:id").handler(this::getTodoById);
        router.post("/todos").handler(this::createTodo);
        router.put("/todos/:id").handler(this::updateTodo);
        router.delete("/todos/:id").handler(this::deleteTodo);

        // Create HTTP server
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8080, http -> {
                    if (http.succeeded()) {
                        startPromise.complete();
                        logger.info("HTTP server started on port 8080");
                    } else {
                        logger.error("Failed to start HTTP server", http.cause());
                        startPromise.fail(http.cause());
                    }
                });
    }

    private void getAllTodos(RoutingContext ctx) {
        logger.info("Received request to get all todos");
        todoService.getAllTodos()
            .onSuccess(todos -> {
                try {
                    String json = objectMapper.writeValueAsString(todos);
                    logger.info("Successfully retrieved {} todos", todos.size());
                    createHttpResponse(ctx, json, 200);
                } catch (JsonProcessingException e) {
                    logger.error("Error serializing todos", e);
                    handleError(ctx, e);
                }
            })
            .onFailure(err -> {
                logger.error("Failed to get todos", err);
                handleError(ctx, err);
            });
    }

    private void getTodoById(RoutingContext ctx) {
        String id = ctx.pathParam("id");
        logger.info("Received request to get todo with id: {}", id);
        todoService.getTodoById(id)
            .onSuccess(todo -> {
                try {
                    String json = objectMapper.writeValueAsString(todo);
                    logger.info("Successfully retrieved todo with id: {}", id);
                    createHttpResponse(ctx, json, 200);
                } catch (JsonProcessingException e) {
                    logger.error("Error serializing todo", e);
                    handleError(ctx, e);
                }
            })
            .onFailure(err -> {
                logger.warn("Todo not found with id: {}", id);
                ctx.response().setStatusCode(404).end();
            });
    }

    private void createTodo(RoutingContext ctx) {
        try {
            String body = ctx.body().asString();
            logger.info("Received request to create todo: {}", body);
            Todo todo = objectMapper.readValue(body, Todo.class);
            todoService.createTodo(todo)
                .onSuccess(createdTodo -> {
                    try {
                        String json = objectMapper.writeValueAsString(createdTodo);
                        logger.info("Successfully created todo with id: {}", createdTodo.getId());
                        createHttpResponse(ctx, json, 201);
                    } catch (JsonProcessingException e) {
                        logger.error("Error serializing created todo", e);
                        handleError(ctx, e);
                    }
                })
                .onFailure(err -> {
                    logger.error("Failed to create todo", err);
                    handleError(ctx, err);
                });
        } catch (JsonProcessingException e) {
            logger.error("Error parsing todo request body", e);
            handleError(ctx, e);
        }
    }

    private void updateTodo(RoutingContext ctx) {
        String id = ctx.pathParam("id");
        JsonObject body = ctx.body().asJsonObject();
        logger.info("Received request to update todo with id: {} and body: {}", id, body);
        todoService.updateTodo(id, body)
            .onSuccess(updatedTodo -> {
                try {
                    String json = objectMapper.writeValueAsString(updatedTodo);
                    logger.info("Successfully updated todo with id: {}", id);
                    createHttpResponse(ctx, json, 200);
                } catch (JsonProcessingException e) {
                    logger.error("Error serializing updated todo", e);
                    handleError(ctx, e);
                }
            })
            .onFailure(err -> {
                logger.warn("Todo not found for update with id: {}", id);
                ctx.response().setStatusCode(404).end();
            });
    }

    private void deleteTodo(RoutingContext ctx) {
        String id = ctx.pathParam("id");
        logger.info("Received request to delete todo with id: {}", id);
        todoService.deleteTodo(id)
            .onSuccess(v -> {
                logger.info("Successfully deleted todo with id: {}", id);
                ctx.response().setStatusCode(204).end();
            })
            .onFailure(err -> {
                logger.warn("Todo not found for deletion with id: {}", id);
                ctx.response().setStatusCode(404).end();
            });
    }

    private void createHttpResponse(RoutingContext ctx, String response, int statusCode) {
        ctx.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(statusCode)
            .end(response);
    }

    private void handleError(RoutingContext ctx, Throwable e) {
        logger.error("Error processing request", e);
        ctx.response()
            .setStatusCode(500)
            .putHeader("content-type", "application/json")
            .end("{\"error\":\"" + e.getMessage() + "\"}");
    }
} 