package io.smudgr.app.view;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

/**
 * The {@link FileDialog} singleton opens a native {@link JFileChooser} and
 * passes a selected file or set of files to a given
 * {@link FileDialogCallback}
 *
 *
 * @see FileDialog#getInstance()
 * @see FileDialog#show(String, boolean, FileDialogFilter, FileDialogCallback)
 *      FileDialog.show(actionName, allowMultiselect, fileFilter, callback)
 * @see FileDialogCallback
 */
public class FileDialog {

	private volatile static FileDialog instance;

	/**
	 * Get the current application instance's {@link FileDialog} service
	 *
	 * @return {@link FileDialog}
	 *
	 * @see FileDialog#show(String, boolean, FileDialogFilter,
	 *      FileDialogCallback) FileDialog.show(actionName, allowMultiselect,
	 *      fileFilter, callback)
	 */
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

	/**
	 * Show a file chooser with the specified parameters
	 *
	 * @param actionName
	 *            Approval button text (i.e. Save, Open, Load)
	 * @param allowMultiselect
	 *            Allow the selection of multiple files
	 * @param fileFilter
	 *            {@link FileDialogFilter} to allow only certain file types
	 * @param callback
	 *            Function to call with selected files, or failure message
	 *
	 * @see FileDialogCallback
	 */
	public void show(String actionName, boolean allowMultiselect, FileDialogFilter fileFilter, FileDialogCallback callback) {
		(new Thread() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						fileChooser.setMultiSelectionEnabled(allowMultiselect);

						for (FileFilter f : fileChooser.getChoosableFileFilters())
							fileChooser.removeChoosableFileFilter(f);

						fileChooser.addChoosableFileFilter(fileFilter);

						int ret = fileChooser.showDialog(null, actionName);

						if (ret != JFileChooser.APPROVE_OPTION) {
							callback.onFailure("Cancelled.");
							return;
						}

						File[] selected = null;
						if (allowMultiselect)
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

	/**
	 * The {@link FileDialogCallback} interface allows passing behavior for
	 * what to do when the file chooser is resolved, either from user
	 * cancellation or actual file selection.
	 *
	 * @see FileDialog#show(String, boolean, FileDialogFilter,
	 *      FileDialogCallback) FileDialog.show(actionName, allowMultiselect,
	 *      fileFilter, callback)
	 */
	public interface FileDialogCallback {
		/**
		 * Callback with the user selected file(s)
		 * <p>
		 * The selectedFiles array will never be empty and if multi-selection
		 * isn't allowed, there will only be on selected file.
		 *
		 * @param selectedFiles
		 *            Never empty, length one if multi-select was not allowed
		 */
		public void onSelection(File[] selectedFiles);

		/**
		 * Callback with reason for failure.
		 * <p>
		 * This is called if the user manually cancelled for didn't select any
		 * files.
		 *
		 * @param reason
		 *            Failure message.
		 */
		public void onFailure(String reason);
	}

	/**
	 * Instances of the {@link FileDialogFilter} class allows restricting
	 * selections to a specific file type for a {@link FileDialog}
	 */
	public static class FileDialogFilter extends FileFilter {

		String description = "";
		String fileExt = "";

		/**
		 * Create a new filter with the given extension type
		 *
		 * @param extension
		 *            File type
		 */
		public FileDialogFilter(String extension) {
			fileExt = extension;
		}

		/**
		 * Create a new filter with the given extension type and a description
		 * of that file type.
		 *
		 * @param extension
		 *            File type
		 * @param typeDescription
		 *            Shown to the user
		 */
		public FileDialogFilter(String extension, String typeDescription) {
			fileExt = extension;
			this.description = typeDescription;
		}

		@Override
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			return (f.getName().toLowerCase().endsWith("." + fileExt));
		}

		@Override
		public String getDescription() {
			return description;
		}
	}
}
