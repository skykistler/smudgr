package io.smudgr.source.graph;

public class Edge {
	private Vertex start, end;
	private float weight, opacity;

	public Edge(Vertex v1, Vertex v2) {
		this(v1, v2, Float.NaN, 0.1f);
	}

	public Edge(Vertex v1, Vertex v2, float weight, float opacity) {
		start = v1;
		end = v2;
		this.weight = weight;
		this.opacity = opacity;

		v1.getAdjacent().add(v2);
		v2.getAdjacent().add(v1);
	}

	public Vertex getStart() {
		return start;
	}

	public void setStart(Vertex v) {
		start = v;
	}

	public Vertex getEnd() {
		return end;
	}

	public void setEnd(Vertex v) {
		end = v;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float w) {
		weight = w;
	}

	public float getOpacity() {
		return opacity;
	}

	public void setOpacity(float o) {
		opacity = o;
	}

	public boolean compare(Edge e) {
		if (getStart() == e.getStart() && getEnd() == e.getEnd())
			return true;
		if (getStart() == e.getEnd() && getEnd() == e.getStart())
			return true;
		return false;
	}

}