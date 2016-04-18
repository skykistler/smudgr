package io.smudgr.app.threads;

import io.smudgr.app.Controller;

public class UpdateThread extends AppThread {

	public int msToTicks(int ms) {
		return (int) (ms / (1000 / getTarget()));
	}

	protected void execute() {
		synchronized (Controller.getInstance()) {
			Controller.getInstance().update();
		}
	}

	protected void printStatus(int ticksPerSecond) {
		if (ticksPerSecond != getTarget())
			System.out.println(ticksPerSecond + " updates (should be " + getTarget() + ")");
	}

}
