package io.smudgr.app.threads;

import io.smudgr.app.controller.Controller;
import io.smudgr.app.project.Project;
import io.smudgr.app.view.View;
import io.smudgr.engine.Rack;
import io.smudgr.util.PixelFrame;

/**
 * The {@link ViewThread} is responsible for taking the last frame rendered
 * {@link Rack#getLastFrame()} and passing it to a given {@link View}
 * instance, with a default rate of {@link Controller#TARGET_FPS}.
 *
 * @see Project#getRack()
 * @see Controller#add(Object)
 */
public class ViewThread extends AppThread {

	private View view;
	private boolean showUpdates = true;

	/**
	 * Instantiate a new {@link ViewThread} for the given {@link View}
	 *
	 * @param view
	 *            {@link View}
	 */
	public ViewThread(View view) {
		super("View Thread - " + view.getName());

		this.view = view;
		setTarget(view.getTargetFPS());
	}

	@Override
	public void onStart() {
		view.start();
	}

	@Override
	protected void execute() {
		try {
			PixelFrame frame = (PixelFrame) Controller.getInstance().getProject().getRack().getLastFrame().copy();

			view.update(frame);

			frame.dispose();
		} catch (NullPointerException e) {
			/*
			 * Frame was probably null, do nothing
			 */
		} catch (IllegalStateException e) {
			/*
			 * Last frame was probably disposed before .copy(), skip it.
			 * If frames are being disposed that fast, skipping a frame probably
			 * isn't a big deal.
			 */
		}
	}

	@Override
	protected void printStatus() {
		if (showUpdates)
			System.out.println(ticks + " updates to view: " + view.getName());
	}

	@Override
	protected void onStop() {
		view.stop();
	}

}
