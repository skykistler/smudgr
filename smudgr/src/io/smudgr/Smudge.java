package io.smudgr;

import java.util.ArrayList;

import io.smudgr.alg.Algorithm;
import io.smudgr.controller.Controller;
import io.smudgr.controller.controls.Controllable;
import io.smudgr.model.Frame;

public class Smudge {
	private Controller controller;

	private ArrayList<Algorithm> algorithms;
	private String filename;
	private String name;
	private Frame originalSource;

	private Frame frame;

	private int downsample = 1;

	private boolean showFPS = true;
	private int frameCount;
	private long lastSecond;

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

		setSource(new Frame("../data/" + filename));

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

		if (originalSource != null) {
			int w = originalSource.getWidth() / downsample;
			int h = originalSource.getHeight() / downsample;
			frame = originalSource.resize(w, h);
		}
	}

	public void addAlgorithm(Algorithm alg) {
		algorithms.add(alg);
	}

	public ArrayList<Algorithm> getAlgorithms() {
		return algorithms;
	}

	public void setSource(Frame image) {
		originalSource = image;

		downsample(downsample);
	}

	public Frame render() {
		for (Controllable c : controller.getControls())
			c.update();

		if (lastSecond == 0)
			lastSecond = System.nanoTime();

		Frame toRender;
		synchronized (this) {
			toRender = frame.copy();
		}

		for (Algorithm a : algorithms) {
			// Frame mix = frame.copy();
			// a.execute(mix);
			// frame = a.mask(frame, mix, a.getMask());

			a.execute(toRender);
		}

		frameCount++;

		if (System.nanoTime() - lastSecond > 1000000000) {
			if (showFPS)
				System.out.println(frameCount + "fps");

			lastSecond = 0;
			frameCount = 0;
		}

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
