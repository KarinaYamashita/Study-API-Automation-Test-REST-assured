package study.automation.restassured.User;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.github.javafaker.Faker;

import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import study.automation.restassured.Entities.User;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserTests {
    private static User user;
    public static Faker faker;
    public static RequestSpecification request;

    @BeforeAll
    public static void setup(){
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
        faker = new Faker();

        user = new User(
            faker.name().username(),
            faker.name().firstName(),
            faker.name().lastName(),
            faker.internet().safeEmailAddress(),
            faker.internet().password(8, 18),
            faker.phoneNumber().toString());
    }

    @BeforeEach
    public void setRequest(){
        request = RestAssured.given().config(RestAssured.config().logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
                .header("api-key","special-key")
                .contentType(ContentType.JSON);
    }

    @Test
    @Order(1)
    public void CreateNewUser_WithValidData_ReturnOk() {
        request
            .body(user)
            .when()
            .post("/user")
            .then()
            .assertThat().statusCode(200).and()
                .body("code", Matchers.equalTo(200))
                .body("type", Matchers.equalTo("unknown"))
                .body("message", Matchers.isA(String.class))
                .body("size()",Matchers.equalTo(3));
    }


    @Test
    @Order(2)
    public void GetLogin_ValidUser_ReturnOk() {
        request
                .param("username", user.getUsername())
                .param("password", user.getPassword())
                .when()
                .get("/user/login")
                .then()
                .assertThat()
                .statusCode(200)
                .and().time(Matchers.lessThan(2000L))
                .and().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("loginResponseSchema.json"));
    }

    @Test
    @Order(3)
    public void GetUserByUsername_ValidUser_ReturnOk() {

        request
                .when()
                .get("/user/" + user.getUsername())
                .then()
                .assertThat()
                .statusCode(200)
                .and().time(Matchers.lessThan(2000L))
                .and().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("getUserResponseSchema.json"));
    }

    @Test
    @Order(4)
    public void DeleteUser_UserExists_ReturneOk() {
        request
                .when()
                .delete("/user/" + user.getUsername())
                .then()
                .assertThat().statusCode(200)
                .and().time(Matchers.lessThan(2000L))
                .log();
    }

    @Test
    public void CreateNewUser_WithInvalidData_ReturnBadRequest() {
        Response response = request
            .body("teste")
            .when()
            .post("/user")
            .then()
            .extract().response();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertTrue(response.getBody().asPrettyString().contains("unknown"));
        Assertions.assertEquals(3, response.body().jsonPath().getMap("$").size());
    }
}
