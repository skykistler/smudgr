package io.smudgr;

import java.util.ArrayList;

import io.smudgr.alg.Algorithm;
import io.smudgr.model.Frame;

public class Smudge {
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

		setSource(new Frame("../data/" + filename));

		System.out.println("Setting up " + algorithms.size() + " algorithms...");
		for (Algorithm a : algorithms)
			a.init();

		System.out.println("Smudge initialized.");
	}

	public void downsample(int amount) {
		downsample = amount;

		if (originalSource != null) {
			frame = originalSource.copy();
			frame.resize(frame.getWidth() / downsample, frame.getWidth() / downsample);
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
		synchronized (this) {
			for (Algorithm a : algorithms) {
				Frame mix = frame.copy();
				a.execute(mix);
				frame = a.mask(frame, mix, a.getMask());

			}

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

		Frame toSave = frame.copy();
		toSave.resize(originalSource.getWidth(), originalSource.getHeight());
		toSave.save(output);

		saveNextRender = false;
	}

}
