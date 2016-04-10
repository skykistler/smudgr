/**
 * Copyright 2010 Gigadot [Weerapong Phadungsukanan]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * email : gigadot@gmail.com
 *
 */
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

	private boolean fullSearch = false;
	private String parentPackage;
	private Class<?> type;

	private Set<Class<?>> results = new HashSet<Class<?>>();

	public Reflect(Class<?> type) {
		this("*", type);
	}

	public Reflect(String parentPackage, Class<?> type) {
		this.parentPackage = parentPackage == null || parentPackage.isEmpty() ? "*" : parentPackage;
		fullSearch = parentPackage.equals("*");

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
		System.out.println("Reflect found " + results.size() + " implementations of " + type.getTypeName() + " from package " + parentPackage + " (" + elapsed + " ms)");

		return results;
	}

	private void searchPackage() throws IOException {
		String packagePath = parentPackage.replace('.', '/');
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Enumeration<URL> resources = classLoader.getResources(packagePath);

		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();

			if (resource.toString().startsWith("jar:file:")) {
				searchJarFile(resource, parentPackage);
				continue;
			}

			File file = new File(URLDecoder.decode(resource.getFile(), "UTF-8"));
			if (file.exists() && file.isDirectory())
				searchDirectory_r(file, parentPackage);
		}

		if (fullSearch) {
			String[] classPaths = System.getProperty("java.class.path").split(";");
			Set<String> classPathSet = new HashSet<String>(Arrays.asList(classPaths));

			for (String string : classPathSet) {
				File file = new File(string);

				if (file.exists() && file.isFile() && file.getName().endsWith(".jar")) {
					URL jarUrl = file.toURI().toURL();

					if (jarUrl != null)
						searchJarFile(jarUrl, packagePath);
				}
			}
		}
	}

	private void searchDirectory_r(File directory, String packageName) {
		String packagePrefix = packageName.isEmpty() ? "" : ".";

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

			if (!jarEntry.isDirectory() && entryName.startsWith(packagePathFilter) && entryName.endsWith(".class")) {
				String className = jarEntry.getName().replaceFirst("\\.class$", "").replace("/", ".");

				try {
					add(Class.forName(className, false, contextClassLoader));
				} catch (Throwable ex) {
					ex.printStackTrace();
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