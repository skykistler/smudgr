package me.skykistler.smudgr.controller.controls;

import java.io.File;
import java.util.ArrayList;

import me.skykistler.smudgr.Smudge;
import me.skykistler.smudgr.view.View;
import processing.core.PImage;

public class ImageSwitcherControl extends Controllable {

	private Smudge smudge;
	private int curImage = 0;
	private ArrayList<String> files = new ArrayList<String>();;
	private ArrayList<PImage> images = new ArrayList<PImage>();;

	public ImageSwitcherControl(Smudge smudge, String location) {
		super("Image Switcher");
		this.smudge = smudge;

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
	}

	public void init() {
		loadImages(smudge.getView());
	}

	public void loadImages(View processor) {
		System.out.println("Loading " + files.size() + " source files...");

		for (int i = 0; i < files.size(); i++) {
			PImage image = processor.loadImage("../data/" + files.get(i));
			if (image != null)
				images.add(image);
		}

		System.out.println("Successfully loaded " + images.size() + " images");
	}

	public void increment() {
		curImage += 1;
		if (curImage >= images.size())
			curImage = 0;

		smudge.setSource(images.get(curImage));
	}

	public void decrement() {
		curImage -= 1;
		if (curImage < 0)
			curImage = images.size() - 1;

		smudge.setSource(images.get(curImage));
	}

	public void midiValue(int value) {
	}

	public void noteOn(int note) {
	}

	public void noteOff(int note) {
	}

}
