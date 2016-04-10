package io.smudgr.view;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

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

	public void show(String name, boolean multiSelect, FileDialogFilter filter, FileDialogCallback callback) {
		(new Thread() {
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						fileChooser.setMultiSelectionEnabled(multiSelect);

						for (FileFilter f : fileChooser.getChoosableFileFilters())
							fileChooser.removeChoosableFileFilter(f);

						fileChooser.addChoosableFileFilter(filter);

						int ret = fileChooser.showDialog(null, name);

						if (ret != JFileChooser.APPROVE_OPTION) {
							callback.onFailure("Cancelled.");
							return;
						}

						File[] selected = null;
						if (multiSelect)
							selected = fileChooser.getSelectedFiles();
						else
							selected = new File[] { fileChooser.getSelectedFile() };

						if (selected == null || selected.length == 0) {
							callback.onFailure("No file chosen.");
							return;
						}

						callback.onSelection(selected);
					}
				});
			}
		}).start();
	}

	public interface FileDialogCallback {
		public void onSelection(File[] selectedFiles);

		public void onFailure(String reason);
	}

	public static class FileDialogFilter extends FileFilter {

		String description = "";
		String fileExt = "";

		public FileDialogFilter(String extension) {
			fileExt = extension;
		}

		public FileDialogFilter(String extension, String typeDescription) {
			fileExt = extension;
			this.description = typeDescription;
		}

		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			return (f.getName().toLowerCase().endsWith("." + fileExt));
		}

		public String getDescription() {
			return description;
		}
	}
}
