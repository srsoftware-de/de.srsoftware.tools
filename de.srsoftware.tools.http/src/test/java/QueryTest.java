/* © SRSoftware 2025 */
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.srsoftware.tools.PathHandler;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class QueryTest {
	@Test
	public void testQuerySplit(){
		var query = "token=00dcbed3-f7d3-4391-afac-4e3fa6a45e08&domain=http%3A%2F%2F192.168.1.11%3A8090%2Fwiki%2F";
		var map = PathHandler.querySplit(query);
		var expected = Map.of("token","00dcbed3-f7d3-4391-afac-4e3fa6a45e08","domain","http://192.168.1.11:8090/wiki/");
		assertEquals(expected,map);
	}
}
