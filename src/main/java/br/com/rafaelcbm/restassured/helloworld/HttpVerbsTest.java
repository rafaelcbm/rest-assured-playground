package br.com.rafaelcbm.restassured.helloworld;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
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
		// Preparation
		given()	
			.contentType("application/json")
			.body("{\n" + 
					"    \"name\":\"Jose\",\n" + 
					"    \"age\":50\n" + 
					"}")
		// Action
		.when()
			.post("/users")
		// Verification
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
		// Preparation
		given()	
			.contentType("application/json")
			.body("{\n" +
					"    \"age\":50\n" + 
					"}")
		// Action
		.when()
			.post("/users")
		// Verification
		.then()
			.log().all()
			.statusCode(400)
			.body("id", is(nullValue()))
			.body("error", is("Name é um atributo obrigatório"))
			;
	}
	
	@Test
	public void shouldPostXmlDataTest() {
		// Preparation
		given()	
			.contentType(ContentType.XML) // using enum
			.body("<user>\n" + 
					"<name>Jose</name>\n" + 
					"<age>50</age>\n" +					 
					"</user>")
		// Action
		.when()
			.post("/usersXml")
		// Verification
		.then()
			.log().all()
			.statusCode(201)
			.body("user.@id", is(notNullValue()))
			.body("user.name", is("Jose"))
			.body("user.age", is("50"))
			;
	}

}
