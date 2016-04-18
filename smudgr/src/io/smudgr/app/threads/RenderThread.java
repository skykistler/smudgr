package io.smudgr.app.threads;

import io.smudgr.app.Controller;
import io.smudgr.app.output.FrameOutput;

public class RenderThread extends AppThread {

	private FrameOutput output;
	private int everyXTicks;

	public RenderThread() {
		setTarget(Controller.TARGET_FPS);
	}

	public void startOutput(FrameOutput output, int everyXTicks) {
		this.output = output;
		this.everyXTicks = everyXTicks;
	}

	public void stopOutput() {
		output = null;
		everyXTicks = 0;
	}

	protected void execute() {

		if (output != null)
			for (int i = 0; i <= everyXTicks; i++)
				Controller.getInstance().update();

		synchronized (Controller.getInstance()) {
			Controller.getInstance().getProject().getSmudge().render();
		}

		if (output != null)
			output.addFrame(Controller.getInstance().getProject().getSmudge().getFrame());

	}

	protected void printStatus(int ticksPerSecond) {
		System.out.println(ticksPerSecond + " fps");
	}

}
