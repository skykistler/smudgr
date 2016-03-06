package io.smudgr.view.cef.util;

import java.io.File;

import org.cef.browser.CefBrowser;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import io.smudgr.source.Source;
import io.smudgr.source.SourceFactory;
import io.smudgr.source.smudge.Smudge;
import io.smudgr.view.cef.CefView;

public class SmudgrQueryRouter extends CefMessageRouterHandlerAdapter {
	private CefView view;

	public SmudgrQueryRouter(CefView v) {
		this.view = v;
	}

	public boolean onQuery(CefBrowser browser, long query_id, String request, boolean persistent, CefQueryCallback callback) {

		if (request.startsWith("smudgr://") && request.endsWith("html")) {
			JarFile resource = new JarFile(request.substring(9));
			byte[] data = resource.getData();
			String file = new String(data);

			callback.success(file);
		}

		if (request.startsWith("open")) {

			callback.success("Opening file dialog");

			view.getController().pause();

			(new Thread() {
				public void run() {
					FileDialog fileDialog = new FileDialog("Open Source");
					fileDialog.show(view.getWindow());
					SourceFactory sf = new SourceFactory();
					File f = fileDialog.getSelectedFile();

					Smudge s = view.getController().getSmudge();
					if (f != null) {
						Source src = sf.makeSource(f.getAbsolutePath());
						src.init();

						s.setSource(src);
					}

					view.getController().start();
				}
			}).start();

		}

		return true;
	}
}