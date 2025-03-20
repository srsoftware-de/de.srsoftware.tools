/* © SRSoftware 2025 */
package de.srsoftware.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StringTest {
	@Test
	public void testCamelCase() {
		var orig   = "Dies ist ein  ganz  normaler Satz.";
		var mapped = Strings.camelCase(orig);
		assertEquals("DiesIstEinGanzNormalerSatz.", mapped);
	}
}
