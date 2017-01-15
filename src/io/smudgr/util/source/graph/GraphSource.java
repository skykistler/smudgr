package io.smudgr.util.source.graph;

import io.smudgr.util.Frame;
import io.smudgr.util.source.Source;

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
