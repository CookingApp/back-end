package com.example.project.model;

import java.io.Serializable;

public class Ingredient implements Serializable {
    private String ingredient;
    private String quantity;
    private String measure;

    public Ingredient() {
    }

    public Ingredient(String ingredient, String quantity, String measure) {
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }
}
