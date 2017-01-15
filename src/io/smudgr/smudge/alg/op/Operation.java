package io.smudgr.smudge.alg.op;

import io.smudgr.smudge.alg.AlgorithmComponent;
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
