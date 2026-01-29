import io.qameta.allure.Description;
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

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;

public class LoginUserTests extends BaseTests {

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

        userSteps.createUser(user);
    }

    @Test
    @DisplayName("Тест на вход под существующим пользователем")
    @Description("Попытка входа по логину и паролю зарегистрированному пользователю")
    public void shouldLoginUserTest() {
        userSteps.loginUser(user)
                   .statusCode(SC_OK)
                   .body("success", is(true))
                   .extract().response();
    }

    @Test
    @DisplayName("Тест попытки авторизации с неверным логином")
    @Description("Попытка входа по неверному адресу электронной почты")
    public void isPossibleToLogInWithWrongEmail() {
        user.withEmail("wrongEmail@yandex.ru");
        userSteps.loginUser(user)
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"))
                .extract().response();
    }

    @Test
    @DisplayName("Тест попытки авторизации с неверным паролем")
    @Description("Попытка входа под зарегистрированным логином, но неверным паролем")
    public void isPossibleToLogInWithWrongPassword() {
        user.withPassword("1234567");
        userSteps.loginUser(user)
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"))
                .extract().response();
    }

    @After
    public void tearDown() {
        if (user == null) {
            return;
        }

        try {
            Response loginResponse = userSteps.loginUser(user)
                    .extract()
                    .response();

            String accessToken = loginResponse.jsonPath().getString("accessToken");

            if (accessToken == null) {
                return;
            }

            String jwtToken = accessToken.startsWith("Bearer ")
                    ? accessToken.substring(7)
                    : accessToken;

            userSteps.deleteUser(jwtToken);

        } catch (Exception e) {
            System.err.println("Cleanup failed: " + e.getMessage());
        }
    }
}
