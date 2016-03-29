package io.smudgr.ext.cef.controller.messages;

import java.io.File;

import javax.swing.SwingUtilities;

import io.smudgr.ext.cef.controller.util.FileDialog;
import io.smudgr.ext.cef.view.CefView;
import io.smudgr.source.Source;
import io.smudgr.source.SourceFactory;
import io.smudgr.source.smudge.Smudge;

public class OpenFileDialog implements CefMessageStrategy {

	public boolean processRequest(String content, CefView view) {
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

		return true;
	}

	public String onSuccess() {
		return "Opening source dialog";
	}

	public String onFailure() {
		return "Failed to open source dialog";
	}

}
