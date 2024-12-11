/* © SRSoftware 2024 */
package de.srsoftware.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;

public class Optionals {

	@SuppressWarnings("unchecked")
	public static <T> Optional<T> getPath(String key, Map<String,?> map){
		if (map == null) return empty();
		List<String> path = new ArrayList<>(List.of(key.split("\\.")));
		Object o = null;
		try {
			while (!path.isEmpty()) {
				o = map.get(path.removeFirst());
				if (path.isEmpty()) return nullable((T) o);
				if (o instanceof Map<?, ?> inner) map = (Map<String, Object>) inner;
			}
		} catch (ClassCastException ignored){

		}
		return empty();
	}

	public static <T> Optional<T> nullable(T payload) {
		return Optional.ofNullable(payload);
	}
}
