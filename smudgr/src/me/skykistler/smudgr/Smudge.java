package me.skykistler.smudgr;

import java.util.ArrayList;

import me.skykistler.smudgr.alg.Algorithm;
import me.skykistler.smudgr.view.View;
import processing.core.PImage;

public class Smudge {
	private ArrayList<Algorithm> algorithms;
	private String name;
	private PImage source;
	private View processor;

	private PImage frame;

	public Smudge(View view, String filename) {
		this(view);
		source = processor.loadImage("data/" + filename);

		name = filename.substring(0, filename.lastIndexOf("."));
	}

	public Smudge(View view) {
		processor = view;
	}

	public void downsample(int amount) {
		source.resize(source.width / amount, source.height / amount);
	}

	public void addAlgorithm(Algorithm alg) {
		algorithms.add(alg);
	}

	public PImage render() {
		frame = source.copy();

		for (Algorithm alg : algorithms) {
			alg.execute(frame);
		}
		frame.updatePixels();

		return frame;
	}

	public void saveFrame() {
		frame.save("output/" + name + "_" + System.currentTimeMillis() + ".png");
	}
}
