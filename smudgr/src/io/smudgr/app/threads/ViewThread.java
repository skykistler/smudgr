package io.smudgr.app.threads;

import javax.swing.SwingUtilities;

import io.smudgr.app.Controller;
import io.smudgr.app.view.View;
import io.smudgr.project.smudge.source.Frame;

public class ViewThread extends AppThread {

	private View view;

	public ViewThread(View view) {
		this.view = view;

		setTarget(Controller.TARGET_FPS);
	}

	protected void execute() {
		Frame frame = Controller.getInstance().getProject().getSmudge().getFrame();
		if (frame == null)
			return;

		view.update(frame);
	}

	protected void printStatus() {

	}

	protected void onStop() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				view.stop();
			}
		});
	}

}
