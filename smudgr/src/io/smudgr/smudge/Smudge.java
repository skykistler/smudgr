package io.smudgr.smudge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import io.smudgr.controller.Controller;
import io.smudgr.smudge.alg.Algorithm;
import io.smudgr.smudge.param.BooleanParameter;
import io.smudgr.smudge.param.Parametric;
import io.smudgr.smudge.source.Frame;
import io.smudgr.smudge.source.Source;

public class Smudge extends Parametric implements Source {

	private BooleanParameter enabled = new BooleanParameter("Enable", this, true);

	private Controller controller;

	private Source source;
	private HashMap<Integer, Algorithm> algorithms = new HashMap<Integer, Algorithm>();
	private ArrayList<Algorithm> orderedAlgorithms = new ArrayList<Algorithm>();
	private ArrayList<Integer> algorithm_ids = new ArrayList<Integer>(1000);
	private Random idPicker = new Random();

	private volatile Frame lastFrame;
	private int downsample = 1;

	public Smudge() {
		for (int i = 0; i < 1000; i++)
			algorithm_ids.add(i);
	}

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
		if (source == null || source == this) {
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

	public Controller getController() {
		return controller;
	}

	public void setController(Controller c) {
		controller = c;

		if (c.getSmudge() != this)
			c.setSmudge(this);

		super.setController(c);

		for (Algorithm a : getAlgorithms())
			a.setController(c);
	}

	public void setDownsample(int amount) {
		if (amount > 0)
			downsample = amount;
	}

	public void add(Algorithm alg) {
		add(alg, getNewAlgorithmID());
	}

	public void add(Algorithm alg, int id_num) {
		alg.setID(id_num);
		pluckID(id_num);

		algorithms.put(id_num, alg);
		alg.setSmudge(this);

		orderedAlgorithms.add(alg);
	}

	public Algorithm getAlgorithm(int id) {
		return algorithms.get(id);
	}

	public ArrayList<Algorithm> getAlgorithms() {
		return orderedAlgorithms;
	}

	public void dispose() {
		if (source != null)
			source.dispose();
	}

	public String getName() {
		return "Smudge";
	}

	public int getNewAlgorithmID() {
		int index = idPicker.nextInt(algorithm_ids.size());
		int id = algorithm_ids.get(index);

		return id;
	}

	private void pluckID(int id) {
		for (int i = 0; i < algorithm_ids.size(); i++) {
			if (algorithm_ids.get(i) == id) {
				algorithm_ids.remove(i);
				return;
			}
		}
	}

}
