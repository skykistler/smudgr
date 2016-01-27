package io.smudgr;

import java.util.ArrayList;

import io.smudgr.alg.Algorithm;
import io.smudgr.controller.Controller;
import io.smudgr.model.Frame;

public class Smudge {
	private Controller controller;

	private ArrayList<Algorithm> algorithms;
	private String filename;
	private String name;
	private Frame originalSource;

	private Frame frame;

	private int downsample = 1;

	public Smudge(String filename) {
		this(filename, filename);
	}

	public Smudge(String smudgename, String filename) {
		this.filename = filename;
		name = smudgename;

		algorithms = new ArrayList<Algorithm>();
	}

	public void init() {
		System.out.println("Initializing smudge...");

		setSource(new Frame(filename));

		System.out.println("Setting up " + algorithms.size() + " algorithms...");
		for (Algorithm a : algorithms)
			a.init();

		System.out.println("Smudge initialized.");
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller c) {
		controller = c;

		if (controller.getSmudge() != this)
			controller.setSmudge(this);
	}

	public void downsample(int amount) {
		downsample = amount;
	}

	public void addAlgorithm(Algorithm alg) {
		algorithms.add(alg);
	}

	public ArrayList<Algorithm> getAlgorithms() {
		return algorithms;
	}

	public void setSource(Frame image) {
		originalSource = frame = image;
	}

	public synchronized Frame render() {
		Frame toRender;
		if (originalSource != null) {
			int w = Math.max(originalSource.getWidth() / downsample, 1);
			int h = Math.max(originalSource.getHeight() / downsample, 1);

			if (frame == null || frame.getWidth() != w || frame.getHeight() != h) {
				frame = originalSource.resize(w, h);
			}
		}

		toRender = frame.copy();

		for (Algorithm a : algorithms)
			a.apply(toRender);

		if (saveNextRender)
			outputFrame(toRender);

		return toRender;
	}

	boolean saveNextRender = false;

	public void save() {
		saveNextRender = true;
	}

	private void outputFrame(Frame f) {
		String output = "output/" + name + "_" + System.currentTimeMillis() + ".png";
		System.out.println("Saving smudge to " + output);

		Frame toSave = f.resize(originalSource.getWidth(), originalSource.getHeight());
		toSave.save(output);

		saveNextRender = false;
	}

}
