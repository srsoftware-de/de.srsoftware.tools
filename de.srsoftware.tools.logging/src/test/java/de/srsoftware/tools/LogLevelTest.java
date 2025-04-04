/* © SRSoftware 2025 */
package de.srsoftware.tools;

import static java.lang.System.Logger.Level.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LogLevelTest {

	@BeforeEach
	public void resetColorLogger() throws NoSuchFieldException, IllegalAccessException {
		ColorLogger.reset();
	}

	@Test
	public void testRootLevel(){
		var logger = new ColorLogger(this);
		ColorLogger.setRootLogLevel(WARNING);
		assertFalse(logger.isLoggable(TRACE));
		assertFalse(logger.isLoggable(DEBUG));
		assertFalse(logger.isLoggable(INFO));
		assertTrue(logger.isLoggable(WARNING));
		assertTrue(logger.isLoggable(ERROR));
	}

	@Test
	public void testInstanceLevel(){
		var logger = new ColorLogger(this);
		ColorLogger.setRootLogLevel(WARNING);
		ColorLogger.setLogLevel(this.getClass().getSimpleName(),INFO);
		assertFalse(logger.isLoggable(TRACE));
		assertFalse(logger.isLoggable(DEBUG));
		assertTrue(logger.isLoggable(INFO));
		assertTrue(logger.isLoggable(WARNING));
		assertTrue(logger.isLoggable(ERROR));
	}

	@Test
	public void testInstanceLevelNoMatch(){
		var logger = new ColorLogger(this);
		ColorLogger.setRootLogLevel(WARNING);
		ColorLogger.setLogLevel(this.getClass().getSimpleName()+"X",INFO); // no match
		assertFalse(logger.isLoggable(TRACE));
		assertFalse(logger.isLoggable(DEBUG));
		assertFalse(logger.isLoggable(INFO));
		assertTrue(logger.isLoggable(WARNING));
		assertTrue(logger.isLoggable(ERROR));
	}

	@Test
	public void testInstanceLevelPartialMatch(){
		var logger = new ColorLogger(this);
		ColorLogger.setRootLogLevel(WARNING);
		ColorLogger.setLogLevel(this.getClass().getSimpleName().substring(0,5),INFO); // no match
		assertFalse(logger.isLoggable(TRACE));
		assertFalse(logger.isLoggable(DEBUG));
		assertTrue(logger.isLoggable(INFO));
		assertTrue(logger.isLoggable(WARNING));
		assertTrue(logger.isLoggable(ERROR));
	}

	@Test
	public void testInstanceLevelExactMatch(){
		var logger = new ColorLogger(this);
		ColorLogger.setRootLogLevel(WARNING);
		ColorLogger.setLogLevel(this.getClass().getSimpleName(),DEBUG); // no match
		ColorLogger.setLogLevel(this.getClass().getSimpleName().substring(0,5),INFO); // no match
		assertFalse(logger.isLoggable(TRACE));
		assertTrue(logger.isLoggable(DEBUG));
		assertTrue(logger.isLoggable(INFO));
		assertTrue(logger.isLoggable(WARNING));
		assertTrue(logger.isLoggable(ERROR));
	}

}
