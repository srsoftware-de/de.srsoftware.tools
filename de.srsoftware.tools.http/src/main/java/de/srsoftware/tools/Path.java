/* © SRSoftware 2025 */
package de.srsoftware.tools;

import static java.net.URLDecoder.decode;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Stack;

/**
 * This class represents a path given in an http request as stack of strings.
 */
public class Path extends Stack<String> {

	private Path(){
		// discourage constructor
	}

	/**
	 * checks whether this path ends with the given string
	 * @param s the string to compare to
	 * @return true, only if the last component of this path stack ends with the given string
	 */
	public boolean endsWith(String s){
		return get(size()-1).endsWith(s);
	}

	/**
	 * checks, whether this path equals the given string
	 * @param s the string to compare against
	 * @return true only if the path has exactly one component and that component matches s
	 */
	public boolean equals(String s){
		return size() == 1 && get(0).equals(s);
	}

	/**
	 * create a new String stack from a given path
	 * @param requestPath the path to parse into the stack
	 * @return the stack consisting of the slash-separated tokens of the input
	 */
	public static Path of(String requestPath) {
		var path = new Path();
		if (requestPath == null || requestPath.isBlank()) return  path;
		var parts = requestPath.split("/");
		for (int i=parts.length-1; i>-1; i--) {
			if (i>0 || !parts[i].isBlank())	path.push(parts[i]);
		}
		return path;
	}

	@Override
	public synchronized String pop() {
		if (isEmpty()) return null;
		return decode(super.pop(), UTF_8);
	}

	@Override
	public synchronized String peek() {
		if (isEmpty()) return null;
		return decode(super.peek(), UTF_8);
	}

	@Override
	public synchronized String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = size(); i>0; i--) sb.append("/").append(get(i-1));
		return sb.isEmpty() ? "" : sb.substring(1);
	}
}
