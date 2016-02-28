package io.smudgr.source.smudge.alg.op;

import io.smudgr.source.Frame;
import io.smudgr.source.smudge.alg.AlgorithmComponent;

public abstract class Operation extends AlgorithmComponent {

	public void init() {

	}

	public void update() {

	}

	public abstract void execute(Frame img);

	public abstract String getName();

	public String toString() {
		return getName();
	}

}
