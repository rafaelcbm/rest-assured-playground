package br.com.rafaelcbm.restassured.webapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.com.rafaelcbm.restassured.webapi.core.BaseTest;
import br.com.rafaelcbm.restassured.webapi.model.Movimentacao;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApiOrderedTest extends BaseTest {

	private static String CONTA_NAME = "Conta" + System.nanoTime();
	private static Integer CONTA_ID;
	private static Integer MOV_ID;
	
	@BeforeClass
	public static void login() {
		// Login
		Map<String, String> login = new HashMap<String, String>();
		login.put("email", "rafaelcbm@gmail.com");
		login.put("senha", "rafael654321");

		String token = 
		given()
			.body(login)
		.when()
			.post("/signin")			
		.then()
			.statusCode(200)
			.extract().path("token");
		
		// (Bearer auth)
		RestAssured.requestSpecification.header("Authorization", "JWT "+ token);
	}

	@Test
	public void t01_shouldInsertContaTest() {

		// API Call
		CONTA_ID = given()
			.body("{\"nome\": \""+CONTA_NAME+"\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
	}
	
	@Test
	public void t02_shouldUpdateContaTest() {

		given()
			.body("{\"nome\": \""+CONTA_NAME+" alterada\"}")
			.pathParam("id", CONTA_ID)
		.when()
			.put("/contas/{id}")
		.then()
			.statusCode(200)
			.body("nome", is(CONTA_NAME + " alterada"))
		;
	}
	
	@Test
	public void t03_shouldNotInsertContaWithSameNameTest() {

		given()
		.body("{\"nome\": \""+CONTA_NAME+" alterada\"}")
		.when()
			.post("/contas")
		.then()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
		;
	}
	
	@Test
	public void t04_shouldInsertMovimentacaoTest() {

		Movimentacao mov = getValidMovimentacao();
		
		MOV_ID = given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
	}
	
	@Test
	public void t05_shouldValidateRequiredFiledsTest() {
		
		given()
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
	public void t06_shouldNotInsertMovimentacaoFuturaTest() {

		LocalDate localDate = LocalDate.now().plusDays(1);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String formattedDate = localDate.format(formatter);
		
		Movimentacao mov = getValidMovimentacao();
		mov.setData_transacao(formattedDate);
		
		given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(1))
			.body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual")) ;
	}
	
	@Test
	public void t07_shouldNotDeleteContaWithMovimentacaoTest() {
		
		given()
			.pathParam("id", CONTA_ID)
		.when()
			.delete("/contas/{id}")
		.then()
			.statusCode(500) // Internal server error
			.body("constraint", is("transacoes_conta_id_foreign"))
		;
	}
	
	@Test
	public void t08_shouldCalculateBalanceContasTest() {
		
		given()
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("100.00"))
		;
	}

	@Test
	public void t09_shouldDeleteMovimentacaoTest() {
		
		given()
			.pathParam("id", MOV_ID)
		.when()
			.delete("/transacoes/{id}")
		.then()
			.statusCode(204)
		;
	}
	
	@Test
	public void t10_shouldNotAccessWithoutTokenTest() {
		
		FilterableRequestSpecification reqSpec=(FilterableRequestSpecification) RestAssured.requestSpecification;
		reqSpec.removeHeader("Authorization");
		
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401);
	}
	
	private Movimentacao getValidMovimentacao() {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		String formattedDate1 = LocalDate.now().minusDays(1).format(formatter);				
		String formattedDate2 = LocalDate.now().plusDays(2).format(formatter);
		
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(CONTA_ID);
		mov.setDescricao("Descricao da mov");
		mov.setEnvolvido("Envolvido da mov");
		mov.setTipo("REC");
		mov.setData_transacao(formattedDate1);
		mov.setData_pagamento(formattedDate2);
		mov.setValor(100f);
		mov.setStatus(true);
		
		return mov;		
	}
		
}