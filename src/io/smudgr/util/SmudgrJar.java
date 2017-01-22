package io.smudgr.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Represents a jar file and the resources within it.
 * <p>
 * This is used to enumerate classes for reflection, or load UI files from a
 * build.
 */
public class SmudgrJar {

	private JarFile jarFile;
	private String resource;

	/**
	 * Load a jar file given a file URL
	 *
	 * @param url
	 *            pointing to file://
	 * @throws UnsupportedEncodingException
	 *             if URL malformed
	 */
	public SmudgrJar(URL url) throws UnsupportedEncodingException {
		this(URLDecoder.decode(url.getFile(), "UTF-8").replaceFirst("!.*$", "").replaceFirst("^file:", ""));
	}

	/**
	 * Create a new jar file given a file path
	 *
	 * @param resource
	 *            File path (or project resource)
	 */
	public SmudgrJar(String resource) {
		this.resource = resource;
	}

	/**
	 * Load byte data of jar
	 *
	 * @return {@code byte[]}
	 */
	public byte[] getData() {
		InputStream inStream = ClassLoader.getSystemResourceAsStream(resource);
		if (inStream != null)
			try {
				ByteArrayOutputStream outFile = new ByteArrayOutputStream();
				int readByte = -1;
				while ((readByte = inStream.read()) >= 0)
					outFile.write(readByte);
				return outFile.toByteArray();
			} catch (IOException e) {
			}

		return null;
	}

	/**
	 * Get the entries within this jar file
	 *
	 * @return entries
	 */
	public Enumeration<JarEntry> entries() {
		try {
			jarFile = new JarFile(resource);
			return jarFile.entries();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}
