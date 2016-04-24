package io.smudgr.project.smudge.alg.op;

import io.smudgr.project.smudge.alg.AlgorithmComponent;
import io.smudgr.project.smudge.util.Frame;

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
