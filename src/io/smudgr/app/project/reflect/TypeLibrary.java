/**
 *
 */
package io.smudgr.app.project.reflect;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;

import io.smudgr.util.SmudgrJar;

/**
 * A {@link TypeLibrary} provides the ability to detect and organize all loaded
 * concrete implementations of a given {@link ReflectableType}
 *
 * @param <T>
 *            The {@link ReflectableType} this Library should manage
 */
public class TypeLibrary<T extends ReflectableType> {

	/*
	 * Adding to this list improves speed of reflection by ignoring libraries
	 * used by smudgr that will never contain ReflectableType's
	 */
	private static final String[] blacklist = new String[] { "gluegen-rt.jar", "jcodec-0.1.9.jar", "jcodec-javase-0.1.9.jar", "jogl-all.jar", "trove.jar" };

	private Class<T> type;
	private String typeName = null;
	private String typeIdentifier = null;

	private HashMap<String, Class<T>> implementations = new HashMap<String, Class<T>>();
	private HashMap<String, String> idToName = new HashMap<String, String>();

	/**
	 * Instantiate a new {@link TypeLibrary} to detect and catalog every
	 * implementation of the given type.
	 *
	 * @param type
	 *            {@link Class} of the type to organize
	 */
	public TypeLibrary(Class<T> type) {
		this.type = type;
		load();
	}

	/**
	 * Create a new instance of the {@link ReflectableType} this
	 * {@link TypeLibrary} represents, using the given implementation
	 * identifier.
	 *
	 * @param identifier
	 *            The identifier returned by
	 *            {@link ReflectableType#getIdentifier()}
	 * @return A new instance of the given identifier, or {@code null} if it
	 *         can't be found or instantiated.
	 */
	public T getNewInstance(String identifier) {
		try {
			return implementations.get(identifier).newInstance();
		} catch (NullPointerException | InstantiationException | IllegalAccessException e) {
			System.out.println("Failed to instantiate a " + typeName + ":" + typeIdentifier);
			return null;
		}
	}

	/**
	 * Gets a list of identifiers for every enumerated implementation.
	 *
	 * @return List of implementation identifiers
	 * @see #getNewInstance(String) getNewInstance(identifier)
	 * @see #getNameById(String) getNameById(id)
	 */
	public Set<String> getIdList() {
		return implementations.keySet();
	}

	/**
	 * Gets a list of names for every enumerated implementation.
	 *
	 * @return List of implementation names
	 * @see #getNewInstance(String) getNewInstance(identifier)
	 */
	public Set<String> getNameList() {
		return idToName.keySet();
	}

	/**
	 * Gets the type identifier loaded by this {@link TypeLibrary}
	 *
	 * @return {@link String} type identifier
	 * @see ReflectableType#getTypeIdentifier()
	 */
	public String getTypeIdentifier() {
		return typeIdentifier;
	}

	/**
	 * Get the user-recognizable name of an implementation by ID
	 *
	 * @param id
	 *            {@link ReflectableType#getIdentifier()}
	 * @return {@link ReflectableType#getName()}
	 * @see #getIdList()
	 */
	public String getNameById(String id) {
		return idToName.get(id);
	}

	private void load() {
		long start = System.currentTimeMillis();

		try {
			search();
		} catch (IOException e) {
			e.printStackTrace();
		}

		long elapsed = System.currentTimeMillis() - start;
		System.out.println("TypeLibrary found " + implementations.size() + " implementations of " + type.getTypeName() + " (" + elapsed + " ms)");

	}

	@SuppressWarnings("unchecked")
	private void add(Class<?> c) {
		// Ignore if not an implementation of the target type
		if (!type.isAssignableFrom(c))
			return;

		// Only add concrete classes
		int mods = c.getModifiers();
		if (Modifier.isAbstract(mods) || Modifier.isInterface(mods))
			return;

		try {
			T reflectedType = (T) c.newInstance();

			// Use the first instance to record the type identifier/name
			// this class is using
			if (typeIdentifier == null) {
				typeIdentifier = reflectedType.getTypeIdentifier();
				typeName = reflectedType.getTypeName();
			}

			// Make sure we're not hitting collisions
			Class<T> existing = implementations.get(reflectedType.getIdentifier());
			if (existing != null) {
				throw new InstantiationException("Tried to load two" + reflectedType.getTypeIdentifier() + " implementations with the same identifier: " + reflectedType.getIdentifier());
			}

			// Save it to the map
			implementations.put(reflectedType.getIdentifier(), (Class<T>) c);
			idToName.put(reflectedType.getIdentifier(), reflectedType.getName());
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private void search() throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Enumeration<URL> resources = classLoader.getResources("");

		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();

			if (resource.toString().startsWith("jar:file:")) {
				searchJarFile(resource);
				continue;
			}

			File file = new File(URLDecoder.decode(resource.getFile(), "UTF-8"));
			if (file.exists() && file.isDirectory())
				recurseDirectory(file, "");
		}

		String[] classPaths = System.getProperty("java.class.path").split(";|:");
		Set<String> classPathSet = new HashSet<String>(Arrays.asList(classPaths));

		for (String string : classPathSet) {
			boolean blacklisted = false;
			for (String blacklistItem : blacklist) {
				if (string.endsWith(blacklistItem)) {
					blacklisted = true;
					break;
				}
			}

			if (blacklisted)
				continue;

			File file = new File(string);

			if (file.exists() && file.isFile() && file.getName().endsWith(".jar")) {
				URL jarUrl = file.toURI().toURL();

				if (jarUrl != null)
					searchJarFile(jarUrl);
			}
		}
	}

	private void recurseDirectory(File directory, String packageName) {
		String packagePrefix = packageName.isEmpty() ? "" : (packageName + ".");

		if (directory.exists()) {
			File[] files = directory.listFiles();

			for (File file : files) {
				String filename = file.getName();

				if (file.isDirectory()) {
					recurseDirectory(file, packagePrefix + filename);
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

	private void searchJarFile(URL url) throws UnsupportedEncodingException {
		Enumeration<JarEntry> entries = new SmudgrJar(url).entries();

		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		while (entries.hasMoreElements()) {
			JarEntry jarEntry = entries.nextElement();
			String entryName = jarEntry.getName();

			boolean isDirectory = jarEntry.isDirectory();
			boolean isClass = entryName.endsWith(".class");

			if (!isDirectory && isClass) {
				String className = jarEntry.getName().replaceFirst("\\.class$", "").replace("/", ".");

				try {
					add(Class.forName(className, false, contextClassLoader));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
