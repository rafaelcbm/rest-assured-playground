package br.com.rafaelcbm.restassured.helloworld;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class UsersJsonTest {
	
	@Test
	public void bodyTest() {
		// Preparation
		given()
		// Action
		.when()
			.get("http://restapi.wcaquino.me/ola")
		// Verification
		.then()
			.statusCode(200)
			.body(is("Ola Mundo!"))
			.body(containsString("Mundo"))
			.body(is(not(nullValue())));
	}

	@Test
	public void jsonFirstLevelTest() {
		// Preparation
		given()
		// Action
		.when()
			.get("http://restapi.wcaquino.me/users/1")
		// Verification
		.then()
			.statusCode(200)
			.body("id", is(1))
			.body("name", containsString("Silva"))
			.body("age", greaterThan(18))
			;
	}

	@Test
	public void jsonFirstLevelWay2Test() {
		
		Response response = RestAssured.request(Method.GET,"http://restapi.wcaquino.me/users/1");

		// path
		assertEquals(Integer.valueOf(1), response.path("id"));  
		assertEquals(Integer.valueOf(1), response.path("%s", "id"));
		
		// Jsonpath
		JsonPath jsonPath = JsonPath.from(response.asString());
		assertEquals(1, jsonPath.getInt("id"));  
		assertEquals("João da Silva", jsonPath.getString("name"));
	}

	@Test
	public void jsonSecondLevelTest() {
		
		// Preparation
		given()
		// Action
		.when()
			.get("http://restapi.wcaquino.me/users/2")
		// Verification
		.then()
			.statusCode(200)
			.body("name", containsString("Joaquina"))
			.body("endereco.rua", is("Rua dos bobos"))
		;
	}

	@Test
	public void jsonListTest() {
		// Preparation
		given()
		// Action
		.when()
			.get("http://restapi.wcaquino.me/users/3")
		// Verification
		.then()
			.statusCode(200)
			.body("name", containsString("Ana"))
			.body("filhos", hasSize(2))
			.body("filhos[0].name", is("Zezinho"))
			.body("filhos[1].name", is("Luizinho"))
			.body("filhos.name", hasSize(2))
			.body("filhos.name", hasItem("Zezinho"))
			.body("filhos.name", hasItems("Zezinho", "Luizinho"))
		;
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void jsonRootListTest() {
		// Preparation
		given()
		// Action
		.when()
			.get("http://restapi.wcaquino.me/users")
		// Verification
		.then()
			.statusCode(200)
			//Convention $
			.body("$", hasSize(3))
			.body("", hasSize(3))
			.body("name", hasItems("João da Silva", "Maria Joaquina", "Ana Júlia"))
			.body("age[1]", is(25))
			.body("filhos.name", hasItem(Arrays.asList("Zezinho", "Luizinho")))
			.body("salary", contains(1234.5678f, 2500, null))
		;
	}
	
	@Test
	public void errorTest() {
		// Preparation
		given()
		// Action
		.when()
			.get("http://restapi.wcaquino.me/users/4")
		// Verification
		.then()
			.statusCode(404)
			.body("error", is("Usuário inexistente"))
		;
	}
	
	@Test
	public void advancedSearchesTest() {
		// Preparation
		given()
		// Action
		.when()
			.get("http://restapi.wcaquino.me/users")
		// Verification
		.then()
			.statusCode(200)
			// Convention $ with root
			.body("$", hasSize(3))
			// without $, the same
			.body("", hasSize(3))
			.body("age.findAll{it <= 25}.size()", is(2))
			.body("age.findAll{it <= 25 && it>20}.size()", is(1))
			.body("findAll{it.age <= 25 && it.age > 20}.name", hasItem("Maria Joaquina"))
			.body("findAll{it.age <= 25}[0].name", is("Maria Joaquina"))
			// back to front index
			.body("findAll{it.age <= 25}[-1].name", is("Ana Júlia"))
			// find first
			.body("find{it.age <= 25}.name", is("Maria Joaquina"))
			// contains (based on Groovy)
			.body("findAll{it.name.contains('n')}.name", hasItems("Maria Joaquina", "Ana Júlia"))
			.body("findAll{it.name.length() > 10}.name", hasItems("João da Silva", "Maria Joaquina"))
			// collect
			.body("name.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
			.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
			// both Matchers conditions - Refactored in the next test: shouldJoinJsonPathWithJava()
			.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()", allOf(arrayContaining("MARIA JOAQUINA"),arrayWithSize(1)))
			// collect transformation
			.body("age.collect{it * 2}", hasItems(60, 50, 40))
			.body("id.max()", is(3))
			// numbers comparison
			.body("salary.min()", is(1234.5677f))
			.body("salary.findAll{it != null}.sum()", is(closeTo(3734.5678f, 0.001)))
			.body("salary.findAll{it != null}.sum()", allOf(greaterThan(3000d), lessThan(5000d)))
		;
	}
	
	@Test
	public void shouldJoinJsonPathWithJava() {
		// Preparation
		ArrayList<String> names = 
		given()
		.when()
			.get("http://restapi.wcaquino.me/users")
		.then()
			.statusCode(200)
			// both Matchers conditions
			.extract().path("name.findAll{it.startsWith('Maria')}");
		
		Assert.assertEquals(1, names.size());
		Assert.assertTrue(names.get(0).equalsIgnoreCase("mAria JoaquiNa"));
		Assert.assertEquals(names.get(0).toUpperCase(), "MARIA JOAQUINA");
		
	}
}
