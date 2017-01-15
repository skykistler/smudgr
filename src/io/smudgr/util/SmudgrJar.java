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

public class SmudgrJar {

	private JarFile jarFile;
	private String resource;

	public SmudgrJar(URL url) throws UnsupportedEncodingException {
		this(URLDecoder.decode(url.getFile(), "UTF-8").replaceFirst("!.*$", "").replaceFirst("^file:", ""));
	}

	public SmudgrJar(String resource) {
		this.resource = resource;
	}

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
