package br.com.rafaelcbm.restassured.helloworld;

import static io.restassured.RestAssured.given;

import org.junit.Assert;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

public class HelloWorldRestAssuredTest {
	
	@Test
	public void helloWorldRestAssured() {
		Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/ola");
		Assert.assertEquals("Ola Mundo!", response.getBody().asString());
		Assert.assertEquals(200, response.statusCode());
	}

	@Test
	public void helloWorldRestAssuredWay2() {
		Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/ola");
		
		ValidatableResponse validatableResponse = response.then();
		validatableResponse.statusCode(200);	
	}

	@Test
	public void helloWorldRestAssuredFluentWay() {
		Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/ola");
		
		ValidatableResponse validatableResponse = response.then();
		validatableResponse.statusCode(200);	
		
		// Preparation
		given()
		// Action
		.when()
			.get("http://restapi.wcaquino.me/ola")
		// Verification
		.then()
			.statusCode(200);
	}

}
