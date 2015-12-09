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

	private int downsample = 1;

	public Smudge(String filename) {
		this.filename = filename;
		name = filename.substring(0, filename.lastIndexOf("."));

		algorithms = new ArrayList<Algorithm>();
	}

	public void init(View view) throws FileNotFoundException {
		processor = view;

		System.out.println("Initializing smudge...");

		if (!(new File("data/" + filename).exists())) {
			throw new FileNotFoundException("Could not find file: data/" + filename);
		}

		source = processor.loadImage("../data/" + filename);

		if (downsample > 1)
			source.resize(source.width / downsample, source.height / downsample);

		source.loadPixels();

		System.out.println("Smudge initialized.");
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

	boolean saved = true;

	public PImage render() {
		frame = source.copy();

		for (int i = 0; i < algorithms.size(); i++) {
			algorithms.get(i).execute(processor, frame);
		}
		frame.updatePixels();

		if (!saved)
			saveFrame();

		return frame;
	}

	public void save() {
		saved = false;
	}

	public void saveFrame() {
		String output = "output/" + name + "_" + System.currentTimeMillis() + ".png";
		System.out.println("Saving smudge to " + output);
		frame.save(new File(output).getAbsolutePath());
		saved = true;
	}
}
