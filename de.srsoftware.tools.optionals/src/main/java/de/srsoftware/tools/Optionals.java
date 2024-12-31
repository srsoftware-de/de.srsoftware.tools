/* © SRSoftware 2024 */
package de.srsoftware.tools;

import static java.util.Optional.empty;

import java.math.BigInteger;
import java.util.*;

public class Optionals {
	private static final BigInteger ZERO = new BigInteger("0");

	private Optionals() {
	}

	public static boolean allEmpty(final Object... objects) {
		if (objects == null) return true;
		for (final Object o : objects) {
			if (isSet(o)) return false;
		}
		return true;
	}

	public static boolean allSet(final Object... objects) {
		for (final Object o : objects) {
			if (!isSet(o)) return false;
		}

		return true;
	}

	public static Optional<String> absentIfBlank(String text) {
		return text == null || text.isBlank() ? empty() : nullable(text.trim());
	}

	public static String emptyIfNull(final String text) {
		return text == null ? "" : text;
	}

	@SuppressWarnings("unchecked")
	public static <T> Optional<T> getPath(String key, Map<String, ?> map) {
		if (map == null) return empty();
		List<String> path = new ArrayList<>(List.of(key.split("\\.")));
		Object       o	  = null;
		try {
			while (!path.isEmpty()) {
				o = map.get(path.removeFirst());
				if (path.isEmpty()) return nullable((T)o);
				if (o instanceof Map<?, ?> inner) map = (Map<String, Object>)inner;
			}
		} catch (ClassCastException ignored) {
		}
		return empty();
	}

	public static Integer intOrNull(final String val) {
		try {
			return Integer.parseInt(val);
		} catch (final NumberFormatException nfe) {
			return null;
		}
	}

	public static boolean is0(final Object o) {
		if (o instanceof final Float f) return f.equals(0f);
		if (o instanceof final Double d) return d.equals(0d);
		if (o instanceof final Short s) return 0 == s;
		if (o instanceof final Integer i) return i.equals(0);
		if (o instanceof final Long l) return l.equals(0L);
		if (o instanceof final BigInteger b) return b.equals(ZERO);
		String s = o == null ? null : o.toString();
		if (s == null) return true;
		s = s.trim();
		return s.isEmpty() || "0".equals(s);
	}

	public static boolean isSet(final Object o) {
		if (o instanceof final String s) return !s.isBlank();
		return o != null;
	}

	public static List<Object> nonNull(final Object... elements) {
		return Arrays.stream(elements).filter(Objects::nonNull).toList();
	}

	public static <T> Optional<T> nullable(T payload) {
		return Optional.ofNullable(payload);
	}

	public static String nullIfEmpty(String s) {
		if (s == null) return null;
		s = s.trim();
		return s.isEmpty() ? null : s;
	}
}
