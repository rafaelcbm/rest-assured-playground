package br.com.rafaelcbm.restassured.webapi.feature;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import br.com.rafaelcbm.restassured.webapi.core.BaseTest;
import br.com.rafaelcbm.restassured.webapi.util.ApiUtil;

public class ContasTest extends BaseTest {

	@Test
	public void shouldInsertContaTest() {

		// API Call
		given()
			.body("{\"nome\": \"Conta inserida\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
		;
	}
	
	@Test
	public void shouldUpdateContaTest() {

		Integer contaId = ApiUtil.getIdContaByNome("Conta para alterar");
		
		given()
			.body("{\"nome\": \"Conta alterada\"}")
			.pathParam("id", contaId) 
		.when()
			.put("/contas/{id}")
		.then()
			.statusCode(200)
			.body("nome", is("Conta alterada"))
		;
	}
	
	@Test
	public void shouldNotInsertContaWithSameNameTest() {

		given()
		.body("{\"nome\": \"Conta mesmo nome\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
		;
	}
}