package io.smudgr.controller.controls;

import java.io.File;
import java.util.ArrayList;

import io.smudgr.controller.Controller;
import io.smudgr.source.Frame;
import io.smudgr.source.Gif;
import io.smudgr.source.Source;
import io.smudgr.source.Video;

public class SourceControl extends Controllable {

	private int curModel = 0;
	private Frame lastFrame;
	private ArrayList<String> files = new ArrayList<String>();;
	private ArrayList<Source> sources = new ArrayList<Source>();;

	public SourceControl(Controller c, String location) {
		super(c, "Source Switcher");

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
			for (int i = 0; i < list.length; i++)
				files.add(location + "/" + list[i]);
		}

		requestBind();
	}

	public void init() {
		loadImages();
	}

	public void loadImages() {
		System.out.println("Loading " + files.size() + " source files...");

		for (int i = 0; i < files.size(); i++) {
			String path = files.get(i);
			Source s = getSource(path);
			if (s != null)
				sources.add(s);
		}

		System.out.println("Successfully loaded " + sources.size() + " sources");
	}

	private Source getSource(String path) {
		String ext = path.substring(path.lastIndexOf(".") + 1);

		try {
			switch (ext) {
			case "mov":
			case "mp4":
				return new Video(path);
			case "gif":
				return new Gif(path);
			default:
				return new Frame(path);
			}
		} catch (Exception e) {
			return null;
		}
	}

	public void update() {
		if (sources.size() == 0)
			return;

		lastFrame = sources.get(curModel).getFrame();

		if (lastFrame != null)
			getController().getSmudge().setSource(lastFrame);
	}

	public void increment() {
		Source current = sources.get(curModel);
		if (current instanceof Video)
			((Video) current).stop();

		curModel += 1;
		if (curModel >= sources.size())
			curModel = 0;
	}

	public void decrement() {
		Source current = sources.get(curModel);
		if (current instanceof Video)
			((Video) current).stop();

		curModel -= 1;
		if (curModel < 0)
			curModel = sources.size() - 1;
	}

	public void inputValue(int value) {
	}

	public void inputOn(int value) {
	}

	public void inputOff(int value) {
	}

}
