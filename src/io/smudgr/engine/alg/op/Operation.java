package io.smudgr.engine.alg.op;

import io.smudgr.engine.alg.AlgorithmComponent;
import io.smudgr.util.Frame;

public abstract class Operation extends AlgorithmComponent {

	public String getType() {
		return "Operation";
	}

	public void init() {

	}

	public void update() {

	}

	public abstract void execute(Frame img);

}
