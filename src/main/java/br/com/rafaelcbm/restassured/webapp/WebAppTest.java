package br.com.rafaelcbm.restassured.webapp;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.restassured.http.ContentType;

public class WebAppTest {
		
	
	@Test
	public void jwtAuthTest() {
		
		Map<String, String> login=new HashMap<String, String>();
		login.put("email", "rafaelcbm@gmail.com");
		login.put("senha", "rafael654321");
		
		// api login
		// receive token 
		String token = given()			
			.log().all()
			.body(login).contentType(ContentType.JSON)
		.when()
			.post("http://barrigarest.wcaquino.me/signin")
		.then()
			.log().all()
			.statusCode(200)
			.extract().path("token")
		;
		
		// call api
		given()			
			.log().all()
			.header("Authorization", "JWT " + token)			
		.when()
			.get("http://barrigarest.wcaquino.me/contas")
		.then()
			.log().all()
			.statusCode(200)
			.body("nome", hasItem("Conta com movimentacao"))
		;		
	}
}
