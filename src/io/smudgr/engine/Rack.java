package io.smudgr.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.engine.param.BooleanParameter;
import io.smudgr.engine.param.NumberParameter;
import io.smudgr.engine.param.Parametric;
import io.smudgr.util.Frame;
import io.smudgr.util.PixelFrame;
import io.smudgr.util.source.Source;
import io.smudgr.util.source.SourceSet;
import io.smudgr.util.source.image.AnimatedSource;

/**
 * The {@link Rack} stores a deliberately ordered list of {@link Smudge} items
 * and applies them sequentially to the current source.
 */
public class Rack extends Parametric {

	@Override
	public String getTypeCategoryName() {
		return "Rack";
	}

	@Override
	public String getTypeCategoryIdentifier() {
		return "rack";
	}

	@Override
	public String getTypeName() {
		return getTypeCategoryName();
	}

	@Override
	public String getTypeIdentifier() {
		return getTypeCategoryIdentifier();
	}

	// Currently active smudges
	private List<Smudge> smudges = Collections.synchronizedList(new ArrayList<Smudge>());

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
		if (!enabled.getValue())
			return;

		Frame nextFrame = null;

		// If source is null or gives a null frame, clear last frame and return
		if (source == null || (nextFrame = source.getFrame()) == null) {
			setLastFrame(null);
			return;
		}

		// TODO: Desperately need to get rid of this and make PixelFrameRack
		// implementation
		if (nextFrame instanceof PixelFrame) {
			// Downsample according to the downsample parameter
			nextFrame = ((PixelFrame) nextFrame).resize(downsample.getValue());
		}

		// Render each smudge successively
		synchronized (smudges) {
			for (Smudge s : smudges)
				if (s.isEnabled())
					nextFrame = s.smudge(nextFrame);
		}

		setLastFrame(nextFrame);
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
		Smudge smudge = getProject().getSmudgeLibrary().getNewInstance(state);
		smudge.load(state);

		add(smudge);
	}

	/**
	 * Adds the given {@link Smudge} to the end of this {@link Rack}
	 *
	 * @param smudge
	 *            {@link Smudge}
	 */
	public void add(Smudge smudge) {
		synchronized (smudges) {
			getProject().add(smudge);
			getSmudges().add(smudge);

			smudge.onInit();
		}
	}

	/**
	 * Move a {@link Smudge} from the first index to the second index.
	 *
	 * @param fromIndex
	 *            first index
	 * @param toIndex
	 *            second index
	 */
	public void move(int fromIndex, int toIndex) {
		synchronized (smudges) {
			Smudge toMove = smudges.remove(fromIndex);
			smudges.add(toIndex, toMove);
		}
	}

	/**
	 * Remove a {@link Smudge} instance from this {@link Rack}. This will remove
	 * the instance from the project.
	 * 
	 * @param smudge
	 *            {@link Smudge}
	 * @return {@code true} if {@link Rack} contained {@link Smudge} and removed
	 *         successfully, {@code false} if otherwise
	 */
	public boolean remove(Smudge smudge) {
		synchronized (smudges) {
			boolean removed = smudges.remove(smudge);

			if (removed)
				getProject().remove(smudge);

			return removed;
		}
	}

	@Override
	public void save(PropertyMap pm) {
		super.save(pm);

		for (Smudge smudge : getSmudges()) {
			PropertyMap map = new PropertyMap(smudge);
			pm.add(map);
		}
	}

	@Override
	public void load(PropertyMap pm) {
		super.load(pm);

		for (PropertyMap map : pm.getChildren(getProject().getSmudgeLibrary())) {
			add(map);
		}
	}

	/**
	 * Get the latest finished processed frame.
	 *
	 * @return {@link Frame}
	 */
	public Frame getLastFrame() {
		return lastFrame;
	}

	protected void setLastFrame(Frame nextFrame) {
		if (lastFrame != null)
			lastFrame.dispose();

		lastFrame = nextFrame;
	}

	/**
	 * Gets all of the smudges contained by this {@link Rack}
	 *
	 * @return {@code List<Smudge>}
	 */
	public List<Smudge> getSmudges() {
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
