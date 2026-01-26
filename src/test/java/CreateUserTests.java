import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.model.User;

import org.example.steps.UserSteps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.is;

public class CreateUserTests extends BaseTests {

    private User user;
    private UserSteps userSteps = new UserSteps();

    @Before
    public void setUp() {
        String email = java.util.UUID.randomUUID().toString() + "@example.com";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        user = new User();
        user.withEmail(email)
                .withPassword(RandomStringUtils.randomAlphanumeric(10))
                .withName(RandomStringUtils.randomAlphabetic(10));

    }

    @Test
    @DisplayName("Тест на создание пользователя")
    public void shouldCreateUser() {
        Response response = userSteps.createUser(user)
                .statusCode(SC_OK)
                .body("success", is(true))
                .extract().response();

        String accessToken = response.jsonPath().getString("accessToken");
        user.withAccessToken(accessToken);
    }

    @Test
    @DisplayName("Тест на создание пользователя, который уже зарегистрирован")
    public void isPossibleToCreateIdenticalUser() {
        userSteps.createUser(user);
        Response response = userSteps.createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("message", is("User already exists"))
                .extract().response();

        String accessToken = response.jsonPath().getString("accessToken");
        user.withAccessToken(accessToken);
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля email")
    public void isPossibleToCreateUserWithoutEmail() {
        user.withEmail("");
        Response response = userSteps.createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("message", is("Email, password and name are required fields"))
                .extract().response();

        String accessToken = response.jsonPath().getString("accessToken");
        user.withAccessToken(accessToken);
    }

    @Test
    @DisplayName("Создания пользователя без обязательного поля пароль")
    public void isPossibleToCreateUserWithoutPassword() {
        user.withPassword("");
        Response response = userSteps.createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("message", is("Email, password and name are required fields"))
                .extract().response();

        String accessToken = response.jsonPath().getString("accessToken");
        user.withAccessToken(accessToken);
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля имя")
    public void isPossibleToCreateUserWithoutName() {
        user.withName("");
        Response response = userSteps.createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("message", is("Email, password and name are required fields"))
                .extract().response();

        String accessToken = response.jsonPath().getString("accessToken");
        user.withAccessToken(accessToken);
    }

    @After
    public void tearDown() {
        if (user == null || user.getAccessToken() == null) {
            return;
        }

        try {
            String accessToken = user.getAccessToken();
            String jwtToken = accessToken.startsWith("Bearer ")
                    ? accessToken.substring(7)
                    : accessToken;

            userSteps.deleteUser(jwtToken);

        } catch (Exception e) {
            System.err.println("Cleanup failed: " + e.getMessage());
        }
    }
}
