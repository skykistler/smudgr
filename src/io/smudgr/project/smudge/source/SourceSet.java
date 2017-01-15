package io.smudgr.project.smudge.source;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import io.smudgr.util.Frame;

public class SourceSet implements Source {
	private ArrayList<String> files = new ArrayList<String>();
	private ArrayList<Source> sources = new ArrayList<Source>();
	private int currentSource;

	public SourceSet(String location) {
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
			for (int i = 0; i < list.length; i++)
				if (!list[i].toLowerCase().equals(".ds_store"))
					files.add(location + "/" + list[i]);
		}

		for (int i = 0; i < files.size(); i++) {
			String path = files.get(i);

			Source s = getSourceLibrary().getSource(path);
			if (s != null) {
				sources.add(s);
			}
		}
	}

	public void init() {
		System.out.println("Loading " + files.size() + " source files...");

		for (Source s : sources)
			s.init();
	}

	public void update() {
		Source s = getCurrentSource();
		if (s == null)
			return;

		s.update();
	}

	public void dispose() {
		for (Source s : sources)
			s.dispose();
	}

	public Frame getFrame() {
		Source s = getCurrentSource();
		if (s == null || s == this)
			return null;

		return s.getFrame();
	}

	public int size() {
		return sources.size();
	}

	public Source getCurrentSource() {
		if (sources.size() == 0)
			return null;

		return sources.get(currentSource);
	}

	public void nextSource() {
		setCurrentSource(currentSource + 1);
	}

	public void previousSource() {
		setCurrentSource(currentSource - 1);
	}

	public void setCurrentSource(int i) {
		if (sources.size() == 0) {
			currentSource = 0;
			return;
		}

		if (i == currentSource)
			return;

		Source current = getCurrentSource();
		if (current instanceof Video)
			current.dispose();

		currentSource = i;

		if (currentSource >= sources.size())
			currentSource %= sources.size();
		else if (currentSource < 0)
			currentSource += sources.size();
	}

}
