package io.smudgr.app.threads;

import io.smudgr.app.Controller;

public class UpdateThread extends AppThread {

	public UpdateThread() {
		super("Update Thread");
	}

	public int msToTicks(int ms) {
		return (int) (ms / (1000 / getTarget()));
	}

	protected void execute() {
		update();
	}

	private void update() {
		Controller.getInstance().update();
	}

	protected void printStatus() {
		if (ticks != getTarget())
			System.out.println(ticks + " updates (should be " + getTarget() + ")");
	}

}
