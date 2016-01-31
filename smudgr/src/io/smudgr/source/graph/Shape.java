package io.smudgr.source.graph;

import java.util.ArrayList;

public class Shape {
	private ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	private float opacity, red, green, blue;

	public Shape(float opacity, float red, float green, float blue) {
		this.opacity = opacity;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public ArrayList<Vertex> getVertices() {
		return vertices;
	}

	public void addVertex(Vertex v) {
		for (Vertex p : vertices) {
			if (p.getX() == v.getX() && p.getY() == v.getY())
				return;
		}

		vertices.add(v);
	}

	public void addEdge(Edge e) {
		addVertex(e.getStart());
		addVertex(e.getEnd());
	}

	public float getOpacity() {
		return opacity;
	}

	public void setOpacity(float o) {
		opacity = o;
	}

	public float[] getColor() {
		return new float[] { red, green, blue };
	}

}