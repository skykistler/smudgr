package io.smudgr.project.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import io.smudgr.app.Controller;
import io.smudgr.project.PropertyMap;
import io.smudgr.project.smudge.source.Gif;
import io.smudgr.project.smudge.source.Image;
import io.smudgr.project.smudge.source.Source;
import io.smudgr.project.smudge.source.SourceSet;
import io.smudgr.project.smudge.source.Video;

public class SourceLibrary {

	private String location;
	private int currentSet = -1;
	private ArrayList<String> files = new ArrayList<String>();;
	private ArrayList<SourceSet> sourceSets = new ArrayList<SourceSet>();

	public void setLocation(String location) {
		this.location = location;

		if (location == null)
			return;

		File directory = new File(location);

		if (!directory.exists()) {
			System.out.println("File " + location + " does not exist!");
			return;
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

	public void nextSet() {
		setCurrentSet(currentSet + 1);
	}

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

		Controller.getInstance().getProject().getSmudge().setSource(current);
	}

	public void save(PropertyMap pm) {
		pm.setAttribute("location", (new File(location)).getAbsolutePath());
	}

	public void load(PropertyMap pm) {
		if (pm.hasAttribute("location"))
			setLocation(pm.getAttribute("location"));
	}

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
