package io.smudgr.engine.alg;

import java.util.ArrayList;

import io.smudgr.engine.SmudgeComponent;
import io.smudgr.engine.param.Parametric;

/**
 * The abstract {@link AlgorithmComponent} class defines a generic
 * {@link Parametric} that can be added to a parent {@link Algorithm}.
 * <p>
 * {@link AlgorithmComponent} implementations will be executed by a parent
 * {@link Algorithm} depending on their type (as given by
 * {@link AlgorithmComponent#getTypeIdentifier()}), and are intended to
 * have a behavioral effect on the parent {@link Algorithm}
 */
public abstract class AlgorithmComponent extends SmudgeComponent {

	@Override
	public String getSmudgeTypeIdentifier() {
		return "algorithm";
	}

	@Override
	public String getIdentifier() {
		return getName();
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Gets the collection of {@link PixelIndexList}s that specify which pixels
	 * should be currently acted on by the algorithm.
	 *
	 * @return list of selected pixel lists
	 */
	protected ArrayList<PixelIndexList> getSelectedPixels() {
		return getAlgorithm().getSelectedPixels();
	}

	protected Algorithm getAlgorithm() {
		return (Algorithm) getParent();
	}

}
