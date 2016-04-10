package io.smudgr.smudge;

import java.util.ArrayList;

import io.smudgr.smudge.alg.Algorithm;
import io.smudgr.smudge.param.BooleanParameter;
import io.smudgr.smudge.param.Parametric;
import io.smudgr.smudge.source.Frame;
import io.smudgr.smudge.source.Source;

public class Smudge extends Parametric implements Source {

	private BooleanParameter enabled = new BooleanParameter("Enable", this, true);

	private Source source;
	private ArrayList<Algorithm> algorithms = new ArrayList<Algorithm>();

	private volatile Frame lastFrame;
	private int downsample = 1;

	public void init() {
		System.out.println("Initializing smudge...");

		if (source != null)
			source.init();

		System.out.println("Setting up " + algorithms.size() + " algorithms...");
		for (Algorithm a : getAlgorithms())
			a.init();

		System.out.println("Smudge initialized.");
	}

	public void update() {
		if (source != null)
			source.update();
	}

	public void render() {
		if (source == null) {
			lastFrame = null;
			return;
		}

		Frame toRender = source.getFrame();
		if (toRender == null) {
			lastFrame = null;
			return;
		}

		if (downsample > 1) {
			int w = Math.max(toRender.getWidth() / downsample, 1);
			int h = Math.max(toRender.getHeight() / downsample, 1);

			toRender = toRender.resize(w, h);
		} else {
			toRender = toRender.copy();
		}

		if (enabled.getValue())
			for (Algorithm a : getAlgorithms())
				a.apply(toRender);

		lastFrame = toRender;
	}

	public Frame getFrame() {
		return lastFrame;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		if (source != this)
			this.source = source;
	}

	public void setDownsample(int amount) {
		if (amount > 0)
			downsample = amount;
	}

	public void add(Algorithm alg) {
		if (algorithms.contains(alg))
			return;

		getIdManager().add(alg);

		algorithms.add(alg);
	}

	public Algorithm getAlgorithm(int id) {
		return algorithms.get(id);
	}

	public ArrayList<Algorithm> getAlgorithms() {
		return algorithms;
	}

	public void dispose() {
		if (source != null)
			source.dispose();
	}

	public String getName() {
		return "Smudge";
	}

}
