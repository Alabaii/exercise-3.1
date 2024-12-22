package com.example;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserApiTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://reqres.in";
    }

    // Позитивный тест для POST /register
    @Test
    @Feature("Register User")
    @Story("User Registration")
    @Description("Test case to register a new user successfully")
    public void testRegisterUserSuccess() {
        String requestBody = "{ \"email\": \"eve.holt@reqres.in\", \"password\": \"pistol\" }";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/register")
                .then()
                .statusCode(200)
                .extract().response();

        String token = response.jsonPath().getString("token");
        assertThat(token, notNullValue());  // Проверяем, что токен не null
        assertThat(token, not(isEmptyString()));
    }

    // Негативный тест для POST /register
    @Test
    @Feature("Register User")
    @Story("User Registration Failure")
    @Description("Test case to register a user with missing password")
    public void testRegisterUserFailure() {
        String requestBody = "{ \"email\": \"eve.holt@reqres.in\" }";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/register")
                .then()
                .statusCode(400)
                .extract().response();

        assertThat(response.jsonPath().getString("error"), equalTo("Missing password"));
    }

    // Позитивный тест для PUT /users/{id}
    @Test
    @Feature("Update User")
    @Story("User Update")
    @Description("Test case to update user successfully")
    public void testUpdateUserSuccess() {
        String requestBody = "{ \"name\": \"John\", \"job\": \"Developer\" }";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/api/users/2")
                .then()
                .statusCode(200)
                .extract().response();

        assertThat(response.jsonPath().getString("name"), equalTo("John"));
        assertThat(response.jsonPath().getString("job"), equalTo("Developer"));
    }

    // Негативный тест для PUT /users/{id}
    @Test
    @Feature("Update User")
    @Story("User Update Failure")
    @Description("Test case to update user with invalid data")
    public void testUpdateUserFailure() {
        String requestBody = "{ \"name\": \"\", \"job\": \"\" }";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/api/users/2")
                .then()
                .statusCode(400)
                .extract().response();

        assertThat(response.jsonPath().getString("error"), equalTo("Invalid input"));
    }

    // Позитивный тест для DELETE /users/{id}
    @Test
    @Feature("Delete User")
    @Story("User Deletion")
    @Description("Test case to delete a user successfully")
    public void testDeleteUserSuccess() {
        Response response = given()
                .when()
                .delete("/api/users/2")
                .then()
                .statusCode(204)
                .extract().response();
    }

    // Негативный тест для DELETE /users/{id}
    @Test
    @Feature("Delete User")
    @Story("User Deletion Failure")
    @Description("Test case to delete a non-existing user")
    public void testDeleteUserFailure() {
        Response response = given()
                .when()
                .delete("/api/users/9999")
                .then()
                .statusCode(404)
                .extract().response();

        assertThat(response.jsonPath().getString("error"), equalTo("Not Found"));
    }

    // Позитивный тест для GET /users/{id}
    @Test
    @Feature("Get User")
    @Story("User Retrieval")
    @Description("Test case to get a user successfully")
    public void testGetUserSuccess() {
        Response response = given()
                .when()
                .get("/api/users/2")
                .then()
                .statusCode(200)
                .extract().response();

        assertThat(response.jsonPath().getString("data.first_name"), equalTo("Janet"));
        assertThat(response.jsonPath().getString("data.last_name"), equalTo("Weaver"));
    }

    // Негативный тест для GET /users/{id}
    @Test
    @Feature("Get User")
    @Story("User Retrieval Failure")
    @Description("Test case to get a non-existing user")
    public void testGetUserFailure() {
        Response response = given()
                .when()
                .get("/api/users/9999")
                .then()
                .statusCode(404)
                .extract().response();

        assertThat(response.jsonPath().getString("error"), equalTo("Not Found"));
    }
}

