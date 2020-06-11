package br.com.rafaelcbm.restassured.helloworld;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import org.junit.Test;

import io.restassured.http.ContentType;

public class SendDataTest {
		
	@Test
	public void sendDataViaQueryParamTest() {
		given()			
		.log().all()
		.when()
			.get("http://restapi.wcaquino.me/v2/users?format=json")
		.then()
			.log().all()
			.statusCode(200)
			.contentType(ContentType.JSON)
		;		
	}

	@Test
	public void sendDataViaQueryParam2Test() {
		given()			
			.log().all()
			.queryParam("format", "xml")
		.when()
			.get("http://restapi.wcaquino.me/v2/users")
		.then()
			.log().all()
			.statusCode(200)
			.contentType(ContentType.XML)
			.contentType(containsString("utf-8"))
		;		
	}

	@Test
	public void sendDataViaHeaderTest() {
		given()			
			.log().all()
			.accept(ContentType.JSON)
		.when()
			.get("http://restapi.wcaquino.me/v2/users")
		.then()
			.log().all()
			.statusCode(200)
			.contentType(ContentType.JSON)
		;		
	}

}
