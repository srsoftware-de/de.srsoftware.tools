/* © SRSoftware 2024 */
package de.srsoftware.tools.jdbc;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class Util {
	private static final MessageDigest SHA256 = getSha256();


	/**
	 * https://howtodoinjava.com/java8/stream-distinct-by-multiple-fields/
	 *
	 * @param <T>
	 * @param keyExtractor
	 * @return
	 */
	public static <T> Predicate<T> distinctByKey(final Function<? super T, Object> keyExtractor) {
		final var seen = new ConcurrentHashMap<Object, Boolean>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), true) == null;
	}

	private static MessageDigest getSha256() {
		try {
			return MessageDigest.getInstance("SHA256");
		} catch (final NoSuchAlgorithmException e) {
			System.err.println("Failed to get SHA256 digest object! [" + Util.class.getPackageName() + "]");
			System.exit(1);
			return null;
		}
	}

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

	public static String fill(final int indent, final String text, final Object... fills) {
		final StringBuilder sb = new StringBuilder(" ".repeat(indent));

		if (fills == null || fills.length < 1) return sb.append(text).toString();

		final var parts = text.split("\\{\\}", fills.length + 1);

		for (int i = 0; i < parts.length - 1; i++) {
			sb.append(parts[i]);
			sb.append(fills[i]);
		}

		sb.append(parts[parts.length - 1]);
		return sb.toString();
	}

	public static String fill(final String text, final Object... fills) {
		return fill(0, text, fills);
	}

	public static String hash(final Object o) {
		if (o == null) return null;
		final byte[] bytes = o.toString().getBytes(StandardCharsets.UTF_8);
		return hex(SHA256.digest(bytes));
	}

	public static String hex(final byte[] bytes) {
		final StringBuilder buf = new StringBuilder(bytes.length * 2);

		for (final var byt : bytes) buf.append(hex(byt));

		return buf.toString();
	}

	public static String hex(final int b) {
		final int lower = b & 0x0F;
		final int upper = (b & 0xF0) >> 4;
		return (char)(upper < 10 ? '0' + upper : 'A' + upper - 10) + "" + (char)(lower < 10 ? '0' + lower : 'A' + lower - 10);
	}


	public static boolean isFilled(final Collection<?> c) {
		return c != null && !c.isEmpty();
	}


	public static boolean equal(final Object a, final Object b) {
		if (a == b) return true;
		if (a == null) return false;
		return a.equals(b);
	}
}
