package br.com.rafaelcbm.restassured.helloworld;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.hamcrest.junit.MatcherAssert;
import org.junit.Test;

public class FileTest {

	@Test
	public void shouldValidateUploadTest() {
		given().log().all().when().post("http://restapi.wcaquino.me/upload").then().log().all().statusCode(404) // should
																												// 404
				.body("error", is("Arquivo não enviado"));
	}

	@Test
	public void shouldUploadTest() {
		given().log().all().multiPart("arquivo", new File("src/main/resources/users.pdf")).when()
				.post("http://restapi.wcaquino.me/upload").then().log().all().statusCode(200) // should 404
				.body("name", is("users.pdf"));
	}

	@Test
	public void shouldNotUploadBigFileTest() {
		given().log().all().multiPart("arquivo", new File("src/main/resources/world-2mb.jpg")).when()
				.post("http://restapi.wcaquino.me/upload").then().log().all()
				// Time limit
				.time(lessThan(20000l))
				// 413 - Payload Too Large
				.statusCode(413);
	}

	@Test
	public void shouldDownloadFileTest() throws IOException {

		byte[] image = given().log().all().when().get("http://restapi.wcaquino.me/download").then().log().all()
				.statusCode(200).extract().asByteArray();

		File file = new File("src/main/resources/file.jpg");
		OutputStream out = new FileOutputStream(file);
		out.write(image);
		out.close();
		
		MatcherAssert.assertThat(Long.valueOf(image.length), lessThan(100000l));

	}
}
