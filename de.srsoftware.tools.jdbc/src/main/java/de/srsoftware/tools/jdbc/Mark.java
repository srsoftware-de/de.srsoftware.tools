/* © SRSoftware 2024 */
package de.srsoftware.tools.jdbc;

/**
 * helper class for creating Update queries
 */
public class Mark {
	private Integer position = null;

	/**
	 *  create a new mark
	 */
	public Mark() {
	}

	/**
	 * return the position saved in this mark
	 * @return the value of the position field
	 */
	public int position() {
		return position;
	}

	/**
	 * derive a new Mark instance with a pre-set position value
	 * @param position the position value for the derived mark
	 * @return the derived mark
	 */
	public Mark set(int position) {
		var newMark	 = new Mark();
		newMark.position = position;
		return newMark;
	}
}