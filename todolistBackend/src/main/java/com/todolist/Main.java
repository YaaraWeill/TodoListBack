package com.todolist;

import io.vertx.core.Vertx;

public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MainVerticle(), ar -> {
            if (ar.succeeded()) {
                System.out.println("Verticle deployed successfully");
            } else {
                System.out.println("Failed to deploy verticle: " + ar.cause().getMessage());
            }
        });
    }
} 