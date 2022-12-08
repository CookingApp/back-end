package com.example.project.model;

public class Step {

    private String description;

    public Step(){ }

    public Step(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
