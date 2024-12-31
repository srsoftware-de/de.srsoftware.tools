/* © SRSoftware 2024 */
package de.srsoftware.tools.plugin;

/**
 * Interface for Classes that need to be notified, when classes are discovered or removed
 */
public interface ClassListener {

	/**
	 * this method is called, when a new class has been discovered
	 * @param clazz the discovered (and loaded) class
	 */
	public void classAdded(Class<?> clazz);

	/**
	 * this method is called, when a class has been remoced
	 * @param clazz the removed class
	 */
	public void classRemoved(Class<?> clazz);
}
