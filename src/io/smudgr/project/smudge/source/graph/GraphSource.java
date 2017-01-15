package io.smudgr.project.smudge.source.graph;

import io.smudgr.project.smudge.source.Source;
import io.smudgr.util.Frame;

public class GraphSource implements Source {
	private Graph graph;
	private GraphRenderer renderer;

	public void init() {
		graph = new Graph(500, 500);
		renderer = new GraphRenderer(graph);
	}

	public void update() {
		// run graph algorithms or something
	}

	public Frame getFrame() {
		return renderer.drawGraph();
	}

	public void dispose() {

	}

}
