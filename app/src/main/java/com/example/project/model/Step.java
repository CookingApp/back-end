package com.example.project.model;

import java.io.Serializable;

public class Step implements Serializable {

    private String description;

    public Step() {
    }

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
