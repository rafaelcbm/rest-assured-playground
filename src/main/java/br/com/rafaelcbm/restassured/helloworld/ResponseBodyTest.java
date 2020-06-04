package br.com.rafaelcbm.restassured.helloworld;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ResponseBodyTest {
	
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
			//Convention $
			.body("$", hasSize(3))
			.body("age.findAll{it <= 25}.size()", is(2))
		;
	}
}
