package io.smudgr.project.smudge;

import java.util.ArrayList;

import io.smudgr.project.PropertyMap;
import io.smudgr.project.smudge.alg.Algorithm;
import io.smudgr.project.smudge.param.BooleanParameter;
import io.smudgr.project.smudge.param.NumberParameter;
import io.smudgr.project.smudge.param.Parametric;
import io.smudgr.project.smudge.source.Frame;
import io.smudgr.project.smudge.source.Source;

public class Smudge extends Parametric implements Source {

	private BooleanParameter enabled = new BooleanParameter("Enable", this, true);
	private NumberParameter downsample = new NumberParameter("Downsample", this, 1, 1, 255);

	private Source source;
	private ArrayList<Algorithm> algorithms = new ArrayList<Algorithm>();

	private volatile Frame lastFrame;

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

		int ds = downsample.getIntValue();
		if (ds > 1) {
			int w = Math.max(toRender.getWidth() / ds, 1);
			int h = Math.max(toRender.getHeight() / ds, 1);

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

	public void add(Algorithm alg) {
		if (algorithms.contains(alg))
			return;

		getProject().add(alg);

		algorithms.add(alg);
	}

	public Algorithm getAlgorithm(int id) {
		return algorithms.get(id);
	}

	public ArrayList<Algorithm> getAlgorithms() {
		return algorithms;
	}

	public void save(PropertyMap pm) {
		super.save(pm);

		for (Algorithm alg : getAlgorithms()) {
			PropertyMap map = new PropertyMap("algorithm");

			alg.save(map);

			pm.add(pm);
		}
	}

	public void load(PropertyMap pm) {
		super.load(pm);

		ArrayList<PropertyMap> children = pm.getChildren("algorithm");

		for (PropertyMap map : children) {
			Algorithm alg = new Algorithm();
			alg.load(map);

			add(alg);
		}
	}

	public void dispose() {
		if (source != null)
			source.dispose();
	}

	public String getName() {
		return "Smudge";
	}

}
