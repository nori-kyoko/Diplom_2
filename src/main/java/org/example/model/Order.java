package org.example.model;

import io.qameta.allure.internal.shadowed.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Order {
    public String[] ingredients;

    public Order withIngredients(String[] ingredients) {
        this.ingredients = ingredients;
        return this;
    }

    public String[] getIngredients() {
        return ingredients;
    }
}
