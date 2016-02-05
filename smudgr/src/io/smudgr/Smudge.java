package io.smudgr;

import java.util.ArrayList;

import io.smudgr.alg.Algorithm;
import io.smudgr.controller.Controller;
import io.smudgr.source.Frame;
import io.smudgr.source.Source;

public class Smudge {
	private Controller controller;

	private Source source;
	private ArrayList<Algorithm> algorithms;

	private int downsample = 1;

	public Smudge(Source s) {
		source = s;
		algorithms = new ArrayList<Algorithm>();
	}

	public void init() {
		System.out.println("Initializing smudge...");

		source.init();

		System.out.println("Setting up " + algorithms.size() + " algorithms...");
		for (Algorithm a : algorithms)
			a.init();

		System.out.println("Smudge initialized.");
	}

	public void update() {
		source.update();
	}

	public synchronized Frame render() {
		if (source == null)
			return null;

		Frame toRender = source.getFrame();

		int w = Math.max(toRender.getWidth() / downsample, 1);
		int h = Math.max(toRender.getHeight() / downsample, 1);

		toRender.resize(w, h);

		for (Algorithm a : algorithms)
			a.apply(toRender);

		if (saveNextRender)
			outputFrame(toRender);

		return toRender;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
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
		if (amount > 0)
			downsample = amount;
	}

	public void addAlgorithm(Algorithm alg) {
		algorithms.add(alg);
	}

	public ArrayList<Algorithm> getAlgorithms() {
		return algorithms;
	}

	boolean saveNextRender = false;

	public void save() {
		saveNextRender = true;
	}

	private void outputFrame(Frame f) {
		String output = "output/" + System.currentTimeMillis() + ".png";
		System.out.println("Saving smudge to " + output);

		Frame toSave = f.resize(source.getFrame().getWidth(), source.getFrame().getHeight());
		toSave.save(output);

		saveNextRender = false;
	}

}
