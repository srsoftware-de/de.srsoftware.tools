/* © SRSoftware 2025 */
package de.srsoftware.tools.plugin;

import static java.lang.System.Logger.Level.*;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.time.Duration;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * This class observes selected directories for JAR files.
 * If a JAR file is found, it tries to load the contained classes.
 */
public class JarWatchdog extends Thread {
	private static final System.Logger LOGGER = System.getLogger(JarWatchdog.class.getSimpleName());

	private final Set<File> dirs	           = new HashSet<>();
	private Runnable afterScan,beforeScan;
	private ClassLoader     context	           = null;
	private Duration        delay	           = Duration.ofSeconds(30);
	private final Set<File> loadedFiles        = new HashSet<>();
	private final Set<ClassListener> listeners = new HashSet<>();
	private final Set<String> warned           = new HashSet<>();

	public JarWatchdog addDirectory(File dir) {
		dirs.add(dir);
		return this;
	}

	public JarWatchdog addListener(ClassListener listener) {
		listeners.add(listener);
		return this;
	}

	/**
	 * set a function to be launched after each scan run
	 * @param afterScan the function to run
	 * @return this object
	 */
	public JarWatchdog afterScan(Runnable afterScan){
		this.afterScan = afterScan;
		return this;
	}

	private void announce(Class<?> clazz) {
		listeners.forEach(listener -> listener.classAdded(clazz));
	}

	/**
	 * set a function to be launched before each scan run
	 * @param beforeScan the function to run
	 * @return this object
	 */
	public JarWatchdog beforeScan(Runnable beforeScan){
		this.beforeScan = beforeScan;
		return this;
	}

	public JarWatchdog dropDirectory(File dir) {
		dirs.remove(dir);
		return this;
	}

	public JarWatchdog dropListener(ClassListener listener) {
		listeners.remove(listener);
		return this;
	}

	public JarWatchdog frequency(Duration newDelay) {
		delay = newDelay;
		return this;
	}

	private Stream<Class<?>> loadClassesFrom(File jarFile) {
		var     filename = jarFile.getName();
		boolean silent	 = warned.contains(filename);
		if (loadedFiles.contains(jarFile)) {
			if (!silent) {
				LOGGER.log(DEBUG, "{0} already scanned.", jarFile);
				warned.add(filename);
			}
			return Stream.of();
		}
		List<Class<?>> classes = new ArrayList<>();
		try (var jar = new JarFile(jarFile); var loader = URLClassLoader.newInstance(new URL[] {new URI("jar:file:" + jarFile + "!/").toURL()}, context)) {
			var enumeration = jar.entries();
			while (enumeration.hasMoreElements()) {
				var entry = enumeration.nextElement();
				if (!silent) LOGGER.log(DEBUG, "Found entry: {0}", entry);
				if (entry.isDirectory() || !entry.getName().endsWith(".class")) continue;
				String className = entry.getName().substring(0, entry.getName().length() - 6).replace('/', '.');
				if (!silent) LOGGER.log(INFO, "Trying to load {0}…", className);
				Class<?> c = loader.loadClass(className);
				if (!silent) LOGGER.log(INFO, "{0} loaded.", c.getSimpleName());
				classes.add(c);
			}
			loadedFiles.add(jarFile);
			warned.remove(filename);
			return classes.stream();
		} catch (Throwable e) {
			if (!silent) {
				LOGGER.log(WARNING, "Failed to load classes from jar!");
				warned.add(filename);
			}
			return Stream.of();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				scan();
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	public void scan(){
		if (beforeScan != null) beforeScan.run();
		for (var dir : dirs) scan(dir);
		if (afterScan != null) afterScan.run();
	}

	private void scan(File dir) {
		File[] files;
		if (dir.exists() && dir.isDirectory() && (files = dir.listFiles()) != null) {
			for (var child : files) {
				if (child.isDirectory()) continue;
				if (!child.canRead()) continue;
				if (child.getName().endsWith(".jar")) loadClassesFrom(child).forEach(this::announce);
			}
		}
	}

	public JarWatchdog setContext(ClassLoader context) {
		this.context = context;
		return this;
	}
}
