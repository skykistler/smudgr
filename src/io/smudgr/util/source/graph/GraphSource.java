package io.smudgr.util.source.graph;

import io.smudgr.util.PixelFrame;
import io.smudgr.util.source.Source;

/**
 * The {@link GraphSource} implementation represents a {@link Source} that
 * returns rendered frames of a {@link Graph}
 */
public class GraphSource implements Source {

	@Override
	public String getTypeIdentifier() {
		return "graph";
	}

	@Override
	public String getTypeName() {
		return "Graph";
	}

	@Override
	public String getName() {
		return "Graph";
	}

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
	public PixelFrame getFrame() {
		return renderer.drawGraph();
	}

	@Override
	public PixelFrame getThumbnail() {
		return null;
	}

	@Override
	public void dispose() {

	}

}
