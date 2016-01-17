package io.smudgr.controller.controls;

import java.io.File;
import java.util.ArrayList;

import io.smudgr.controller.Controller;
import io.smudgr.model.Frame;
import io.smudgr.model.Gif;
import io.smudgr.model.Model;
import io.smudgr.model.Video;

public class SourceSwitcherControl extends Controllable {

	private int curModel = 0;
	private Frame lastFrame;
	private ArrayList<String> files = new ArrayList<String>();;
	private ArrayList<Model> models = new ArrayList<Model>();;

	public SourceSwitcherControl(Controller c, String location) {
		super(c, "Source Switcher");

		if (!location.endsWith("/"))
			location += "/";

		File directory = new File("data/" + location);
		if (!directory.isDirectory()) {
			System.out.println("The specified location was not a directory! Errors will result.");
			return;
		}

		String[] list = directory.list();
		for (int i = 0; i < list.length; i++) {
			files.add(location + list[i]);
		}

		requestBind();
	}

	public void init() {
		loadImages();
	}

	public void loadImages() {
		System.out.println("Loading " + files.size() + " source files...");

		for (int i = 0; i < files.size(); i++) {
			String path = "../data/" + files.get(i);
			Model model = getModel(path);
			if (model != null)
				models.add(model);
		}

		System.out.println("Successfully loaded " + models.size() + " sources");
	}

	private Model getModel(String path) {
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
		lastFrame = models.get(curModel).getFrame();

		if (lastFrame != null)
			getController().getSmudge().setSource(lastFrame);
	}

	public void increment() {
		Model current = models.get(curModel);
		if (current instanceof Video)
			((Video) current).stop();

		curModel += 1;
		if (curModel >= models.size())
			curModel = 0;
	}

	public void decrement() {
		Model current = models.get(curModel);
		if (current instanceof Video)
			((Video) current).stop();

		curModel -= 1;
		if (curModel < 0)
			curModel = models.size() - 1;
	}

	public void inputValue(int value) {
	}

	public void inputOn(int value) {
	}

	public void inputOff(int value) {
	}

}
