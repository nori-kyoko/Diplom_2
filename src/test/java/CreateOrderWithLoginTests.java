import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.model.Order;
import org.example.model.User;
import org.example.steps.OrderSteps;
import org.example.steps.UserSteps;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.is;

public class CreateOrderWithLoginTests extends BaseTests {

    private User user;
    private UserSteps userSteps = new UserSteps();
    private Order order = new Order();
    private OrderSteps orderSteps = new OrderSteps();

    @Before
    public void setUp() {
        String email = java.util.UUID.randomUUID().toString() + "@example.com";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        user = new User();
        user.withEmail(email)
                .withPassword(RandomStringUtils.randomAlphanumeric(10))
                .withName(RandomStringUtils.randomAlphabetic(10));

        userSteps.createUser(user);
        userSteps.loginUser(user);
    }

    @Test
    @DisplayName("Создания заказ c авторизации с ингредиентами")
    @Description("Попытка создания заказа авторизованным пользователем с ингредиентами")
    public void isPossibleCreateOrderWithAuthorizationWithIngredients() {
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
    @DisplayName("Создание заказа c авторизации без ингредиентов")
    @Description("Попытка создания заказа авторизованным пользователем без ингредиентов")
    public void isPossibleCreateOrderWithoutAuthorizationWithoutIngredients() {
        String[] ingredients = {};
        order.withIngredients(ingredients);
        orderSteps.createOrder(order)
                .statusCode(SC_BAD_REQUEST)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"))
                .extract().response();
    }

    @Test
    @DisplayName("Создание заказа c авторизации с неверным хешем ингредиентов")
    @Description("Попытка заказа авторизованным пользователем с несуществующими хешами в базе данных ингредиентов")
    public void isPossibleCreateOrderWithoutAuthorizationWithWrongIngredients() {
        String[] ingredients = {"60d3b41abdacab0026a733c6", "609646e4dc916e00276b2870"};
        order.withIngredients(ingredients);
        orderSteps.createOrder(order)
                .statusCode(SC_INTERNAL_SERVER_ERROR)
                .body("success", is(false))
                .extract().response();
    }

}
