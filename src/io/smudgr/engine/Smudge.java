package io.smudgr.engine;

import java.util.ArrayList;

import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.engine.alg.Algorithm;
import io.smudgr.engine.param.BooleanParameter;
import io.smudgr.engine.param.NumberParameter;
import io.smudgr.engine.param.Parametric;
import io.smudgr.util.Frame;
import io.smudgr.util.source.AnimatedSource;
import io.smudgr.util.source.Source;
import io.smudgr.util.source.SourceSet;

/**
 * TODO: Refactor soon
 * <p>
 * The {@link Smudge} class is a {@link Parametric} {@link Algorithm} container.
 * The application instance attempts to render the {@link Smudge} at a constant
 * framerate.
 */
public class Smudge extends Parametric implements Source {

	/**
	 * Project save file identifier
	 */
	public static final String PROJECT_MAP_TAG = "smudge";

	private BooleanParameter enabled = new BooleanParameter("Enable", this, true);
	private NumberParameter downsample = new NumberParameter("Downsample", this, 1, .01, 1, .01);
	private NumberParameter sourceSpeed = new NumberParameter("Source Speed", this, 1, .25, 4, .05);

	private Source source;
	private ArrayList<Algorithm> algorithms = new ArrayList<Algorithm>();

	private volatile Frame lastFrame;

	@Override
	public void init() {
		System.out.println("Initializing smudge...");

		if (source != null)
			source.init();

		downsample.setReverse(true);

		System.out.println("Smudge initialized.");
	}

	@Override
	public void update() {
		if (source != null) {
			source.update();

			// Update speed factor if current source is animated
			Source s = source instanceof SourceSet ? ((SourceSet) source).getCurrentSource() : source;
			if (s instanceof AnimatedSource)
				((AnimatedSource) s).setSpeed(sourceSpeed.getValue());
		}
	}

	/**
	 * Render the smudge to the current frame.
	 *
	 * @see Smudge#getFrame()
	 */
	public void render() {
		Frame toRender = null;

		if (source != null)
			toRender = source.getFrame();

		if (toRender != null) {
			toRender = toRender.resize(downsample.getValue());

			if (enabled.getValue()) {
				for (Algorithm a : getAlgorithms())
					a.apply(toRender);
			}
		}

		if (lastFrame != null)
			lastFrame.dispose();

		lastFrame = toRender;
	}

	@Override
	public Frame getFrame() {
		return lastFrame;
	}

	/**
	 * Gets the current source being operated on by this {@link Smudge}
	 *
	 * @return {@link Source}
	 * @see Smudge#setSource(Source)
	 */
	public Source getSource() {
		return source;
	}

	/**
	 * Sets the current source to operate this {@link Smudge} on
	 *
	 * @param source
	 *            {@link Source}
	 */
	public void setSource(Source source) {
		if (source != this)
			this.source = source;
	}

	/**
	 * Add an algorithm to this {@link Smudge}
	 *
	 * @param alg
	 *            {@link Algorithm}
	 */
	public void add(Algorithm alg) {
		if (algorithms.contains(alg))
			return;

		getProject().add(alg);

		algorithms.add(alg);

		alg.init();
	}

	/**
	 * Gets all of the algorithms contained by this {@link Smudge}
	 *
	 * @return {@code ArrayList<Algorithm>}
	 */
	public ArrayList<Algorithm> getAlgorithms() {
		return algorithms;
	}

	@Override
	public void save(PropertyMap pm) {
		super.save(pm);

		for (Algorithm alg : getAlgorithms()) {
			PropertyMap map = new PropertyMap(Algorithm.PROJECT_MAP_TAG);
			alg.save(map);

			pm.add(map);
		}
	}

	@Override
	public void load(PropertyMap pm) {
		super.load(pm);

		for (PropertyMap map : pm.getChildren(Algorithm.PROJECT_MAP_TAG)) {
			Algorithm alg = new Algorithm();
			alg.load(map);

			add(alg);
		}
	}

	@Override
	public void dispose() {
		if (source != null)
			source.dispose();
	}

	@Override
	public String getName() {
		return "Smudge";
	}

}
