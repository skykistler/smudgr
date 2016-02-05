package io.smudgr.source;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SourceSet implements Source {
	private ArrayList<String> files = new ArrayList<String>();
	private ArrayList<Source> sources = new ArrayList<Source>();
	private int currentSource;

	public SourceSet(String location) {
		File directory = new File("data/" + location);

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
				files.add(location + "/" + list[i]);
		}
	}

	public void init() {
		System.out.println("Loading " + files.size() + " source files...");

		for (int i = 0; i < files.size(); i++) {
			String path = files.get(i);

			Source s = makeSource(path);
			if (s != null) {
				s.init();
				sources.add(s);
			}
		}
	}

	public void update() {
		Source s = getCurrentSource();
		if (s == null)
			return;

		s.update();
	}

	public void stop() {
		Source current = getCurrentSource();
		if (current instanceof Video)
			((Video) current).stop();
	}

	public Frame getFrame() {
		Source s = getCurrentSource();
		if (s == null)
			return null;

		return s.getFrame();
	}

	private Source makeSource(String path) {
		String ext = path.substring(path.lastIndexOf(".") + 1);

		try {
			switch (ext) {
			case "mov":
			case "mp4":
				return new Video(path);
			case "gif":
				return new Gif(path);
			default:
				return new Image(path);
			}
		} catch (Exception e) {
			return null;
		}
	}

	public int size() {
		return sources.size();
	}

	public Source getCurrentSource() {
		if (sources.size() == 0)
			return null;

		return sources.get(currentSource);
	}

	public void increment() {
		setCurrentSource(currentSource + 1);
	}

	public void decrement() {
		setCurrentSource(currentSource - 1);
	}

	private void setCurrentSource(int i) {
		if (sources.size() == 0) {
			currentSource = 0;
			return;
		}

		if (i == currentSource)
			return;

		stop();

		currentSource = i;

		if (currentSource >= sources.size())
			currentSource %= sources.size();
		else if (currentSource < 0)
			currentSource += sources.size();
	}

}
