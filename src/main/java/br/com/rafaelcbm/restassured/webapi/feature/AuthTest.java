package br.com.rafaelcbm.restassured.webapi.feature;

import static io.restassured.RestAssured.given;

import org.junit.Test;

import br.com.rafaelcbm.restassured.webapi.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

public class AuthTest extends BaseTest {
	
	@Test
	public void shouldNotAccessWithoutTokenTest() {
		
		FilterableRequestSpecification reqSpec=(FilterableRequestSpecification) RestAssured.requestSpecification;
		reqSpec.removeHeader("Authorization");
		
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401);
	}
}