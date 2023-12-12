package org.example;

import org.example.EntitiesControllers.EntityController;

import java.util.List;

public class Application {
    private final List<EntityController> controllers;

    public Application(List<EntityController> controllers) {
        this.controllers = controllers;
    }

    public void start() {
        for (EntityController controller : controllers) {
            controller.initializeEndpoints();
        }
    }
}
