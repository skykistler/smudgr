package io.smudgr.project.smudge.source.graph;

import java.util.ArrayList;

public class Vertex {
	private double position[] = new double[2];
	private String name, displayName;
	private ArrayList<Vertex> adj = new ArrayList<Vertex>();

	public Vertex(String name, double x, double y) {
		this.name = name;
		displayName = name;
		position[0] = x;
		position[1] = y;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String text) {
		displayName = text;
	}

	public double getX() {
		return position[0];
	}

	public double getY() {
		return position[1];
	}

	public void setX(double x) {
		position[0] = x;
	}

	public void setY(double y) {
		position[1] = y;
	}

	public ArrayList<Vertex> getAdjacent() {
		return adj;
	}

}