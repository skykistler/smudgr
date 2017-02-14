package io.smudgr.app.project.reflect;

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

import io.smudgr.util.SmudgrJar;

/**
 * A {@link ReflectionLibrary} provides the ability to detect and organize all
 * loaded
 * concrete implementations of a given {@link ReflectableType}
 *
 * @param <T>
 *            The {@link ReflectableType} this Library should manage
 */
public abstract class ReflectionLibrary<T extends ReflectableType> {

	/*
	 * Adding to this list improves speed of reflection by ignoring libraries
	 * used by smudgr that will never contain ReflectableType's
	 */
	private static final String[] blacklist = new String[] { "gluegen-rt.jar", "jcodec-0.1.9.jar", "jcodec-javase-0.1.9.jar", "jogl-all.jar", "trove.jar" };

	protected Class<T> type;
	private String typeIdentifier;
	private String typeName;

	/**
	 * Instantiate a new {@link ReflectionLibrary} to detect and catalog every
	 * implementation of the given type.
	 *
	 * @param type
	 *            {@link Class} of the type to organize
	 */
	public ReflectionLibrary(Class<T> type) {
		this.type = type;
	}

	/**
	 * Gets whether this library already contains an implementation of the given
	 * type.
	 *
	 * @param implementation
	 *            type implementation
	 * @return {@code boolean}
	 */
	public abstract boolean contains(ReflectableType implementation);

	protected abstract void add(Class<T> type, ReflectableType implementation);

	/**
	 * Gets how many implementations were loaded by this library.
	 *
	 * @return {@code int}
	 */
	public abstract int count();

	/**
	 * Gets the type identifier loaded by this {@link ReflectionLibrary}
	 *
	 * @return {@link String} type identifier
	 * @see ReflectableType#getTypeCategoryIdentifier()
	 */
	public String getTypeIdentifier() {
		return typeIdentifier;
	}

	/**
	 * Gets the type name loaded by this {@link ReflectionLibrary}
	 *
	 * @return {@link String} type identifier
	 * @see ReflectableType#getTypeCategoryName()
	 */
	public String getTypeName() {
		return typeName;
	}

	protected void load() {
		long start = System.currentTimeMillis();

		try {
			search();
		} catch (IOException e) {
			e.printStackTrace();
		}

		long elapsed = System.currentTimeMillis() - start;
		System.out.println("TypeLibrary found " + count() + " implementations of " + typeName + " (" + elapsed + " ms)");

	}

	@SuppressWarnings("unchecked")
	private void tryAdd(Class<?> c) {
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
				typeIdentifier = reflectedType.getTypeCategoryIdentifier();
				typeName = reflectedType.getTypeCategoryName();
			}

			// Make sure we're not hitting collisions
			if (contains(reflectedType)) {
				throw new InstantiationException("Tried to load two " + reflectedType.getTypeCategoryIdentifier() + " implementations with the same identifier: " + reflectedType.getTypeIdentifier());
			}

			// Save it to the map
			add((Class<T>) c, reflectedType);
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
						tryAdd(Class.forName(packagePrefix + filename.substring(0, filename.length() - 6)));
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
					tryAdd(Class.forName(className, false, contextClassLoader));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
