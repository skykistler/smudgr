package io.smudgr.view.cef.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class JarFile {

	private String resource;

	public JarFile(String resource) {
		this.resource = resource;
	}

	public byte[] getData() {
		InputStream inStream = ClassLoader.getSystemResourceAsStream(resource);
		if (inStream != null) {
			try {
				ByteArrayOutputStream outFile = new ByteArrayOutputStream();
				int readByte = -1;
				while ((readByte = inStream.read()) >= 0)
					outFile.write(readByte);
				return outFile.toByteArray();
			} catch (IOException e) {
			}
		}
		return null;
	}
}
