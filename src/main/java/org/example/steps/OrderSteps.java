package org.example.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.example.model.Order;

import static config.RestConfig.CREATE_ORDER;
import static io.restassured.RestAssured.given;

public class OrderSteps {

    @Step("Создание заказа")
    public ValidatableResponse createOrder(Order order) {
        return given()
                .body(order)
                .when()
                .post(CREATE_ORDER)
                .then();
    }
}
