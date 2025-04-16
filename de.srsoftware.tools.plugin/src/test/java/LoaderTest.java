/* © SRSoftware 2025 */
import static org.junit.jupiter.api.Assertions.*;

import de.srsoftware.tools.plugin.ClassListener;
import de.srsoftware.tools.plugin.JarWatchdog;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class LoaderTest {

	@Test
	public void testScan(){
		var foundClasses = new HashSet<String>();
		var dir = new File("build/libs");
		var listener = new ClassListener(){
			@Override
			public void classAdded(Class<?> clazz) {
				foundClasses.add(clazz.getSimpleName());
			}

			@Override
			public void classRemoved(Class<?> clazz) {

			}
		};
		new JarWatchdog().addDirectory(dir).addListener(listener).scan();
		assertEquals(Set.of(JarWatchdog.class.getSimpleName(),ClassListener.class.getSimpleName()),foundClasses);
	}

	@Test
	public void testLoadResource(){
		List<Object> list = new ArrayList<>();
		var dir = new File("build/libs");
		var listener = new ClassListener(){
			@Override
			public void classAdded(Class<?> clazz) {
				if (clazz.getSimpleName().equals(JarWatchdog.class.getSimpleName())) {
					try {
						list.add(clazz.getDeclaredConstructor().newInstance());
					} catch (Exception ignored){

					}
				}
			}

			@Override
			public void classRemoved(Class<?> clazz) {

			}
		};
		new JarWatchdog().addDirectory(dir).addListener(listener).scan();
		assertFalse(list.isEmpty());
		Object instance = list.getFirst();
		var url = instance.getClass().getClassLoader().getResource("test.txt");
		assertNotNull(url);
	}



}
