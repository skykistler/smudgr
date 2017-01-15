package io.smudgr.util.source.graph.alg;

import io.smudgr.util.source.graph.Graph;

public abstract class GraphAlgorithm {
	public abstract void exectute(Graph g);

	public abstract String getName();

	public String toString() {
		return getName();
	}
}
