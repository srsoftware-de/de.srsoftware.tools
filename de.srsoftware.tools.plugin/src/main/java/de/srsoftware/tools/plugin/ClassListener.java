/* © SRSoftware 2024 */
package de.srsoftware.tools.plugin;

public interface ClassListener {
	public void classAdded(Class<?> clazz);
	public void classRemoved(Class<?> clazz);
}
