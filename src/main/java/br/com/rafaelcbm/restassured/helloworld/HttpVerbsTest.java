package br.com.rafaelcbm.restassured.helloworld;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class HttpVerbsTest {
		
	public static RequestSpecification reqSpec;
	public static ResponseSpecification resSpec;
	
	@BeforeClass
	public static void setup() {
		RestAssured.baseURI="http://restapi.wcaquino.me";
//		RestAssured.port="443";
//		RestAssured.basePath="";
				
		RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
		reqBuilder.log(LogDetail.ALL);
		reqSpec = reqBuilder.build();

		ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();		
		resBuilder.log(LogDetail.ALL);
		resSpec = resBuilder.build();

		// Adding Global Specification
		RestAssured.requestSpecification = reqSpec;
		RestAssured.responseSpecification = resSpec;
	}
	
	@Test
	public void shouldPostDataTest() {
		given()	
			.contentType("application/json")
			.body("{\n" + 
					"    \"name\":\"Jose\",\n" + 
					"    \"age\":50\n" + 
					"}")
		.when()
			.post("/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("Jose"))
			.body("age", is(50))
			;
	}
	
	@Test
	public void shouldNotPostInvalidDataTest() {
		given()	
			.contentType("application/json")
			.body("{\n" +
					"    \"age\":50\n" + 
					"}")
		.when()
			.post("/users")
		.then()
			.log().all()
			.statusCode(400)
			.body("id", is(nullValue()))
			.body("error", is("Name é um atributo obrigatório"))
			;
	}
	
	@Test
	public void shouldPostXmlDataTest() {
		given()	
			.contentType(ContentType.XML) // using enum
			.body("<user>\n" + 
					"<name>Jose</name>\n" + 
					"<age>50</age>\n" +					 
					"</user>")
		.when()
			.post("/usersXml")
		.then()
			.log().all()
			.statusCode(201)
			.body("user.@id", is(notNullValue()))
			.body("user.name", is("Jose"))
			.body("user.age", is("50"))
			;
	}
	
	@Test
	public void shouldPutDataTest() {
		given()	
			.contentType("application/json")
			.body("{\n" + 
					"    \"name\":\"Jason\",\n" + 
					"    \"age\":80\n" + 
					"}")
		.when()
			.put("/users/1")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Jason"))
			.body("age", is(80))
			.body("salary", is(1234.5678f))
			;
	}
	
	@Test
	public void shouldCustomizeUrlTest() {
		given()	
			.contentType("application/json")
			.body("{\n" + 
					"    \"name\":\"Jason\",\n" + 
					"    \"age\":80\n" + 
					"}")
			.pathParam("resourceName", "users")
			.pathParam("resourceId", 1)
		.when()
			.put("/{resourceName}/{resourceId}", "users", 1)
		//Other way to pass params
			//.put("/{resourceName}/{resourceId}", "users", 1)
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Jason"))
			.body("age", is(80))
			.body("salary", is(1234.5678f))
			;
	}
	
	@Test
	public void shouldDeleteTest() {
		given()				
		.when()
			.delete("/users/1")
		.then()
			.log().all()
			.statusCode(204)
			;
	}
	
	@Test
	public void shouldNotDeleteInvalidIdTest() {
		given()				
		.when()
			.delete("/users/10000")
		.then()
			.log().all()
			.statusCode(400)
			.body("error", is("Registro inexistente"))
		;
	}

}
