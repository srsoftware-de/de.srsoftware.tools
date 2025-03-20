/* © SRSoftware 2025 */
package de.srsoftware.tools;
import static java.lang.System.Logger;
import static java.lang.System.Logger.Level.*;

import org.junit.jupiter.api.Test;

public class LoggingTest {
	private static final Logger LOG = System.getLogger(LoggingTest.class.getSimpleName());

	@Test
	public void doTest() {
		LOG.log(ERROR, "This is an error message");
		LOG.log(WARNING, "This is a warning");
		LOG.log(INFO, "This is an informational message");
		LOG.log(DEBUG, "You may debug this code");
		LOG.log(TRACE, "Don`t you care about this!");
	}
}
