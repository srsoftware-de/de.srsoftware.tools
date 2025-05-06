/* © SRSoftware 2025 */
package de.srsoftware.tools;

/**
 * Flags for name conversion when creating a Tag
 */
public enum NameConversion {
	/** don`t convert name **/
	NO_CONVERSION,

	/** convert name to snake case **/
	SNAKE_CASE,

	/** convert name to camel case **/
	CAMEL_CASE,

	/** convert name to lower case **/
	LOWER_CASE,

	/** convert name to upper case **/
	UPPER_CASE
}
