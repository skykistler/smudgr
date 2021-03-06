package io.smudgr.app.threads;

import io.smudgr.app.controller.Controller;
import io.smudgr.app.project.Project;
import io.smudgr.engine.Smudge;
import io.smudgr.util.PixelFrame;
import io.smudgr.util.output.FrameOutput;

/**
 * The {@link RenderThread} is responsible for rendering every {@link Smudge} in
 * the current application instance {@link Project#getRack()} at the
 * {@link Controller#TARGET_FPS} rate.
 */
public class RenderThread extends AppThread {

	private FrameOutput output;
	private int everyXTicks;
	private int lastFPS;

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
			Controller.getInstance().getProject().getRack().render();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

		if (output != null)
			output.addFrame((PixelFrame) Controller.getInstance().getProject().getRack().getLastFrame().copy());
	}

	@Override
	protected void printStatus() {
		lastFPS = (int) ticks;
		System.out.println(lastFPS + " fps");
	}

	/**
	 * Gets the amount of frames rendered in the previous second.
	 *
	 * @return last FPS
	 */
	public int getLastFPS() {
		return lastFPS;
	}

}
