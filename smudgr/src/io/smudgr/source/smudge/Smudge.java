package io.smudgr.source.smudge;

import java.util.ArrayList;

import io.smudgr.controller.Controller;
import io.smudgr.out.GifOutput;
import io.smudgr.out.ImageOutput;
import io.smudgr.out.Output;
import io.smudgr.source.Frame;
import io.smudgr.source.Source;
import io.smudgr.source.smudge.alg.Algorithm;
import io.smudgr.source.smudge.param.BooleanParameter;
import io.smudgr.source.smudge.param.Parametric;

public class Smudge extends Parametric implements Source {
	private Controller controller;

	private Source source;
	private ArrayList<Algorithm> algorithms;
	private BooleanParameter enabled = new BooleanParameter("Enable", this, true);

	private Frame lastFrame;
	private int downsample = 1;

	private Output output;

	public Smudge() {
		algorithms = new ArrayList<Algorithm>();
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
		if (source != null)
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

		if (enabled.getValue())
			for (Algorithm a : algorithms)
				a.apply(toRender);

		if (output != null)
			output.addFrame(toRender);

		return lastFrame = toRender;
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

		if (controller.getSmudge() != this)
			controller.setSmudge(this);

		super.setController(c);

		for (Algorithm a : algorithms)
			a.setController(c);
	}

	public void setDownsample(int amount) {
		if (amount > 0)
			downsample = amount;
	}

	public void add(Algorithm alg) {
		algorithms.add(alg);
		alg.setSmudge(this);
	}

	public void save(String path) {

	}

	public void saveFrame() {
		output = new ImageOutput(Long.toString(System.currentTimeMillis()), lastFrame.getWidth(), lastFrame.getHeight());
		output.addFrame(lastFrame);
		output.close();
	}

	public void startGifOutput() {
		output = new GifOutput(Long.toString(System.currentTimeMillis()), 30, true);
		output.addFrame(lastFrame);
	}

	public void finishGifOutput() {
		output.close();
		output = null;
	}

	public void dispose() {
		if (source != null)
			source.dispose();

		if (output != null)
			output.close();
	}

	public String getName() {
		return "Smudge";
	}

}
