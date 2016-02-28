package io.smudgr.source.smudge.alg.op;

import io.smudgr.source.Frame;
import io.smudgr.source.smudge.alg.AlgorithmComponent;

public abstract class Operation extends AlgorithmComponent {

	protected Frame img;

	public void init() {

	}

	public void update() {

	}

	public void apply(Frame img) {
		this.img = img;
		execute(img);
	}

	protected abstract void execute(Frame img);

	public abstract String getName();

	public String toString() {
		return getName();
	}

}
