package io.smudgr.smudge.source.graph;

import java.util.ArrayList;

import io.smudgr.smudge.source.graph.alg.GraphAlgorithm;

public class Graph {

	private int width, height;

	private ArrayList<Shape> polygons = new ArrayList<Shape>();
	private ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	private ArrayList<Edge> edges = new ArrayList<Edge>();
	private ArrayList<GraphAlgorithm> graphAlgorithms = new ArrayList<GraphAlgorithm>();

	public Graph(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void addAlgorithm(GraphAlgorithm ga) {
		graphAlgorithms.add(ga);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public ArrayList<Shape> getPolygons() {
		return polygons;
	}

	public ArrayList<Vertex> getVertices() {
		return vertices;
	}

	public ArrayList<Edge> getEdges() {
		return edges;
	}

	public ArrayList<GraphAlgorithm> getGraphAlgorithms() {
		return graphAlgorithms;
	}

}
