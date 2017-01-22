package io.smudgr.util.source.graph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

import io.smudgr.util.Frame;

/**
 * The {@link GraphRenderer} class uses Java libraries to draw the
 * {@link Vertex}, {@link Edge}, and {@link Shape} components of a {@link Graph}
 * onto a {@link Frame}
 */
public class GraphRenderer {
	private Graph graph;

	/**
	 * Create a new {@link GraphRenderer} for the given {@link Graph}
	 *
	 * @param model
	 *            {@link Graph}
	 */
	public GraphRenderer(Graph model) {
		graph = model;
	}

	/**
	 * Draw the graph and return the {@link Frame}
	 *
	 * @return {@link Frame}
	 * @see GraphRenderer#GraphRenderer(Graph)
	 */
	public Frame drawGraph() {
		BufferedImage graphImage = new BufferedImage(graph.getWidth(), graph.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = graphImage.getGraphics();

		for (Shape p : graph.getPolygons())
			drawPolygon(p, g);

		for (Vertex v : graph.getVertices())
			drawVertex(v, g);

		for (Edge e : graph.getEdges())
			drawEdge(e, g);

		return new Frame(graphImage);
	}

	private void drawPolygon(Shape p, Graphics g) {
		Polygon poly = new Polygon();

		for (Vertex v : p.getVertices()) {
			poly.addPoint((int) v.getX(), (int) v.getY());
		}

		float[] color = p.getColor();
		g.setColor(new Color(color[0], color[1], color[2], p.getOpacity()));

		g.drawPolygon(poly);
	}

	private void drawVertex(Vertex v, Graphics g) {
		double radius = 5;
		Color color = Color.gray;

		int x = (int) (v.getX() - radius);
		int y = (int) (v.getY() - radius);
		int diameter = (int) (radius * 2);
		g.setColor(color);
		g.drawOval(x, y, diameter, diameter);
	}

	private void drawEdge(Edge e, Graphics g) {
		int x1 = (int) e.getStart().getX();
		int y1 = (int) e.getStart().getY();
		int x2 = (int) e.getEnd().getX();
		int y2 = (int) e.getEnd().getY();

		g.setColor(new Color(0, 0, 0, e.getOpacity()));
		g.drawLine(x1, y1, x2, y2);
	}

}
