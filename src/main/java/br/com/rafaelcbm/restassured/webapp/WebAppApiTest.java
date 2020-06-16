package br.com.rafaelcbm.restassured.webapp;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import br.com.rafaelcbm.restassured.webapp.core.BaseTest;

public class WebAppApiTest extends BaseTest {

	private String token;
	
	@Before
	public void login() {
		// Login
		Map<String, String> login = new HashMap<String, String>();
		login.put("email", "rafaelcbm@gmail.com");
		login.put("senha", "rafael654321");

		token = 
		given()
			.body(login)
		.when()
			.post("/signin")			
		.then()
			.statusCode(200)
			.extract().path("token");
	}

	
	@Test
	public void shouldNotAccessWithoutTokenTest() {
		given().when().get("/contas").then().statusCode(401);
	}

	@Test
	public void shouldInsertContaTest() {

		// API Call
		given()
			.header("Authorization", "JWT "+ token)  // Bearer auth
			.body("{\"nome\":\"conta qualquer\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(201);
	}
	
	@Test
	public void shouldUpdateContaTest() {

		// API Call
		given()
			.header("Authorization", "JWT "+ token)
			.body("{\"nome\":\"conta alterada\"}")
		.when()
			.put("/contas/185843")
		.then()
			.statusCode(200)
			.body("nome", is("conta alterada"))
		;
	}
	
	@Test
	public void shouldNotInsertContaWithSameNameTest() {

		// API Call
		given()
			.header("Authorization", "JWT "+ token)  // Bearer auth
			.body("{\"nome\":\"conta alterada\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
			;
	}
	
	@Test
	public void shouldInsertMovimentacaoTest() {

		Movimentacao mov = getValidMovimentacao();
		
		given()
			.header("Authorization", "JWT "+ token)
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201);
	}
	
	@Test
	public void shouldValidateRequiredFiledsTest() {
		
		given()
			.header("Authorization", "JWT "+ token)
			.body("{}")
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(8))
			.body("msg", hasItems(
					"Data da Movimentação é obrigatório",
					"Data do pagamento é obrigatório",
					"Descrição é obrigatório",
					"Interessado é obrigatório",
					"Valor é obrigatório",
					"Valor deve ser um número",
					"Conta é obrigatório",
					"Situação é obrigatório"
					))
		;
	}
	
	@Test
	public void shouldNotInsertMovimentacaoFuturaTest2() {

		Movimentacao mov = getValidMovimentacao();
		mov.setData_transacao("12/12/2020");
		
		given()
			.header("Authorization", "JWT "+ token)
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(1))
			.body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual")) ;
	}
	
	@Test
	public void shouldNotDeleteContaWithMovimentacaoTest() {
		
		Movimentacao mov = getValidMovimentacao();
		mov.setData_transacao("12/12/2020");
		
		given()
			.header("Authorization", "JWT "+ token)			
		.when()
			.delete("/contas/185843")
		.then()
			.statusCode(500) // Internal server error
			.body("constraint", is("transacoes_conta_id_foreign"))
		;
	}
	
	@Test
	public void shouldCalculateBalanceContasTest() {
		
		given()
			.header("Authorization", "JWT "+ token)			
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == 185843}.saldo", is("100.00"))
		;
	}

	@Test
	public void shouldDeleteMovimentacaoTest() {
		
		given()
			.header("Authorization", "JWT "+ token)			
		.when()
			.delete("/transacoes/164642")
		.then()
			.statusCode(204)
		;
	}
	
	private Movimentacao getValidMovimentacao() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(185843);
		mov.setDescricao("Descricao da mov");
		mov.setEnvolvido("Envolvido da mov");
		mov.setTipo("REC");
		mov.setData_transacao("01/01/2020");
		mov.setData_pagamento("10/05/2020");
		mov.setValor(100f);
		mov.setStatus(true);
		
		return mov;		
	}
		
}