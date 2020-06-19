package br.com.rafaelcbm.restassured.webapi.feature;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

import br.com.rafaelcbm.restassured.webapi.core.BaseTest;
import br.com.rafaelcbm.restassured.webapi.model.Movimentacao;
import br.com.rafaelcbm.restassured.webapi.util.ApiUtil;

public class MovimentacaoTest extends BaseTest {

	@Test
	public void shouldInsertMovimentacaoTest() {

		Movimentacao mov = getValidMovimentacao();
		
		given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)			
		;
	}
	
	@Test
	public void shouldNotInsertMovimentacaoFuturaTest() {

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
	public void shouldNotDeleteContaWithMovimentacaoTest() {
		
		Integer contaId = ApiUtil.getIdContaByNome("Conta com movimentacao");
		
		given()
			.pathParam("id", contaId)
		.when()
			.delete("/contas/{id}")
		.then()
			.statusCode(500) // Internal server error
			.body("constraint", is("transacoes_conta_id_foreign"))
		;
	}
	
	@Test
	public void shouldValidateRequiredFiledsTest() {
		
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
	public void shouldDeleteMovimentacaoTest() {
		
		Integer movId = ApiUtil.getIdMovimentacaoByDescricao("Movimentacao para exclusao");
		
		given()
			.pathParam("id", movId)
		.when()
			.delete("/transacoes/{id}")
		.then()
			.statusCode(204)
		;
	}

	private Movimentacao getValidMovimentacao() {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		String formattedDate1 = LocalDate.now().minusDays(1).format(formatter);				
		String formattedDate2 = LocalDate.now().plusDays(2).format(formatter);
		
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(ApiUtil.getIdContaByNome("Conta para movimentacoes"));
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