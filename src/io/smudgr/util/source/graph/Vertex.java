package io.smudgr.util.source.graph;

import java.util.ArrayList;

/**
 * The {@link Vertex} class represents an arbitrary 2d point in space.
 */
public class Vertex {
	private double position[] = new double[2];
	private ArrayList<Vertex> adj = new ArrayList<Vertex>();

	/**
	 * Create a new vertex.
	 *
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 */
	public Vertex(double x, double y) {
		position[0] = x;
		position[1] = y;
	}

	/**
	 * Gets the x coordinate of this vertex.
	 *
	 * @return x coordinate
	 */
	public double getX() {
		return position[0];
	}

	/**
	 * Gets the y coordinate of this vertex.
	 *
	 * @return y coordinate
	 */
	public double getY() {
		return position[1];
	}

	/**
	 * Sets the x coordinate of this vertex.
	 *
	 * @param x
	 *            coordinate
	 */
	public void setX(double x) {
		position[0] = x;
	}

	/**
	 * Sets the y coordinate of this vertex.
	 *
	 * @param y
	 *            coordinate
	 */
	public void setY(double y) {
		position[1] = y;
	}

	/**
	 * Gets container for arbitrary 'adjacent' vertices, used for graph
	 * algorithms.
	 *
	 * @return {@code ArrayList<Vertex>} of adjacent vertices
	 */
	public ArrayList<Vertex> getAdjacent() {
		return adj;
	}

}