import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.example.model.Order;
import org.example.steps.OrderSteps;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.is;


public class CreateOrderWithoutLoginTests extends BaseTests {

    private Order order = new Order();
    private OrderSteps orderSteps = new OrderSteps();

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @DisplayName("Создания заказ без авторизации с ингредиентами")
    public void isPossibleCreateOrderWithoutAuthorizationWithIngredients() {
        String[] ingredients = {
                "61c0c5a71d1f82001bdaaa6d",
                "61c0c5a71d1f82001bdaaa6f"
        };
        order.withIngredients(ingredients);
        orderSteps.createOrder(order)
        .statusCode(SC_OK)
                .body("success", is(true))
                .extract().response();
    }

    @Test
    @DisplayName("Создание заказа без авторизации без ингредиентов")
    public void isPossibleCreateOrderWithoutAuthorizationWithoutIngredients() {
        String[] ingredients = {};
        order.withIngredients(ingredients);
        orderSteps.createOrder(order)
                .statusCode(SC_BAD_REQUEST)
                .body("success", is(false))
                .extract().response();
    }

    @Test
    @DisplayName("Создание заказа без авторизации с неверным хешем ингредиентов")
    public void isPossibleCreateOrderWithoutAuthorizationWithWrongIngredients() {
        String[] ingredients = {"60d3b41abdacab0026a733c6", "609646e4dc916e00276b2870"};
        order.withIngredients(ingredients);
        orderSteps.createOrder(order)
                .statusCode(SC_INTERNAL_SERVER_ERROR)
                .body("success", is(false))
                .extract().response();
    }
}
