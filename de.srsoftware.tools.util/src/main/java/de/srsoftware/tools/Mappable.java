/* © SRSoftware 2025 */
package de.srsoftware.tools;

import java.util.Map;

/**
 * an interface for components that can be marshalled into a map
 */
public interface Mappable {
	/**
	 * create a map of this object
	 * @return a map representing the contents of this object
	 */
	public Map<String,Object> toMap();
}