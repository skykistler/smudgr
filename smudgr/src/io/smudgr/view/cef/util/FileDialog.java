package io.smudgr.view.cef.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class FileDialog {

	private volatile static FileDialog instance;

	public static FileDialog getInstance() {
		if (instance == null)
			instance = new FileDialog();

		return instance;
	}

	private JFileChooser fileChooser;
	private File[] selectedFiles;

	public FileDialog() {
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

	public File getSelectedFile() {
		if (selectedFiles == null || selectedFiles.length == 0)
			return null;

		return selectedFiles[0];
	}

	public File[] getSelectedFiles() {
		return selectedFiles;
	}

	public void show(JFrame frame, String name) {
		int ret = fileChooser.showDialog(frame, name);

		if (ret == JFileChooser.APPROVE_OPTION)
			selectedFiles = new File[] { fileChooser.getSelectedFile() };
		else
			selectedFiles = null;
	}

}
