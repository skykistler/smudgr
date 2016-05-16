package io.smudgr.app.threads;

import javax.swing.SwingUtilities;

import io.smudgr.app.Controller;
import io.smudgr.app.view.View;
import io.smudgr.project.smudge.util.Frame;

public class ViewThread extends AppThread {

	private static final boolean DEBUG = true;

	private View view;

	public ViewThread(View view) {
		super("View Thread - " + view);

		this.view = view;
		setTarget(Controller.TARGET_FPS);
	}

	protected void execute() {
		try {
			Frame frame = Controller.getInstance().getProject().getSmudge().getFrame().copy();

			view.update(frame);

			frame.dispose();
		} catch (NullPointerException e) {
			// frame was probably null, do nothing
		}
	}

	protected void printStatus() {
		if (DEBUG)
			System.out.println(ticks + " view updates");
	}

	protected void onStop() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				view.stop();
			}
		});
	}

}
