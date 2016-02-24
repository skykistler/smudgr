package io.smudgr.source.smudge;

import java.util.ArrayList;

import io.smudgr.controller.SmudgeController;
import io.smudgr.source.Frame;
import io.smudgr.source.Source;
import io.smudgr.source.smudge.alg.Algorithm;

public class Smudge implements Source {
	private SmudgeController controller;

	private Source source;
	private ArrayList<Algorithm> algorithms;
	private boolean enabled;

	private Frame lastFrame;
	private int downsample = 1;

	public Smudge(Source s) {
		setSource(s);
		algorithms = new ArrayList<Algorithm>();
		setEnabled(true);
	}

	public void init() {
		System.out.println("Initializing smudge...");

		if (source != null)
			source.init();

		System.out.println("Setting up " + algorithms.size() + " algorithms...");
		for (Algorithm a : algorithms)
			a.init();

		System.out.println("Smudge initialized.");
	}

	public void update() {
		source.update();

		for (Algorithm a : algorithms)
			a.update();
	}

	public synchronized Frame getFrame() {
		if (source == null || source == this)
			return null;

		Frame toRender = source.getFrame();

		if (toRender == null)
			return lastFrame;

		if (downsample > 1) {
			int w = Math.max(toRender.getWidth() / downsample, 1);
			int h = Math.max(toRender.getHeight() / downsample, 1);

			toRender = toRender.resize(w, h);
		} else {
			toRender = toRender.copy();
		}

		if (enabled)
			for (Algorithm a : algorithms)
				a.apply(toRender);

		if (saveNextRender)
			outputFrame(toRender);

		return lastFrame = toRender;
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		if (source != this)
			this.source = source;
	}

	public SmudgeController getController() {
		return controller;
	}

	public void setController(SmudgeController c) {
		controller = c;

		if (controller.getSmudge() != this)
			controller.setSmudge(this);
	}

	public void setDownsample(int amount) {
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

	public void dispose() {

	}

}
