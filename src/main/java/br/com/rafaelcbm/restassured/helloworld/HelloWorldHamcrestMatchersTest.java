package br.com.rafaelcbm.restassured.helloworld;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;
import org.hamcrest.junit.MatcherAssert;
import org.junit.Test;

public class HelloWorldHamcrestMatchersTest {
	
	@Test
	public void helloWorldRestAssured() {
		
		MatcherAssert.assertThat("Maria", Matchers.is("Maria"));
		MatcherAssert.assertThat(128, Matchers.is(128));
		MatcherAssert.assertThat(128, Matchers.isA(Integer.class));
		MatcherAssert.assertThat(128d, Matchers.greaterThan(127d));
		MatcherAssert.assertThat(128d, Matchers.lessThan(129d));
		
		
		List<Integer> oddNumbers = Arrays.asList(1,3,5,7,9);
		MatcherAssert.assertThat(oddNumbers, Matchers.hasSize(5));
		MatcherAssert.assertThat(oddNumbers, Matchers.contains(1,3,5,7,9));
		MatcherAssert.assertThat(oddNumbers, Matchers.containsInAnyOrder(3,1,5,7,9));
		MatcherAssert.assertThat(oddNumbers, Matchers.hasItem(3));
		MatcherAssert.assertThat(oddNumbers, Matchers.hasItems(3,7));
		
		MatcherAssert.assertThat("Maria", Matchers.is(Matchers.not("John")));
		MatcherAssert.assertThat("Maria", Matchers.not("John"));
		// Same, with import static
		assertThat("Maria", not("John"));
		
		assertThat("Maria", anyOf(is("John"), is("Maria")));
		assertThat("Maria", allOf(startsWith("Ma"), endsWith("ia"), containsString("r")));
	}
}
