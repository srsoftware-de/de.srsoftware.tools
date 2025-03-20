/* © SRSoftware 2025 */
package de.srsoftware.tools;

/**
 * A haser for passwords
 * @param <T> The representation of the password, e.g. String, byte array, bigint…
 */
public interface PasswordHasher<T> {
	/**
	 * hashes a password using a second input as salt
	 * @param password the password to process
	 * @param salt the salt added to the password
	 * @return the hased password
	 */
	public String hash(String password, String salt);

	/**
	 * return the salt part of a given hashed password
	 * @param hashedPassword the hashed password to tokenize
	 * @return the salt extracted from the password
	 */
	public String salt(String hashedPassword);

	/**
	 * test, whether a given plain text password matches a hashed password
	 * @param plaintextPassword the plain text password to verify
	 * @param hashedPassword the password hash to verigy against
	 * @return true, only if the plain text password corresponds with the hashed password
	 */
	public default boolean matches(String plaintextPassword, String hashedPassword) {
		return hash(plaintextPassword, salt(hashedPassword)).equals(hashedPassword);
	}
}
