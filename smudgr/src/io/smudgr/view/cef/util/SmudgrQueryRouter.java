package io.smudgr.view.cef.util;

import java.io.File;

import javax.swing.SwingUtilities;

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

		if (request.startsWith("action:open")) {

			callback.success("Opening file dialog");

			(new Thread() {
				public void run() {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							FileDialog.getInstance().show(view.getWindow(), "Open Source");
							SourceFactory sf = new SourceFactory();
							File f = FileDialog.getInstance().getSelectedFile();

							Smudge s = view.getController().getSmudge();
							if (f != null) {
								Source src = sf.makeSource(f.getAbsolutePath());

								if (src != null) {
									src.init();
									s.setSource(src);
								}
							}
						}
					});
				}
			}).start();
		}

		if (request.startsWith("prop")) {
			String[] parts = request.split(":");

			if (parts.length != 3) {
				callback.failure(0, "Invalid property request");
				return true;
			}

			int intval = 0;
			try {
				intval = Integer.parseInt(parts[2]);
			} catch (Exception e) {
				e.printStackTrace();
				callback.failure(0, "Invalid property value");
				return true;
			}

			switch (parts[1]) {
			case "renderOffsetX":
				view.getWindow().getRenderFrame().setX(intval);
				break;
			case "renderOffsetY":
				view.getWindow().getRenderFrame().setY(intval);
				break;
			case "renderViewWidth":
				view.getWindow().getRenderFrame().setWidth(intval);
				break;
			case "renderViewHeight":
				view.getWindow().getRenderFrame().setHeight(intval);
				break;
			default:
				break;
			}
		}

		if (request.startsWith("action:updateRenderView"))
			view.getWindow().getRenderFrame().updateDimensions();

		return true;
	}
}