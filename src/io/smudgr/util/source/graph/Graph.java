package io.smudgr.util.source.graph;

import java.util.ArrayList;

import io.smudgr.util.source.graph.alg.GraphAlgorithm;

/**
 * The {@link Graph} class represents a collection of 2D {@link Shape}
 * instances, {@link Vertex} instances, and {@link Edge} instances to be acted
 * upon by {@link GraphAlgorithm} classes and rendered by a
 * {@link GraphRenderer}
 */
public class Graph {

	private int width, height;

	private ArrayList<Shape> polygons = new ArrayList<Shape>();
	private ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	private ArrayList<Edge> edges = new ArrayList<Edge>();
	private ArrayList<GraphAlgorithm> graphAlgorithms = new ArrayList<GraphAlgorithm>();

	/**
	 * Create a new {@link Graph} with the given dimensions
	 *
	 * @param width
	 *            dimension
	 * @param height
	 *            dimension
	 */
	public Graph(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Add a {@link GraphAlgorithm} to be executed
	 *
	 * @param ga
	 *            {@link GraphAlgorithm}
	 */
	public void addAlgorithm(GraphAlgorithm ga) {
		graphAlgorithms.add(ga);
	}

	/**
	 * Gets the width of this {@link Graph}
	 *
	 * @return width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets the height of this {@link Graph}
	 *
	 * @return height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets the polygons in this {@link Graph}
	 *
	 * @return list of {@link Shape} instances
	 */
	public ArrayList<Shape> getPolygons() {
		return polygons;
	}

	/**
	 * Gets the independent vertices in this {@link Graph}
	 *
	 * @return list of {@link Vertex} instances
	 */
	public ArrayList<Vertex> getVertices() {
		return vertices;
	}

	/**
	 * Gets the independent edges in this {@link Graph}
	 *
	 * @return list of {@link Edge} instances
	 */
	public ArrayList<Edge> getEdges() {
		return edges;
	}

	/**
	 * Gets the graph algorithms acting on this {@link Graph}
	 *
	 * @return list of {@link GraphAlgorithm} instances
	 */
	public ArrayList<GraphAlgorithm> getGraphAlgorithms() {
		return graphAlgorithms;
	}

}
