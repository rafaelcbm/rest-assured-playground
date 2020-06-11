package br.com.rafaelcbm.restassured.helloworld;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.io.File;

import org.junit.Test;

public class FileTest {
		
	@Test
	public void shouldValidateUploadTest() {
		given()			
			.log().all()
		.when()
			.post("http://restapi.wcaquino.me/upload")
		.then()
			.log().all()
			.statusCode(404) //should 404
			.body("error", is("Arquivo não enviado"))
		;		
	}

	@Test
	public void shouldUploadTest() {
		given()			
			.log().all()
			.multiPart("arquivo", new File("src/main/resources/users.pdf"))
		.when()
			.post("http://restapi.wcaquino.me/upload")
		.then()
			.log().all()
			.statusCode(200) //should 404
			.body("name", is("users.pdf"))
		;		
	}
}
 