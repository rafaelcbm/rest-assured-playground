package br.com.rafaelcbm.restassured.webapi.suite;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import br.com.rafaelcbm.restassured.webapi.core.BaseTest;
import br.com.rafaelcbm.restassured.webapi.feature.AuthTest;
import br.com.rafaelcbm.restassured.webapi.feature.BalanceTest;
import br.com.rafaelcbm.restassured.webapi.feature.ContasTest;
import br.com.rafaelcbm.restassured.webapi.feature.MovimentacaoTest;
import io.restassured.RestAssured;

@RunWith(Suite.class)
@SuiteClasses({
	ContasTest.class,
	MovimentacaoTest.class,
	BalanceTest.class,
	AuthTest.class, // Should be the last one
})
public class ApiSuite extends BaseTest {

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
		
		// Reset data
		RestAssured.get("/reset").then().statusCode(200);
	}
}
