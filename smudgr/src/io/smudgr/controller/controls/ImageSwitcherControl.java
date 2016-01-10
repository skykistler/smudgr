package io.smudgr.controller.controls;

import java.io.File;
import java.util.ArrayList;

import io.smudgr.controller.Controller;
import io.smudgr.model.Frame;

public class ImageSwitcherControl extends Controllable {

	private int curImage = 0;
	private ArrayList<String> files = new ArrayList<String>();;
	private ArrayList<Frame> images = new ArrayList<Frame>();;

	public ImageSwitcherControl(Controller c, String location) {
		super(c, "Image Switcher");

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
			Frame image = new Frame("../data/" + files.get(i));
			if (image != null)
				images.add(image);
		}

		System.out.println("Successfully loaded " + images.size() + " images");
	}

	public void increment() {
		curImage += 1;
		if (curImage >= images.size())
			curImage = 0;

		getController().getSmudge().setSource(images.get(curImage));
	}

	public void decrement() {
		curImage -= 1;
		if (curImage < 0)
			curImage = images.size() - 1;

		getController().getSmudge().setSource(images.get(curImage));
	}

	public void inputValue(int value) {
	}

	public void inputOn(int value) {
	}

	public void inputOff(int value) {
	}

}
