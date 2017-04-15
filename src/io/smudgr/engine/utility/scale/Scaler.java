package io.smudgr.engine.utility.scale;

import io.smudgr.engine.param.NumberParameter;
import io.smudgr.engine.utility.UtilityComponent;
import io.smudgr.util.Frame;

/**
 * The {@link Scaler} class defines a {@link UtilityComponent} that allows
 * over-sampling.
 */
public class Scaler extends UtilityComponent {

	// Start at normal resolution.
	private NumberParameter scale = new NumberParameter("Scale Factor", this, 1, 0.01, 1.5, 0.01);

	@Override
	public String getTypeName() {
		return "Scaler";
	}

	@Override
	public String getComponentTypeName() {
		return "Scaler";
	}

	@Override
	public String getComponentTypeIdentifier() {
		return "scaler";
	}

	/**
	 * Returns current scale factor.
	 * 
	 * @return double
	 */
	public double getZoomFactor() {
		return scale.getValue();
	}

	/**
	 * Over or under samples the given Frame based on a scale parameter.
	 * 
	 * @param f
	 *            {@link Frame}
	 * @return {@link Frame}
	 */
	public Frame scale(Frame f) {
		double currentZoom = scale.getValue();

		if (currentZoom == 1.0)
			return f;

		// God bless
		Frame scaledFrame = f.resize(currentZoom);
		f.dispose();

		return scaledFrame;
	}

}
