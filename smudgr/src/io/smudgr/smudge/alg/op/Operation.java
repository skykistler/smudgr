package io.smudgr.smudge.alg.op;

import io.smudgr.smudge.alg.AlgorithmComponent;
import io.smudgr.smudge.source.Frame;

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
