/* © SRSoftware 2024 */
package de.srsoftware.tools.slf4j2syslog;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.spi.MDCAdapter;

/**
 * ServiceProvider implementation to accomodate SL4J injection
 */
public class ServiceProvider implements org.slf4j.spi.SLF4JServiceProvider{
	@Override
	public ILoggerFactory getLoggerFactory() {
		return new LoggerFactory();
	}

	@Override
	public IMarkerFactory getMarkerFactory() {
		return new BasicMarkerFactory();
	}

	@Override
	public MDCAdapter getMDCAdapter() {
		return new NOPMDCAdapter();
	}

	@Override
	public String getRequestedApiVersion() {
		return "2.0.16";
	}

	@Override
	public void initialize() {}
}
