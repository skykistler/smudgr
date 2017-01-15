package io.smudgr.app.threads;

import javax.swing.SwingUtilities;

import io.smudgr.app.Controller;
import io.smudgr.app.view.View;
import io.smudgr.util.Frame;

public class ViewThread extends AppThread {

	private View view;
	private boolean showUpdates = true;

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
			/* Frame was probably null, do nothing 
			 */
		} catch (IllegalStateException e) {
			/* Frame was probably disposed too fast, skip it.
			 * If frames are being disposed too fast, skipping a frame probably isn't a big deal.
			 */
		}
	}

	protected void printStatus() {
		if (showUpdates)
			System.out.println(ticks + " updates to view: " + view.getName());
	}

	protected void onStop() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				view.stop();
			}
		});
	}

}
