package io.smudgr.util.source.graph;

import java.util.ArrayList;

/**
 * The {@link Shape} class represents a container of {@link Vertex} instances,
 * with specified fill properties like RGB and opacity.
 */
public class Shape {
	private ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	private float opacity, red, green, blue;

	/**
	 * Create a new shape with the given opacity and RGB values.
	 *
	 * @param opacity
	 *            0-1
	 * @param red
	 *            0-1
	 * @param green
	 *            0-1
	 * @param blue
	 *            0-1
	 */
	public Shape(float opacity, float red, float green, float blue) {
		this.opacity = opacity;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	/**
	 * Gets the vertices that represent this {@link Shape}
	 *
	 * @return list of vertices
	 */
	public ArrayList<Vertex> getVertices() {
		return vertices;
	}

	/**
	 * Add a {@link Vertex} to this {@link Shape}
	 *
	 * @param v
	 *            {@link Vertex}
	 */
	public void addVertex(Vertex v) {
		for (Vertex p : vertices) {
			if (p.getX() == v.getX() && p.getY() == v.getY())
				return;
		}

		vertices.add(v);
	}

	/**
	 * Add the vertices of an {@link Edge} to this {@link Shape}
	 *
	 * @param e
	 *            {@link Edge}
	 */
	public void addEdge(Edge e) {
		addVertex(e.getStart());
		addVertex(e.getEnd());
	}

	/**
	 * Gets the opacity setting of this {@link Shape}
	 *
	 * @return opacity value 0-1
	 */
	public float getOpacity() {
		return opacity;
	}

	/**
	 * Sets the opacity setting for this {@link Shape}
	 *
	 * @param o
	 *            opacity value 0-1
	 */
	public void setOpacity(float o) {
		opacity = o;
	}

	/**
	 * Gets the RGB components that represent the color of this {@link Shape}
	 *
	 * @return RGB components as {@code float[]}, values are 0-1
	 */
	public float[] getColor() {
		return new float[] { red, green, blue };
	}

}