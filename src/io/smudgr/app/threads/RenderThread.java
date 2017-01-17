package io.smudgr.app.threads;

import io.smudgr.app.controller.Controller;
import io.smudgr.app.project.Project;
import io.smudgr.util.output.FrameOutput;

/**
 * The {@link RenderThread} is responsible for rendering the current application
 * instance {@link Project#getSmudge()} at the {@link Controller#TARGET_FPS}
 * rate.
 */
public class RenderThread extends AppThread {

	private FrameOutput output;
	private int everyXTicks;

	/**
	 * Instantiate a new {@link RenderThread} with the target set to
	 * {@link Controller#TARGET_FPS}
	 */
	public RenderThread() {
		super("Render Thread");

		setTarget(Controller.TARGET_FPS);
	}

	/**
	 * Start outputting rendered frames to the specified {@link FrameOutput} at
	 * a rate of {@code everyXTicks}
	 * 
	 * @param output
	 *            {@link FrameOutput} to output to
	 * @param everyXTicks
	 *            output rate
	 * @see Controller#startOutput(FrameOutput)
	 */
	public void startOutput(FrameOutput output, int everyXTicks) {
		this.output = output;
		this.everyXTicks = everyXTicks;
	}

	/**
	 * Stop any existing {@link FrameOutput}
	 * 
	 * @see Controller#stopOutput()
	 */
	public void stopOutput() {
		output = null;
		everyXTicks = 0;
	}

	@Override
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

	@Override
	protected void printStatus() {
		System.out.println(ticks + " fps");
	}

}
