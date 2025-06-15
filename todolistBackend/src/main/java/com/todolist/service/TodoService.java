package com.todolist.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todolist.model.Todo;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TodoService {
    private static final Logger logger = LoggerFactory.getLogger(TodoService.class);
    private static final String DATA_FILE = "todos.json";
    private final ObjectMapper objectMapper;
    private List<Todo> todos;

    public TodoService() {
        this.objectMapper = new ObjectMapper();
        this.todos = new ArrayList<>();
        loadTodos();
    }

    private void loadTodos() {
        try {
            File file = new File(DATA_FILE);
            if (file.exists()) {
                todos = objectMapper.readValue(file, new TypeReference<List<Todo>>() {});
                logger.info("Loaded {} todos from file", todos.size());
            } else {
                todos = new ArrayList<>();
                logger.info("No existing todos file found, starting with empty list");
            }
        } catch (IOException e) {
            logger.error("Error loading todos from file", e);
            todos = new ArrayList<>();
        }
    }

    private void saveTodos() {
        try {
            objectMapper.writeValue(new File(DATA_FILE), todos);
            logger.debug("Saved {} todos to file", todos.size());
        } catch (IOException e) {
            logger.error("Error saving todos to file", e);
        }
    }

    public Future<List<Todo>> getAllTodos() {
        logger.debug("Getting all todos");
        return Future.succeededFuture(new ArrayList<>(todos));
    }

    public Future<Todo> getTodoById(String id) {
        logger.debug("Getting todo with id: {}", id);
        Optional<Todo> todo = todos.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
        return todo.map(Future::succeededFuture)
                .orElseGet(() -> Future.failedFuture("Todo not found"));
    }

    public Future<Todo> createTodo(Todo todo) {
        logger.debug("Creating new todo: {}", todo);
        todos.add(todo);
        saveTodos();
        return Future.succeededFuture(todo);
    }

    public Future<Todo> updateTodo(String id, JsonObject updates) {
        logger.debug("Updating todo with id: {} and updates: {}", id, updates);
        return getTodoById(id)
                .map(todo -> {
                    if (updates.containsKey("title")) {
                        todo.setTitle(updates.getString("title"));
                    }
                    if (updates.containsKey("description")) {
                        todo.setDescription(updates.getString("description"));
                    }
                    if (updates.containsKey("completed")) {
                        todo.setCompleted(updates.getBoolean("completed"));
                    }
                    saveTodos();
                    return todo;
                });
    }

    public Future<Void> deleteTodo(String id) {
        logger.debug("Deleting todo with id: {}", id);
        boolean removed = todos.removeIf(todo -> todo.getId().equals(id));
        if (removed) {
            saveTodos();
            return Future.succeededFuture();
        }
        return Future.failedFuture("Todo not found");
    }
} 