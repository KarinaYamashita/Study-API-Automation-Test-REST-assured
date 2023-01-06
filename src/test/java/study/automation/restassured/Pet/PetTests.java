package study.automation.restassured.Pet;

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
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import study.automation.restassured.Entities.Category;
import study.automation.restassured.Entities.Pet;
import study.automation.restassured.Entities.PetTag;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PetTests {
    private static Pet pet;
    public static Faker faker;
    public static RequestSpecification request;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
        faker = new Faker();
        Integer id = faker.number().randomDigit();
        Category category = new Category(faker.number().randomDigit(), faker.name().firstName());
        String name = faker.dog().name();
        String[] photoUrls = new String[] { faker.internet().image() };
        PetTag[] tags = new PetTag[] { new PetTag(faker.number().randomDigit(), faker.funnyName().name()) };

        pet = new Pet(id,
                category,
                name,
                photoUrls,
                tags,
                "available");
    }

    @BeforeEach
    public void setRequest() {
        request = RestAssured.given()
                .config(RestAssured.config()
                        .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
                .header("api-key", "special-key")
                .contentType(ContentType.JSON);
    }

    @Test
    @Order(1)
    public void AddNewPet_WithValidData_ReturnOk() {
        request
                .body(pet)
                .when()
                .post("/pet")
                .then()
                .assertThat().statusCode(200).and()
                .body("size()", Matchers.equalTo(6))
                .body("status", Matchers.isA(String.class));
    }

    @Test
    @Order(2)
    public void UpdateAnExistingPet_WithValidData_ReturnOk() {
        pet.setName("Jose");
        request
                .body(pet)
                .when()
                .post("/pet")
                .then()
                .assertThat().statusCode(200).and()
                .body("size()", Matchers.equalTo(6))
                .body("status", Matchers.isA(String.class));
    }

    @Test
    @Order(3)
    public void DeletePet_PetExists_ReturneOk() {
        request
                .when()
                .delete("/pet/" + pet.getId())
                .then()
                .assertThat().statusCode(200)
                .and().time(Matchers.lessThan(2000L))
                .log();
    }

    @Test
    public void AddNewPet_WithInvalidData_ReturnBadRequest() {
        Response response = request
                .body("pet")
                .when()
                .post("/pet")
                .then()
                .extract().response();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, response.statusCode());
        Assertions.assertTrue(response.getBody().asPrettyString().contains("unknown"));
        Assertions.assertEquals(3, response.body().jsonPath().getMap("$").size());
    }

}
