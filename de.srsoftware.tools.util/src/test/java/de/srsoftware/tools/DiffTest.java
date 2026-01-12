package de.srsoftware.tools;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiffTest {

	@Test
	public void testEmptyMaps(){
		var expected = "";
		assertEquals(expected,Diff.MapDiff.diff(Map.of(),Map.of()));
	}

	@Test
	public void leftMapEmpty(){
		var expected = "+ key1\n+ key2";
		assertEquals(expected,Diff.MapDiff.diff(Map.of(),Map.of("key1","value1","key2","value2")));
	}

	@Test
	public void rightMapEmpty(){
		var expected = "- key1\n- key2";
		assertEquals(expected,Diff.MapDiff.diff(Map.of("key1","value1","key2","value2"),Map.of()));
	}

	@Test
	public  void mapReplaced(){
		var expected = "- key1\n+ key2";
		assertEquals(expected,Diff.MapDiff.diff(Map.of("key1","value1"),Map.of("key2","value2")));
	}

	@Test
	public void nestedMap(){
		var expected = "";
		var a = Map.of("title","this contains maps","map",Map.of("key1","value1","key2", "value2"));
		var b = Map.of("title","this contains maps","map",Map.of("key1","value1","key2", "value2"));
		assertEquals(expected, Diff.MapDiff.diff(a,b));

		expected = "- map.key1\n+ map.key2";
		a = Map.of("title","this contains maps","map",Map.of("key1","value1"));
		b = Map.of("title","this contains maps","map",Map.of("key2", "value2"));
		assertEquals(expected, Diff.MapDiff.diff(a,b));
	}

	@Test
	public void testText(){
		var a = """
				Line 1
				Line 2
				Line 3
				Line 4
				Line 5				
				Line 6
				""";
		var b = """
				Line 1
				Line B
				Line C
				Line 4
				Line 5
				Line E
				Line 6
				Line G
				""";
		var expected = """
				…
				- Line 2
				- Line 3
				+ Line B
				+ Line C
				…
				+ Line E
				…
				+ Line G
				""".trim();
		var diff = Diff.LineDiff.diff(a.split("\n"),b.split("\n"));
		assertEquals(expected,diff);
	}
}
