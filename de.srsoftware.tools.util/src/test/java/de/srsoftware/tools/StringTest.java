/* © SRSoftware 2025 */
package de.srsoftware.tools;

import static de.srsoftware.tools.Strings.camelCase;
import static de.srsoftware.tools.Strings.snakeCase;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StringTest {
	@Test
	public void testCamelCase() {
		var orig   = "Dies ist ein  ganz  normaler Satz.";
		var mapped = camelCase(orig);
		assertEquals("DiesIstEinGanzNormalerSatz.", mapped);
	}

	@Test
	public void testSnakeCaseToCamelCase() {
		var orig   = "my_secret_var";
		var mapped = camelCase(orig);
		assertEquals("mySecretVar", mapped);
	}

	@Test void testCamelCaseToSnakeCase(){
		var orig = "testCamelCaseToSnakeCase";
		var expected = "test_camel_case_to_snake_case";
		assertEquals(expected, snakeCase(orig));
	}

	@Test void testCaseToSnakeCase(){
		var orig   = "Dies ist ein  ganz  normaler Satz.";
		var mapped = snakeCase(orig);
		assertEquals("dies_ist_ein_ganz_normaler_satz.", mapped);
	}

}
