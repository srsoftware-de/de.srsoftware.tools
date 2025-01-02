/* © SRSoftware 2024 */
package de.srsoftware.tools.sandbox;

public class LoggerTest {
	private static final System.Logger LOG = System.getLogger(LoggerTest.class.getSimpleName());
	public static void main(String[] args) {
		LOG.log(System.Logger.Level.WARNING,"Test");
	}
}
