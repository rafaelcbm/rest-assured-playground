package br.com.rafaelcbm.restassured.helloworld;

import static io.restassured.RestAssured.given;

import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXParseException;

import io.restassured.RestAssured;
import io.restassured.matcher.RestAssuredMatchers;
import io.restassured.module.jsv.JsonSchemaValidator;

public class SchemaTest {
	
	@BeforeClass
	public static void setup() {
		RestAssured.baseURI="http://restapi.wcaquino.me";
	}
	
	@Test
	public void shouldValidateXmlSchemaTest() {
		given()
			.log().all()
		.when()
			.get("/usersXML")
		.then()
			.statusCode(200)
			.body(RestAssuredMatchers.matchesXsdInClasspath("users.xsd"))
			;
	}
	
	
	@Test(expected = SAXParseException.class)
	public void shouldNotValidateXmlSchemaTest() {
		given()
			.log().all()
		.when()
			.get("/invalidUsersXML")
		.then()
			.statusCode(200)
			.body(RestAssuredMatchers.matchesXsdInClasspath("users.xsd"))
			;
	}
	
	@Test
	public void shouldValidateJsonSchemaTest() {
		given()
			.log().all()
		.when()
			.get("/users")
		.then()
			.statusCode(200)
			.body(JsonSchemaValidator.matchesJsonSchemaInClasspath("users.json"))
			;
	}
}
