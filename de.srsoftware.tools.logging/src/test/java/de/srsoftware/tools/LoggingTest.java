/* © SRSoftware 2025 */
package de.srsoftware.tools;
import static de.srsoftware.tools.ConsoleColors.RED;
import static de.srsoftware.tools.ConsoleColors.RESET;
import static java.lang.System.Logger;
import static java.lang.System.Logger.Level.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class LoggingTest {
	private static final Logger LOG = System.getLogger(LoggingTest.class.getSimpleName());
	private static PrintStream backup;


	@BeforeAll
	public static void prepare(){
		backup = System.out;
		LOG.log(ERROR,"Flush first line");
	}

	@AfterAll
	public static void cleanup(){
		System.setOut(backup);
	}

	@Test
	public void doTest() {
		LOG.log(ERROR, "This is an error message");
		LOG.log(WARNING, "This is a warning");
		LOG.log(INFO, "This is an informational message");
		LOG.log(DEBUG, "You may debug this code");
		LOG.log(TRACE, "Don`t you care about this!");
	}

	@Test
	public void testErrorNoMark() throws IOException {
		var bos = new ByteArrayOutputStream();
		System.setOut(new PrintStream(bos));
		LOG.log(ERROR,"Test");
		System.out.flush();
		var content = bos.toString(UTF_8);
		bos.close();
		assertTrue(content.contains("["+this.getClass().getSimpleName()+"]:"));
		content = content.split(": ")[1];
		assertEquals(RED+"Test"+RESET+"\n",content);
	}

	@Test
	public void testIndexedMarks() throws IOException {
		var bos = new ByteArrayOutputStream();
		System.setOut(new PrintStream(bos));
		LOG.log(ERROR,"{0}-{1}","A","B");
		System.out.flush();
		var content = bos.toString(UTF_8);
		bos.close();
		assertTrue(content.contains("["+this.getClass().getSimpleName()+"]:"));
		content = content.split(": ")[1];
		assertEquals(RED+"A-B"+RESET+"\n",content);
	}

	@Test
	public void testMarksWithoutIndex() throws IOException {
		var bos = new ByteArrayOutputStream();
		System.setOut(new PrintStream(bos));
		LOG.log(ERROR,"{}-{}","A","B");
		System.out.flush();
		var content = bos.toString(UTF_8);
		bos.close();
		assertTrue(content.contains("["+this.getClass().getSimpleName()+"]:"));
		content = content.split(": ")[1];
		assertEquals(RED+"A-B"+RESET+"\n",content);
	}

}
