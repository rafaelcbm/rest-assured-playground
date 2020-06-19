package br.com.rafaelcbm.restassured.webapi.feature;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import br.com.rafaelcbm.restassured.webapi.core.BaseTest;
import br.com.rafaelcbm.restassured.webapi.util.ApiUtil;

public class BalanceTest extends BaseTest {

	@Test
	public void shouldCalculateBalanceContasTest() {
		
		Integer contaId = ApiUtil.getIdContaByNome("Conta para saldo");
		
		given()
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == "+ contaId+"}.saldo", is("534.00"))
		;
	}
}