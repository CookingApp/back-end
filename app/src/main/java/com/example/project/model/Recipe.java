package com.example.project.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Serializable {
    private String name;
    private ArrayList<Ingredient> ingredientList;
    private ArrayList<Step> stepList;

    public Recipe() {
    }

    public Recipe(String name, ArrayList<Ingredient> ingredientList, ArrayList<Step> stepList) {
        this.name = name;
        this.ingredientList = ingredientList;
        this.stepList = stepList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Ingredient> getIngredientList() {
        return ingredientList;
    }

    public void setIngredientList(ArrayList<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }

    public ArrayList<Step> getStepList() {
        return stepList;
    }

    public void setStepList(ArrayList<Step> stepList) {
        this.stepList = stepList;
    }
}
