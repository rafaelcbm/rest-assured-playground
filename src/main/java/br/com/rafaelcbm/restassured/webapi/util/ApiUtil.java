package br.com.rafaelcbm.restassured.webapi.util;

import io.restassured.RestAssured;

public class ApiUtil  {
	
	public static Integer getIdContaByNome(String nome) {
		return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
	}
	
	public static Integer getIdMovimentacaoByDescricao(String descricao) {
		return RestAssured.get("/transacoes?descricao="+descricao).then().extract().path("id[0]");
	}
}
