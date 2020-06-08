package br.com.rafaelcbm.restassured.helloworld;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.internal.path.xml.NodeImpl;

public class UsersXmlTest {
	
	@BeforeClass
	public static void setup() {
		RestAssured.baseURI="http://restapi.wcaquino.me";
//		RestAssured.port="443";
//		RestAssured.basePath=""; 
	}
	
	@Test
	public void workingWithXmlTest() {
		given()
		.when()
			.get("/usersXML/3")
		.then()
			.statusCode(200)
			.body("user.name", is("Ana Julia"))
			// xml attribute
			.body("user.@id", is("3"))
			.body("user.filhos.name.size()", is(2))
			.body("user.filhos.name[0]", is("Zezinho"))
			.body("user.filhos.name[1]", is("Luizinho"))
			.body("user.filhos.name", hasItem("Luizinho"))
			.body("user.filhos.name", hasItems("Zezinho", "Luizinho"))
			;
	}
	
	@Test
	public void workingWithXmlUsingRootPathTest() {
		given()
		.when()
			.get("/usersXML/3")
		.then()
			.statusCode(200)
			
			// Adding root path
			.rootPath("user")
			.body("name", is("Ana Julia"))
			.body("@id", is("3"))
			
			// changing root path
			.rootPath("user.filhos")
			.body("name.size()", is(2))
			
			// detaching root path
			.detachRootPath("filhos")
			.body("filhos.name[0]", is("Zezinho"))
			.body("filhos.name", hasItem("Luizinho"))
			
			// ataching root path
			.appendRootPath("filhos")
			.body("name[1]", is("Luizinho"))
			.body("name", hasItems("Zezinho", "Luizinho"))
			;
	}
	
	@Test
	public void shouldDoAdvancedXmlSearchTest() {
		given()
		.when()
			.get("/usersXML")
		.then()
			.statusCode(200)			
			.body("users.user.size()", is(3))
			.body("users.user.findAll{it.age.toInteger() <= 25}.size()", is(2))
			.body("users.user.@id", hasItems("1", "2", "3"))
			.body("users.user.find{it.age == 25}.name", is("Maria Joaquina"))
			.body("users.user.findAll{it.name.toString().contains('n')}.name", hasItems("Maria Joaquina", "Ana Julia"))
			.body("users.user.salary.find{it != null}.toDouble()", is(1234.5678d))
			.body("users.user.age.collect{it.toInteger() * 2}", hasItems(40, 50, 60))
			.body("users.user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA"))
			;
	}
	
	@Test
	public void shouldDoAdvancedXmlSearchWithJavaTest() {
		
		ArrayList<NodeImpl> names =
		given()
		.when()
			.get("/usersXML")
		.then()
			.statusCode(200)			
			.extract()
			.path("users.user.name.findAll{it.toString().contains('n')}")
			;
		
		Assert.assertEquals(2, names.size());
		Assert.assertEquals("Maria Joaquina".toUpperCase(), names.get(0).toString().toUpperCase());
		Assert.assertTrue("ANA JULIA".equalsIgnoreCase(names.get(1).toString()));
	}
	
	//XPath
	@Test
	public void shouldWorkWithXPathTest() {
		
		given()
		.when()
			.get("/usersXML")
		.then()
			.statusCode(200)
			.body(hasXPath("count(/users/user)", is("3")))			
			.body(hasXPath("/users/user[@id = '1']"))			
			.body(hasXPath("//user[@id = '2']"))
			// From filhos to mother
			.body(hasXPath("//name[text() = 'Luizinho']/../../name", is("Ana Julia")))
			// From mother to filhos
			.body(hasXPath("//name[text() = 'Ana Julia']/following-sibling::filhos", allOf(containsString("Zezinho"), containsString("Luizinho"))))			
			// First user name
			.body(hasXPath("/users/user/name", is("João da Silva")))			
			.body(hasXPath("//name", is("João da Silva")))
			// Second user
			.body(hasXPath("/users/user[2]/name", is("Maria Joaquina")))
			// Last user
			.body(hasXPath("/users/user[last()]/name", is("Ana Julia")))
			.body(hasXPath("count(/users/user/name[contains(., 'n')])", is("2")))			
			.body(hasXPath("//user[age < 24]/name", is("Ana Julia")))			
			.body(hasXPath("//user[age > 20 and age < 30]/name", is("Maria Joaquina")))			
			.body(hasXPath("//user[age > 20][age < 30]/name", is("Maria Joaquina")))			
			;
	}

	
}
