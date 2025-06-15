package com.todolist.model;

import java.util.UUID;

public class Todo {
    private String id;
    private String title;
    private String description;
    private boolean completed;

    public Todo() {
        this.id = UUID.randomUUID().toString();
    }

    public Todo(String title, String description) {
        this();
        this.title = title;
        this.description = description;
        this.completed = false;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
} 