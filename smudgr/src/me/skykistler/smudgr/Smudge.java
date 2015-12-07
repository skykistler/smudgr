package me.skykistler.smudgr;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import me.skykistler.smudgr.alg.Algorithm;
import me.skykistler.smudgr.view.View;
import processing.core.PImage;

public class Smudge {
	private ArrayList<Algorithm> algorithms;
	private String filename;
	private String name;
	private PImage source;
	private View processor;

	private PImage frame;

	public Smudge(View view, String filename) {
		processor = view;

		this.filename = filename;
		name = filename.substring(0, filename.lastIndexOf("."));

		algorithms = new ArrayList<Algorithm>();
	}

	public void init() throws FileNotFoundException {
		System.out.println("Initializing smudge...");

		if (!(new File("data/" + filename).exists())) {
			throw new FileNotFoundException("Could not find file: data/" + filename);
		}

		source = processor.loadImage("../data/" + filename);
		source.loadPixels();

		System.out.println("Smudge initialized.");
	}

	public void downsample(int amount) {
		source.resize(source.width / amount, source.height / amount);
	}

	public void addAlgorithm(Algorithm alg) {
		algorithms.add(alg);
	}

	public PImage render() {
		frame = source.copy();

		frame.loadPixels();
		for (Algorithm alg : algorithms) {
			alg.execute(processor, frame);
		}
		frame.updatePixels();

		return frame;
	}

	public void saveFrame() {
		String output = "output/" + name + "_" + System.currentTimeMillis() + ".png";
		System.out.println("Saving smudge to " + output);
		frame.save(new File(output).getAbsolutePath());
	}
}
