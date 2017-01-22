package io.smudgr.util.source.graph;

import io.smudgr.util.Frame;
import io.smudgr.util.source.Source;

/**
 * The {@link GraphSource} implementation represents a {@link Source} that
 * returns rendered frames of a {@link Graph}
 */
public class GraphSource implements Source {
	private Graph graph;
	private GraphRenderer renderer;

	@Override
	public void init() {
		graph = new Graph(500, 500);
		renderer = new GraphRenderer(graph);
	}

	@Override
	public void update() {
		// run graph algorithms or something
	}

	@Override
	public Frame getFrame() {
		return renderer.drawGraph();
	}

	@Override
	public void dispose() {

	}

}
