package io.smudgr.engine;

import java.util.ArrayList;

import io.smudgr.app.project.ProjectItem;
import io.smudgr.app.project.reflect.TypeLibrary;
import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.engine.param.BooleanParameter;
import io.smudgr.engine.param.NumberParameter;
import io.smudgr.engine.param.Parametric;
import io.smudgr.util.Frame;
import io.smudgr.util.source.AnimatedSource;
import io.smudgr.util.source.Source;
import io.smudgr.util.source.SourceSet;

/**
 * The {@link Rack} stores a deliberately ordered list of {@link Smudge} items
 * and applies them sequentially to the current source.
 */
public class Rack extends Parametric implements ProjectItem {

	/**
	 * This is needed since {@link Rack} isn't being loaded with reflection.
	 * <p>
	 * Currently set to {@value}.
	 */
	public static final String RACK_IDENTIFIER = "rack";

	@Override
	public String getName() {
		return "Rack";
	}

	/**
	 * Gets the tag used to identify {@link Rack} states in the save file
	 *
	 * @return {@link #RACK_IDENTIFIER}
	 */
	public String getIdentifier() {
		return RACK_IDENTIFIER;
	}

	// Currently active smudges
	private ArrayList<Smudge> smudges;

	// Available Smudge types
	private TypeLibrary<Smudge> smudgeLibrary;

	/* These parameters control runtime settings for the rack */
	private BooleanParameter enabled = new BooleanParameter("Enable", this, true);
	private NumberParameter downsample = new NumberParameter("Downsample", this, 1, .01, 1, .01);
	private NumberParameter sourceSpeed = new NumberParameter("Source Speed", this, 1, .25, 4, .05);

	/*
	 * The rack manages the current source and keeps a buffer of the last frame
	 */
	private Source source;
	private volatile Frame lastFrame;

	/**
	 * Initialize the smudge rack.
	 */
	public void init() {
		System.out.println("Initializing rack...");

		if (source != null)
			source.init();

		System.out.println("Rack initialized.");
	}

	/**
	 * Update every added smudge.
	 */
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
	 * Render every smudge to the current frame.
	 *
	 * @see Rack#getLastFrame()
	 */
	public void render() {
		Frame toRender = null;

		if (source != null)
			toRender = source.getFrame();

		if (toRender != null) {
			toRender = toRender.resize(downsample.getValue());

			if (enabled.getValue()) {
				for (Smudge s : getSmudges())
					s.render(toRender);
			}
		}

		if (lastFrame != null)
			lastFrame.dispose();

		lastFrame = toRender;
	}

	/**
	 * Use a {@link PropertyMap} representing the state of a {@link Smudge} to
	 * add to this {@link Rack}
	 *
	 * @param state
	 *            {@link PropertyMap} with {@code type} attribute, with or
	 *            without ID
	 */
	public void add(PropertyMap state) {
		Smudge smudge = smudgeLibrary.getNewInstance(state.getAttribute("type"));

		// If state already has a project ID,
		if (state.hasAttribute("id"))
			getProject().put(smudge, Integer.parseInt(state.getAttribute("id")));
		else
			getProject().add(smudge);

		getSmudges().add(smudge);

		smudge.init();
	}

	@Override
	public void save(PropertyMap pm) {
		super.save(pm);

		for (Smudge smudge : getSmudges()) {
			PropertyMap map = new PropertyMap(smudgeLibrary.getTypeIdentifier());

			map.setAttribute("id", getProject().getId(smudge));
			map.setAttribute("type", smudge.getIdentifier());

			smudge.save(map);

			pm.add(map);
		}
	}

	@Override
	public void load(PropertyMap pm) {
		super.load(pm);

		smudgeLibrary = new TypeLibrary<Smudge>(Smudge.class);

		for (PropertyMap map : pm.getChildren(smudgeLibrary.getTypeIdentifier())) {
			add(map);
		}
	}

	/**
	 * Get the latest finished rendered frame.
	 *
	 * @return {@link Frame}
	 */
	public Frame getLastFrame() {
		return lastFrame;
	}

	/**
	 * Gets all of the smudges contained by this {@link Rack}
	 *
	 * @return {@code ArrayList<Smudge>}
	 */
	public ArrayList<Smudge> getSmudges() {
		return smudges;
	}

	/**
	 * Gets the current source being operated on by the {@link Rack}
	 *
	 * @return {@link Source}
	 * @see Rack#setSource(Source)
	 */
	public Source getSource() {
		return source;
	}

	/**
	 * Sets the current source to operate the {@link Rack} on
	 *
	 * @param source
	 *            {@link Source}
	 */
	public void setSource(Source source) {
		if (source != this)
			this.source = source;
	}

}