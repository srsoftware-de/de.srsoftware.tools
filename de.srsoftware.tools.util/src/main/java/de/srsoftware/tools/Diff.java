package de.srsoftware.tools;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;

public class Diff {
	public static class LineDiff{
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
	}
	public static class MapDiff{
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
					lines.add(key+": "+LineDiff.diff(r.split("\n"), u.split("\n")));
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
