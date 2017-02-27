package io.smudgr.extensions.cef.util;

import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandlerAdapter;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

import io.smudgr.util.SmudgrJar;

/**
 * The {@link SmudgrScheme} provides a protocol to allow CEF to load assets from
 * the smudgr build
 */
public class SmudgrScheme extends CefResourceHandlerAdapter {
	/**
	 * The scheme:// tag to use
	 */
	public static final String scheme = "smudgr";

	/**
	 * The base path to serve files from
	 */
	public static final String basePath = "io/smudgr/ui/";

	private byte[] data;
	private String mime_type;
	private int offset = 0;

	@Override
	public synchronized boolean processRequest(CefRequest request, CefCallback callback) {
		String url = request.getURL();
		System.out.println("Frontend requested: " + url);

		String path = url.substring(url.indexOf("://ui/") + 6);

		if (path.endsWith("#/"))
			path = path.substring(0, path.length() - 2);

		if (path.endsWith("/"))
			path = path.substring(0, path.length() - 1);

		if (path.isEmpty())
			path = "index.html";

		String ext = path.substring(path.lastIndexOf(".") + 1);

		SmudgrJar resource = new SmudgrJar(basePath + path);
		data = resource.getData();

		if (data != null)
			switch (ext) {
				case "html":
					mime_type = "text/html";
					break;

				case "png":
					mime_type = "image/png";
					break;

				case "gif":
					mime_type = "image/gif";
					break;

				case "js":
					mime_type = "application/javascript";
					break;

				case "css":
					mime_type = "text/css";
					break;

				default:
					mime_type = "application/octet-stream";
					break;
			}
		else {
			String html = "<html><head><title>Error 404</title></head>";
			html += "<body><h1>Error 404</h1>";
			html += "File  " + path + " ";
			html += "does not exist</body></html>";
			data = html.getBytes();
			mime_type = "text/html";

			System.out.println("Could not find: " + basePath + path);
		}

		callback.Continue();
		return true;
	}

	@Override
	public void getResponseHeaders(CefResponse response, IntRef response_length, StringRef redirectUrl) {
		response.setMimeType(mime_type);
		response.setStatus(200);

		response_length.set(data.length);
	}

	@Override
	public synchronized boolean readResponse(byte[] data_out, int bytes_to_read, IntRef bytes_read, CefCallback callback) {
		boolean has_data = false;

		if (offset < data.length) {
			// Copy the next block of data into the buffer.
			int transfer_size = Math.min(bytes_to_read, (data.length - offset));
			System.arraycopy(data, offset, data_out, 0, transfer_size);
			offset += transfer_size;

			bytes_read.set(transfer_size);
			has_data = true;
		} else {
			offset = 0;
			bytes_read.set(0);
		}

		return has_data;
	}

}
