package io.smudgr.reflect;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;

public class Reflect {

	private Class<?> type;

	private Set<Class<?>> results = new HashSet<Class<?>>();

	public Reflect(Class<?> type) {
		this.type = type;
	}

	public Set<Class<?>> get() {
		long start = System.currentTimeMillis();

		try {
			searchPackage();
		} catch (IOException e) {
			e.printStackTrace();
		}

		long elapsed = System.currentTimeMillis() - start;
		System.out.println("Reflect found " + results.size() + " implementations of " + type.getTypeName() + " (" + elapsed + " ms)");

		return results;
	}

	private void searchPackage() throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Enumeration<URL> resources = classLoader.getResources("META-INF");

		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();

			if (resource.toString().startsWith("jar:file:")) {
				searchJarFile(resource, "");
				continue;
			}

			File file = new File(URLDecoder.decode(resource.getFile(), "UTF-8"));
			if (file.exists() && file.isDirectory())
				searchDirectory_r(file, "");
		}

		String[] classPaths = System.getProperty("java.class.path").split(";");
		Set<String> classPathSet = new HashSet<String>(Arrays.asList(classPaths));

		for (String string : classPathSet) {
			File file = new File(string);

			if (file.exists() && file.isFile() && file.getName().endsWith(".jar")) {
				URL jarUrl = file.toURI().toURL();

				if (jarUrl != null)
					searchJarFile(jarUrl, "");
			}
		}
	}

	private void searchDirectory_r(File directory, String packageName) {
		String packagePrefix = packageName.isEmpty() ? "" : (packageName + ".");

		if (directory.exists()) {
			File[] files = directory.listFiles();

			for (File file : files) {
				String filename = file.getName();

				if (file.isDirectory()) {
					searchDirectory_r(file, packagePrefix + filename);
					continue;
				}

				if (filename.endsWith(".class")) {
					try {
						add(Class.forName(packagePrefix + filename.substring(0, filename.length() - 6)));
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void searchJarFile(URL url, String packageName) throws UnsupportedEncodingException {
		String packagePathFilter = packageName.replace('.', '/');

		Enumeration<JarEntry> entries = new SmudgrJar(url).entries();

		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		while (entries.hasMoreElements()) {
			JarEntry jarEntry = entries.nextElement();
			String entryName = jarEntry.getName();

			boolean isDirectory = jarEntry.isDirectory();
			boolean isInPackage = entryName.startsWith(packagePathFilter) || packageName.isEmpty();
			boolean isClass = entryName.endsWith(".class");

			if (!isDirectory && isInPackage && isClass) {
				String className = jarEntry.getName().replaceFirst("\\.class$", "").replace("/", ".");

				try {
					add(Class.forName(className, false, contextClassLoader));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void add(Class<?> c) {
		if (!type.isAssignableFrom(c))
			return;

		int mods = c.getModifiers();
		if (Modifier.isAbstract(mods) || Modifier.isInterface(mods))
			return;

		results.add(c);
	}

}