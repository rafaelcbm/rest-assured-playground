package br.com.rafaelcbm.restassured.auth;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.restassured.http.ContentType;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class AuthTest {
		
	@Test
	public void starWarsApiTest() {
		given()			
			.log().all()
		.when()
			.get("https://swapi.dev/api/people/1")
		.then()
			.log().all()
			.statusCode(200)
			.body("name", is("Luke Skywalker"))
			;
	}
	
	@Test
	public void openWeatherWithApiKeyTest() {
		given()			
			.log().all()
			.queryParam("q", "Fortaleza")
			// TODO: Should inform API KEY
			.queryParam("appId", "")
			.queryParam("units", "metric")
		.when()
			.get("http://api.openweathermap.org/data/2.5/weather")
		.then()
			.log().all()
			.statusCode(200)
			.body("name", is("Fortaleza"))
			.body("coord.lon", is(-38.52f))
			.body("main.temp", greaterThan(20f))
			;
	}
	
	@Test
	public void shouldNotAccessWithoutPasswordTest() {
		given()			
			.log().all()
		.when()
			.get("http://restapi.wcaquino.me/basicauth")
		.then()
			.log().all()
			.statusCode(401)			
			;
	}
	
	@Test
	public void shouldBasicAuthTest() {
		given()			
			.log().all()
		.when()
			.get("http://admin:senha@restapi.wcaquino.me/basicauth")
		.then()
			.log().all()
			.statusCode(200)
			.body("status", is("logado"))
			;
	}
	
	@Test
	public void shouldBasicAuthTest2() {
		given()			
			.log().all()
			.auth().basic("admin", "senha")
		.when()
			.get("http://restapi.wcaquino.me/basicauth")
		.then()
			.log().all()
			.statusCode(200)
			.body("status", is("logado"))
		;
	}


	// Preemptive or on challange basic auth
	@Test
	public void shouldBasicAuthOnChallengeTest() {
		given()			
			.log().all()
			.auth().preemptive().basic("admin", "senha")
		.when()
			.get("http://restapi.wcaquino.me/basicauth2")
		.then()
			.log().all()
			.statusCode(200)
			.body("status", is("logado"))
		;
	}
	
	@Test
	public void jwtAuthTest() {
		
		Map<String, String> login=new HashMap<String, String>();
		//TODO: add credentials
		login.put("email", "");
		login.put("senha", "");
		
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
	
	@Test
	public void shouldAccessWebAppViaSessionCookieTest() {
		
		// login
		String cookie = given()			
			.log().all()
			.formParam("email", "rafaelcbm@gmail.com")
			.formParam("senha", "rafael654321")
			.contentType(ContentType.URLENC.withCharset("UTF-8"))
		.when()
			.post("http://seubarriga.wcaquino.me/logar")
		.then()
			.log().all()
			.statusCode(200)
			.extract().header("set-cookie")
		;
		
		cookie = cookie.split("=")[1].split(";")[0];		
		System.out.println("*** cookie = " + cookie);
		
		// call api
		String body = given()			
			.log().all()
			.cookie("connect.sid", cookie)			
		.when()
			.get("http://seubarriga.wcaquino.me/contas")
		.then()
			.log().all()
			.statusCode(200)
			.body("html.body.table.tbody.tr[0].td[0]", is("Conta alterada2"))
			.extract().body().asString();
		;
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, body);
		String contaNameExtracted = xmlPath.getString("html.body.table.tbody.tr[0].td[0]");
		System.out.println("*** contaNameExtracted = " + contaNameExtracted);
		
	}
}
