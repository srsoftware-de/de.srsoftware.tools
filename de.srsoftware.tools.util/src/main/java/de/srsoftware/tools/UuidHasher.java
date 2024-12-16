/* © SRSoftware 2024 */
package de.srsoftware.tools;

import static de.srsoftware.tools.Strings.hex;
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
}
