import de.srsoftware.tools.PathHandler;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryTest {
	@Test
	public void testQuerySplit(){
		var query = "token=00dcbed3-f7d3-4391-afac-4e3fa6a45e08&domain=http%3A%2F%2F192.168.1.11%3A8090%2Fwiki%2F";
		var map = PathHandler.querySplit(query);
		var expected = Map.of("token","00dcbed3-f7d3-4391-afac-4e3fa6a45e08","domain","http://192.168.1.11:8090/wiki/");
		assertEquals(expected,map);
	}
}
