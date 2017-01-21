package io.smudgr.app.threads;

import javax.swing.SwingUtilities;

import io.smudgr.app.controller.Controller;
import io.smudgr.app.project.Project;
import io.smudgr.app.view.View;
import io.smudgr.util.Frame;

/**
 * The {@link ViewThread} is responsible for taking the latest rendered
 * {@link Project#getSmudge()} frame and passing it to the given {@link View}
 * instance, with a default rate of {@link Controller#TARGET_FPS}.
 *
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
		super("View Thread - " + view);

		this.view = view;
		setTarget(Controller.TARGET_FPS);
	}

	@Override
	public void start() {
		view.start();
		super.start();
	}

	@Override
	protected void execute() {
		try {
			Frame frame = Controller.getInstance().getProject().getSmudge().getFrame().copy();

			view.update(frame);

			frame.dispose();
		} catch (NullPointerException e) {
			/*
			 * Frame was probably null, do nothing
			 */
		} catch (IllegalStateException e) {
			/*
			 * Frame was probably disposed too fast, skip it.
			 * If frames are being disposed too fast, skipping a frame probably
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
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				view.stop();
			}
		});
	}

}
