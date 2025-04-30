/* © SRSoftware 2025 */
package de.srsoftware.tools;

import static java.lang.System.Logger.Level.INFO;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.empty;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class provides method to decode and encode URL query parameters
 */
public class Query {
	private static final Pattern ARRAY_PATTERN = Pattern.compile("([^\\[]+)(\\[(.*?)\\])*");
	private static final System.Logger LOG = System.getLogger(Query.class.getSimpleName());

	private Query(){
		// disable object creation
	}

	/**
	 * Transform an encoded query string into a map
	 * @param query serialized URL-encoded data
	 * @return a map of the decoded data
	 */
	public static Map<String,Object> decode(String query){
		var map = new LinkedHashMap<String,Object>();
		if (query == null) return map;
		var parts = query.split("&");
		for (var part : parts){
			var keyVal = part.split("=",2);
			var key = URLDecoder.decode(keyVal[0], UTF_8);
			var val = URLDecoder.decode(keyVal[1], UTF_8);
			insertInto(map,key,val);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	private static void insertInto(HashMap<String, Object> map, String key, String value) {
		LOG.log(INFO,"key = {0}",key);
		var parts = key.split("\\[|]\\[|]");
		Map<String, Object> current = map;
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].isEmpty()) continue;
			if (i == parts.length - 1) {
				// Letztes Element: Wert setzen
				Object existing = current.get(parts[i]);
				if (existing == null) {
					current.put(parts[i], value);
				} else if (existing instanceof List) {
					((List<String>) existing).add(value);
				} else {
					List<String> list = new ArrayList<>();
					list.add((String) existing);
					list.add(value);
					current.put(parts[i], list);
				}
			} else {
				// Verschachtelung aufbauen
				Object next = current.get(parts[i]);
				if (!(next instanceof Map)) {
					next = new LinkedHashMap<String, Object>();
					current.put(parts[i], next);
				}
				current = (Map<String, Object>) next;
			}
		}
	}

	/**
	 * Transform a Map to a query string
	 * @param query the data to serialize
	 * @return the serialized, URL-safe representation of the input data
	 */
	public static Optional<String> encode(Map<String,?> query){
		if (query == null) return empty();
		var sb = new StringBuilder();
		for (var entry : query.entrySet()){
			if (!sb.isEmpty()) sb.append("&");
			var key = URLEncoder.encode(entry.getKey(), UTF_8);
			var val = entry.getValue();
			if (val instanceof String stringValue) {
				stringValue = URLEncoder.encode(stringValue, UTF_8);
				sb.append(key).append("=").append(stringValue);
			}
			if (val instanceof Collection<?> list){
				var listString = list.stream().map(o -> key+"="+o).collect(Collectors.joining("&"));
				sb.append(listString);
			}
			if (val instanceof Map<?,?> map) {
				var pathList = traverse(map).stream().map(o -> key+o).collect(Collectors.joining("&"));
				sb.append(pathList);
			}
		}
		return Optional.of(sb.toString());
	}

	private static List<String> traverse(Map<?,?> map){
		var list = new ArrayList<String>();
		for (var entry : map.entrySet()){
			var key = "["+URLEncoder.encode(entry.getKey().toString(), UTF_8)+"]";
			var val = entry.getValue();
			if (val instanceof Map<?,?> nested) {
				traverse(nested).forEach(o -> list.add(key+o));
			} else {
				list.add(key+"="+URLEncoder.encode(val.toString(), UTF_8));
			}
		}
		return list;
	}
}
