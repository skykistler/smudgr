package io.smudgr.project.smudge;

import java.util.ArrayList;

import io.smudgr.project.PropertyMap;
import io.smudgr.project.smudge.alg.Algorithm;
import io.smudgr.project.smudge.param.BooleanParameter;
import io.smudgr.project.smudge.param.NumberParameter;
import io.smudgr.project.smudge.param.Parametric;
import io.smudgr.project.smudge.source.AnimatedSource;
import io.smudgr.project.smudge.source.Source;
import io.smudgr.project.smudge.source.SourceSet;
import io.smudgr.project.smudge.util.Frame;

public class Smudge extends Parametric implements Source {

	private BooleanParameter enabled = new BooleanParameter("Enable", this, true);
	private NumberParameter downsample = new NumberParameter("Downsample", this, 1, .01, 1, .01);
	private NumberParameter sourceSpeed = new NumberParameter("Source Speed", this, 1, .25, 4, .05);

	private Source source;
	private ArrayList<Algorithm> algorithms = new ArrayList<Algorithm>();

	private volatile Frame lastFrame;

	public void init() {
		System.out.println("Initializing smudge...");

		if (source != null)
			source.init();

		downsample.setReverse(true);

		System.out.println("Smudge initialized.");
	}

	public void update() {
		if (source != null) {
			source.update();

			// Update speed factor if current source is animated
			Source s = source instanceof SourceSet ? ((SourceSet) source).getCurrentSource() : source;
			if (s instanceof AnimatedSource)
				((AnimatedSource) s).setSpeedFactor(sourceSpeed.getValue());
		}
	}

	public void render() {
		Frame toRender = null;

		if (source != null)
			toRender = source.getFrame().resize(downsample.getValue());

		if (toRender != null) {
			if (enabled.getValue()) {
				for (Algorithm a : getAlgorithms())
					a.apply(toRender);
			}
		}

		if (lastFrame != null)
			lastFrame.dispose();

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

		alg.init();
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

			pm.add(map);
		}
	}

	public void load(PropertyMap pm) {
		super.load(pm);

		for (PropertyMap map : pm.getChildren("algorithm")) {
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
