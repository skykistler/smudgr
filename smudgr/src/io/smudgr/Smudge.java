package io.smudgr;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import io.smudgr.alg.Algorithm;
import io.smudgr.controller.controls.Controllable;
import io.smudgr.view.View;
import processing.core.PImage;

public class Smudge {
	private ArrayList<Algorithm> algorithms;
	private String filename;
	private String name;
	private PImage originalSource;
	private PImage source;
	private View processor;

	private PImage frame;

	private int downsample = 1;

	public Smudge(String filename) {
		this(filename, filename);
	}

	public Smudge(String smudgename, String filename) {
		this.filename = filename;
		name = smudgename;

		algorithms = new ArrayList<Algorithm>();
	}

	public void init(View view) throws FileNotFoundException {
		processor = view;

		System.out.println("\nInitializing smudge...");

		if (!(new File("data/" + filename).exists())) {
			throw new FileNotFoundException("Could not find file: data/" + filename);
		}

		setSource(processor.loadImage("../data/" + filename));

		System.out.println("Setting up controls...");
		for (Controllable c : Controllable.getControls())
			c.init();

		System.out.println("Smudge initialized.");
	}

	public void downsample(int amount) {
		downsample = amount;

		if (originalSource != null) {
			source = originalSource.copy();
			source.resize(source.width / downsample, source.height / downsample);

			source.loadPixels();
		}
	}

	public void addAlgorithm(Algorithm alg) {
		algorithms.add(alg);
	}

	public ArrayList<Algorithm> getAlgorithms() {
		return algorithms;
	}

	public void setSource(PImage image) {
		originalSource = image;

		downsample(downsample);
	}

	public PImage render() {
		synchronized (this) {
			frame = source.copy();

			for (int i = 0; i < algorithms.size(); i++) {
				algorithms.get(i).execute(processor, frame);
			}
			frame.updatePixels();

			if (saveNextRender)
				outputFrame();
		}

		return frame;
	}

	boolean saveNextRender = false;

	public void save() {
		saveNextRender = true;
	}

	private void outputFrame() {
		String output = "output/" + name + "_" + System.currentTimeMillis() + ".png";
		System.out.println("Saving smudge to " + output);
		frame.save(new File(output).getAbsolutePath());
		saveNextRender = false;
	}

	public View getView() {
		return processor;
	}
}
