package io.smudgr.util.source.graph.alg;

import io.smudgr.util.source.graph.Graph;

/**
 * The abstract {@link GraphAlgorithm} class defines behavior for altering a
 * {@link Graph} model.
 */
public abstract class GraphAlgorithm {

	/**
	 * Execute the algorithm
	 *
	 * @param g
	 *            {@link GraphAlgorithm}
	 */
	public abstract void exectute(Graph g);

	/**
	 * Gets the name of this {@link GraphAlgorithm}
	 * 
	 * @return name
	 */
	public abstract String getName();

	@Override
	public String toString() {
		return getName();
	}
}
