package io.smudgr.app.threads;

import io.smudgr.app.Controller;
import io.smudgr.util.output.FrameOutput;

public class RenderThread extends AppThread {

	private FrameOutput output;
	private int everyXTicks;

	public RenderThread() {
		super("Render Thread");

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

		try {
			Controller.getInstance().getProject().getSmudge().render();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

		if (output != null)
			output.addFrame(Controller.getInstance().getProject().getSmudge().getFrame().copy());
	}

	protected void printStatus() {
		System.out.println(ticks + " fps");
	}

}
