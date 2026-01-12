package de.srsoftware.tools;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;

/**
 * This class provides means to create textual diff descriptions for several object types
 */
public class Diff {

	/**
	 * Creates textual diffs of multi-line texts or arrays of lines
	 */
	public static class LineDiff{

		/**
		 * create a textual representation of two arrays of strings
		 * @param reference a reference text passed as array of strings
		 * @param update text that will be compared against the reference, provided as array of strings
		 * @return the textual diff
		 */
		public static String diff(String[] reference, String[] update) {
			var diff = new ArrayList<String>();
			var matchBefore = 0;
			for (var r = 0; r<reference.length; r++){
				var refLine = reference[r];
				var match = false;
				for (var u = matchBefore; u<update.length; u++){
					var updLine = update[u];
					if (refLine.equals(updLine)) {
						match = true;
						for (var i = matchBefore; i<u; i++) diff.add("+ "+update[i]);
						matchBefore=u+1;
						if (diff.isEmpty() || !"…".equals(diff.getLast())) diff.add("…");
						break;
					}
				}
				if (!match) diff.add("- "+refLine);
			}
			for (var i = matchBefore; i<update.length; i++) diff.add("+ "+update[i]);
			return String.join("\n",diff);
		}

		/**
		 * create a textual representation of two (multi-line) strings
		 * @param reference a reference text
		 * @param update text that will be compared against the reference
		 * @return the textual diff
		 */
		public static String diff(String reference, String update){
			return diff(reference.split("\n"),update.split("\n"));
		}
	}

	/**
	 * this class provides methods to generate a textual diff of two maps
	 */
	public static class MapDiff{

		/**
		 * create a textual diff of two maps
		 * @param reference first map, against the second map will be compared
		 * @param update second map, compared against the reference map
		 * @return the textual diff
		 */
		public static String diff(Map<String, Object> reference, Map<String, Object> update) {
			var lines = new ArrayList<String>();
			diff("", lines, reference, update);
			return String.join("\n",lines);
		}
		private static void diff(String prefix, ArrayList<String> lines, Map<String, Object> reference, Map<String, Object> update) {
			var keys = new TreeSet<String>(){{
				addAll(reference.keySet());
				addAll(update.keySet());
			}};
			for (var key : keys){
				var prefixed = prefix+key;
				if (!update.containsKey(key)){
					lines.add("- "+prefixed);
					continue;
				}
				if (!reference.containsKey(key)){
					lines.add("+ "+prefixed);
					continue;
				}
				// present in reference and update
				var refVal = reference.get(key);
				var updVal = update.get(key);
				if (refVal == null) {
					if (updVal != null) lines.add(prefixed+": +"+updVal);
					continue;
				}
				if (updVal == null && refVal != null){
					lines.add(prefixed+": -"+refVal);
					continue;
				}
				// both non-null
				if (refVal.equals(updVal)) continue;
				// different
				if (refVal instanceof String r && updVal instanceof String u){
					lines.add(key+": "+LineDiff.diff(r, u));
					continue;
				}
				if (refVal instanceof Map<?,?> && updVal instanceof Map<?,?>){
					diff(prefixed+".",lines, (Map<String, Object>) refVal, (Map<String, Object>) updVal);
					continue;
				}
				lines.add(key+": "+refVal+" → "+updVal);
			}

		}
	}
}
