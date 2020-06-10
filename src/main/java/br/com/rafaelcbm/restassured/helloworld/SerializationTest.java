package br.com.rafaelcbm.restassured.helloworld;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.junit.MatcherAssert;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.rafaelcbm.restassured.model.User;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class SerializationTest {
		
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
	public void shouldPostMapTest() {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", "Tyson");
		params.put("age", 30);
		
		given()	
			.contentType("application/json")
			.body(params)
		.when()
			.post("/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("Tyson"))
			.body("age", is(30))
			;
	}
	
	@Test
	public void shouldPostObjectTest() {

		User user =new User("Mary", 35);
		
		given()	
			.contentType("application/json")
			.body(user)
		.when()
			.post("/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("Mary"))
			.body("age", is(35))
			;
	}
	
	@Test
	public void shouldDeserializeObjectTest() {

		User user =new User("Mary deserialized", 35);
		
		User insertedUser = given()	
			.contentType("application/json")
			.body(user)
		.when()
			.post("/users")
		.then()
			.log().all()
			.statusCode(201)
			.extract().body().as(User.class)
			;
		
		MatcherAssert.assertThat(insertedUser.getId(), notNullValue());
		Assert.assertEquals("Mary deserialized", insertedUser.getName());
		MatcherAssert.assertThat(insertedUser.getAge(), is(35));
	}
	
	@Test
	public void shouldPostObjectThroughXmlTest() {

		User user = new User("John", 20);
		
		given()	
			.contentType(ContentType.XML)
			.body(user)
		.when()
			.post("/usersXml")
		.then()
			.log().all()
			.statusCode(201)
			.body("user.@id", is(notNullValue()))
			.body("user.name", is("John"))
			.body("user.age", is("20"))
			;
	}
	
	@Test
	public void shouldDeserializeXmlObjectTest() {

		User user = new User("Mary Xml deserialized", 35);
		
		User insertedUser = given()	
			.contentType(ContentType.XML)
			.body(user)
		.when()
			.post("/usersXml")
		.then()
			.log().all()
			.statusCode(201)
			.extract().body().as(User.class)
			;
		
		MatcherAssert.assertThat(insertedUser.getId(), notNullValue());
		Assert.assertEquals("Mary Xml deserialized", insertedUser.getName());
		MatcherAssert.assertThat(insertedUser.getAge(), is(35));
		MatcherAssert.assertThat(insertedUser.getSalary(), nullValue());
	}
}
