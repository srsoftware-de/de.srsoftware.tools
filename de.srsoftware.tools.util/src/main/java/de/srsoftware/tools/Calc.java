/* © SRSoftware 2024 */
package de.srsoftware.tools;

import static java.lang.System.Logger.Level.ERROR;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

/**
 * collection of tiny algorithms
 */
public class Calc {
	private static final System.Logger LOG    = System.getLogger(Calc.class.getSimpleName());
	private static final MessageDigest SHA256 = getSha256();

	private Calc() {
	}

	private static MessageDigest getSha256() {
		try {
			return MessageDigest.getInstance("SHA256");
		} catch (final NoSuchAlgorithmException e) {
			LOG.log(ERROR, "Failed to get SHA256 digest object! [{0}]", Calc.class.getPackageName());
			System.exit(1);
			return null;
		}
	}

	/**
	 * calculate the grates common divisor
	 * @param a first number
	 * @param b second number
	 * @return greatest common divisor of a and b
	 */
	public static long ggt(long a, long b) {
		if (a == 0 || b == 0) {
			return 0;
		}

		while (a != b) {
			if (a > b) {
				a = a - b;
			} else {
				b = b - a;
			}
		}

		return a;
	}

	/**
	 * calculate the sha256 hash of the objects string representation
	 * @param o the object to hash
	 * @return the hash value represented as byte array string
	 */
	public static Optional<byte[]> sha256(final Object o) {
		return Optional	 //
		    .ofNullable(o)
		    .map(Object::toString)
		    .map(s -> s.getBytes(UTF_8))
		    .map(SHA256::digest);
	}
}
