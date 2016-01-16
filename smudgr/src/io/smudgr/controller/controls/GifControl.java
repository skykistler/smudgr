package io.smudgr.controller.controls;

import io.smudgr.controller.Controller;
import io.smudgr.model.Frame;
import io.smudgr.model.Gif;

public class GifControl extends Controllable {

	private String filename;
	private Gif gif;
	private Frame lastFrame;
	private boolean paused;

	public GifControl(Controller controller, String filename) {
		this(controller, filename, 0);
	}

	public GifControl(Controller controller, String filename, int start) {
		super(controller, "Animated Gif");
		this.filename = filename;
	}

	public void init() {
		gif = new Gif(filename);
	}

	int i = 0;

	public void update() {
		if (!paused) {
			if (i++ < 5)
				lastFrame = gif.getFrame();

			if (lastFrame != null)
				getController().getSmudge().setSource(lastFrame);
		}
	}

	public void increment() {
		// TODO speed up
	}

	public void decrement() {
		// TODO slow down
	}

	public void inputValue(int value) {

	}

	public void inputOn(int value) {
		paused = false;
	}

	public void inputOff(int value) {
		paused = true;
	}

}
