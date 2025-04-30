/* © SRSoftware 2025 */
import static de.srsoftware.tools.Query.decode;
import static de.srsoftware.tools.Query.encode;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class QueryTest {
	@Test
	public void testDecodeSimple(){
		var query = "first=1&second=two&third=three";
		var map = decode(query);
		var expected = Map.of("first","1","second","two","third","three");
		assertEquals(expected,map);
	}

	@Test
	public void testEncodeSimple(){
		var query = new LinkedHashMap<String,Object>();
		query.put("first","1");
		query.put("second","two");
		query.put("third","three");
		var string = encode(query).orElse(null);
		var expected = "first=1&second=two&third=three";
		assertEquals(expected,string);
	}

	@Test
	public void testEncodeList(){
		var query = new LinkedHashMap<String,Object>();
		query.put("first","primitive");
		query.put("second",List.of("A","B","C"));
		var string = encode(query).orElse(null);
		var expected = "first=primitive&second=A&second=B&second=C";
		assertEquals(expected,string);
	}

	@Test
	public void testEncodeMap(){
		var query = new LinkedHashMap<String,Object>();
		var mapA = new LinkedHashMap<String,String>();
		mapA.put("A","AA");
		mapA.put("B","AB");
		var mapB = new LinkedHashMap<String,String>();
		mapB.put("A","BA");
		mapB.put("B","BB");
		var second = new LinkedHashMap<String,Object>();
		second.put("A",mapA);
		second.put("B",mapB);

		query.put("first","primitive");
		query.put("second",second);
		var string = encode(query).orElse(null);
		var expected = "first=primitive&second[A][A]=AA&second[A][B]=AB&second[B][A]=BA&second[B][B]=BB";
		assertEquals(expected,string);
	}

	@Test
	public void testDecodeEscaped(){
		var query = "token=00dcbed3-f7d3-4391-afac-4e3fa6a45e08&domain=http%3A%2F%2F192.168.1.11%3A8090%2Fwiki%2F";
		var map = decode(query);
		var expected = Map.of("token","00dcbed3-f7d3-4391-afac-4e3fa6a45e08","domain","http://192.168.1.11:8090/wiki/");
		assertEquals(expected,map);
	}

	@Test
	public void testDecodeSimpleArray(){
		var query = "array=first&type=primitive&array=second&array=third";
		var map = decode(query);
		var expected = Map.of("type","primitive","array", List.of("first","second","third"));
		assertEquals(expected,map);
	}


	@Test
	public void testDecodeNestedArray(){
		var query = "type=primitive&array[A][A]=AA&array[A][B]=AB&array[B][A]=BA&array[B][B]=BB";
		var map = decode(query);
		var expected = Map.of("type","primitive","array", Map.of("A",Map.of("A","AA","B","AB"),"B",Map.of("A","BA","B","BB")));
		assertEquals(expected,map);
	}


}
