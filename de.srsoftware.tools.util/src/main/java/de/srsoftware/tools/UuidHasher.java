/* © SRSoftware 2024 */
package de.srsoftware.tools;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * A password hasher that uses uuids as salt
 */
public class UuidHasher implements PasswordHasher<String> {
	private static final String SHA256 = "SHA-256";

	private final MessageDigest digest;

	/**
	 * Create a new instance
	 * @throws NoSuchAlgorithmException if SHA256 cannot be instantiated
	 */
	public UuidHasher() throws NoSuchAlgorithmException {
		digest = MessageDigest.getInstance(SHA256);
	}

	@Override
	public String hash(String password, String uuid) {
		var salt       = uuid;
		var saltedPass = "%s %s".formatted(salt, password);
		var bytes      = digest.digest(saltedPass.getBytes(UTF_8));

		return "%s@%s".formatted(hex(bytes), salt);
	}

	@Override
	public String salt(String hashedPassword) {
		return hashedPassword.split("@", 2)[1];
	}

	/**
	 * get a hexadecimal representation of the byte array
	 * @param bytes a byte array to convert to a hex string
	 * @return a string containing a hexadecimal representation of the given byte array
	 */
	public static String hex(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) sb.append(String.format("%02x", b));
		return sb.toString();
	}
}
