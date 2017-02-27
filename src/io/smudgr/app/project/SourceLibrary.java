package io.smudgr.app.project;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import io.smudgr.app.controller.Controller;
import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.util.source.Gif;
import io.smudgr.util.source.Image;
import io.smudgr.util.source.Source;
import io.smudgr.util.source.SourceSet;
import io.smudgr.util.source.Video;

/**
 * Managed collection of sources for manipulation and mixing. Sources are
 * aggregated in a {@link SourceSet} for organization and efficiency. This
 * system will hopefully change soon as the UI is developed.
 *
 * @see SourceSet
 * @see Source
 */
public class SourceLibrary {

	private String location;
	private int currentSet = -1;
	private ArrayList<String> files = new ArrayList<String>();;
	private ArrayList<SourceSet> sourceSets = new ArrayList<SourceSet>();

	/**
	 * Set the folder location of the sources to load, and load them.
	 *
	 * @param location
	 *            Path to sources.
	 * @see SourceSet
	 */
	public void setLocation(String location) {
		this.location = location;

		if (location == null)
			return;

		File directory = new File(location);

		if (!directory.exists()) {
			location = Controller.getInstance().getAppPath() + "/" + location;
			directory = new File(location);

			if (!directory.exists()) {
				System.out.println("Source directory " + location + " can't be found!");
				return;
			}
		}

		// If not a directory, just add the one file
		if (!directory.isDirectory())
			files.add(location);
		else {
			String[] list = directory.list();
			Arrays.sort(list);
			for (int i = 0; i < list.length; i++) {
				String path = location + "/" + list[i];

				files.add(path);
			}
		}

		load();
	}

	/**
	 * Switch to the next set in the library.
	 *
	 * @see SourceLibrary#previousSet()
	 */
	public void nextSet() {
		setCurrentSet(currentSet + 1);
	}

	/**
	 * Switch to the previous set in the library.
	 *
	 * @see SourceLibrary#nextSet()
	 */
	public void previousSet() {
		setCurrentSet(currentSet - 1);
	}

	private void load() {
		System.out.println("Loading " + files.size() + " source files...");

		for (String path : files) {
			SourceSet set = new SourceSet(path);
			if (set.size() > 0)
				sourceSets.add(set);
		}

		setCurrentSet(0);
		System.out.println("Successfully loaded " + sourceSets.size() + " source sets");
	}

	private SourceSet getCurrentSet() {
		if (sourceSets.size() == 0 || currentSet == -1)
			return null;

		return sourceSets.get(currentSet);
	}

	private void setCurrentSet(int i) {
		if (sourceSets.size() == 0) {
			currentSet = 0;
			return;
		}

		if (i == currentSet)
			return;

		SourceSet current = getCurrentSet();
		if (current != null)
			current.dispose();

		currentSet = i;

		if (currentSet >= sourceSets.size())
			currentSet %= sourceSets.size();
		else if (currentSet < 0)
			currentSet += sourceSets.size();

		current = sourceSets.get(currentSet);
		current.init();

		Controller.getInstance().getProject().getRack().setSource(current);
	}

	/**
	 * Save this {@link SourceLibrary} location to the given
	 * {@link PropertyMap}
	 *
	 * @param pm
	 *            The {@link PropertyMap} to save to.
	 */
	public void save(PropertyMap pm) {
		pm.setAttribute("location", (new File(location)).getAbsolutePath());
	}

	/**
	 * Load {@link SourceLibrary} location from the given {@link PropertyMap}.
	 *
	 * @param pm
	 *            The {@link PropertyMap} to load from.
	 */
	public void load(PropertyMap pm) {
		if (pm.hasAttribute("location"))
			setLocation(pm.getAttribute("location"));
	}

	/**
	 * Get a {@link Source} object from a given path.
	 *
	 * @param path
	 *            Relative or absolute path of source to load.
	 * @return {@link Source} object of type based on file extension.
	 *
	 * @see Image
	 * @see Gif
	 * @see Video
	 */
	public Source getSource(String path) {
		if (path.contains("/."))
			return null;

		String ext = path.substring(path.lastIndexOf(".") + 1);

		try {
			switch (ext) {
				case "mov":
				case "mp4":
					return new Video(path);
				case "gif":
					return new Gif(path);
				case "png":
				case "jpg":
				case "jpeg":
					return new Image(path);
				default:
					return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

}
