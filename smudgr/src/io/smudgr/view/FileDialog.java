package io.smudgr.view;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

public class FileDialog {

	private volatile static FileDialog instance;

	public static FileDialog getInstance() {
		if (instance == null)
			instance = new FileDialog();

		return instance;
	}

	private JFileChooser fileChooser;

	private FileDialog() {
		String parent_path = null;
		try {
			String this_path = URLDecoder.decode(FileDialog.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
			parent_path = (new File(this_path)).getParentFile().getAbsolutePath();

			if (parent_path.contains("smudgr"))
				parent_path = parent_path.substring(0, parent_path.lastIndexOf("smudgr"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		fileChooser = new JFileChooser(parent_path);
	}

	public void show(String name, FileDialogCallback callback) {
		(new Thread() {
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						int ret = fileChooser.showDialog(null, name);

						File[] selected = fileChooser.getSelectedFiles();
						if (ret == JFileChooser.APPROVE_OPTION && selected != null && selected.length > 1)
							callback.onSelection(selected);
					}
				});
			}
		}).start();
	}

	public interface FileDialogCallback {
		public void onSelection(File[] selectedFiles);
	}
}
