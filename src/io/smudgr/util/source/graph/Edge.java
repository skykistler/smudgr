package io.smudgr.util.source.graph;

import io.smudgr.util.source.graph.alg.GraphAlgorithm;

/**
 * The {@link Edge} class represents a set of two connected {@link Vertex}
 * instances, with a configurable opacity and an extra 'weight' parameter (for
 * {@link GraphAlgorithm} usage).
 */
public class Edge {
	private Vertex start, end;
	private float weight, opacity;

	/**
	 * Create a new {@link Edge} with the given vertices
	 *
	 * @param v1
	 *            {@link Vertex}
	 * @param v2
	 *            {@link Vertex}
	 */
	public Edge(Vertex v1, Vertex v2) {
		this(v1, v2, Float.NaN, 0.1f);
	}

	/**
	 * Create a new {@link Edge} with the given vertices
	 *
	 * @param v1
	 *            {@link Vertex}
	 * @param v2
	 *            {@link Vertex}
	 * @param weight
	 *            arbitrary parameter for {@link GraphAlgorithm} usage
	 * @param opacity
	 *            to render this {@link Edge} at
	 */
	public Edge(Vertex v1, Vertex v2, float weight, float opacity) {
		start = v1;
		end = v2;
		this.weight = weight;
		this.opacity = opacity;

		v1.getAdjacent().add(v2);
		v2.getAdjacent().add(v1);
	}

	/**
	 * Gets the first {@link Vertex} comprising this {@link Edge}
	 *
	 * @return {@link Vertex}
	 */
	public Vertex getStart() {
		return start;
	}

	/**
	 * Sets the first {@link Vertex} comprising this {@link Edge}
	 *
	 * @param v
	 *            {@link Vertex}
	 */
	public void setStart(Vertex v) {
		start = v;
	}

	/**
	 * Gets the end {@link Vertex} comprising this {@link Edge}
	 *
	 * @return {@link Vertex}
	 */
	public Vertex getEnd() {
		return end;
	}

	/**
	 * Sets the end {@link Vertex} comprising this {@link Edge}
	 *
	 * @param v
	 *            {@link Vertex}
	 */
	public void setEnd(Vertex v) {
		end = v;
	}

	/**
	 * Gets the weight of this {@link Edge}
	 *
	 * @return arbitrary weight value
	 */
	public float getWeight() {
		return weight;
	}

	/**
	 * Sets the weight of this {@link Edge}
	 *
	 * @param w
	 *            arbitrary weight value
	 */
	public void setWeight(float w) {
		weight = w;
	}

	/**
	 * Gets the opacity of this {@link Edge}
	 *
	 * @return opacity 0-1
	 */
	public float getOpacity() {
		return opacity;
	}

	/**
	 * Sets the opacity of this {@link Edge}
	 *
	 * @param o
	 *            opacity 0-1
	 */
	public void setOpacity(float o) {
		opacity = o;
	}

	/**
	 * Compare this {@link Edge} to another
	 *
	 * @param e
	 *            {@link Edge}
	 * @return {@code true} if and only if the given {@link Edge} has vertices
	 *         that match this {@link Edge}, either in forward or reverse order
	 */
	public boolean compare(Edge e) {
		if (getStart() == e.getStart() && getEnd() == e.getEnd())
			return true;
		if (getStart() == e.getEnd() && getEnd() == e.getStart())
			return true;
		return false;
	}

}