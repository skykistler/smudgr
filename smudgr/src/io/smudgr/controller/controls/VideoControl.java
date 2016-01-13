package io.smudgr.controller.controls;

import io.smudgr.controller.Controller;
import io.smudgr.model.Frame;
import io.smudgr.model.Video;

public class VideoControl extends Controllable {

	private String filename;
	private Video video;
	private Frame lastFrame;
	private int start;
	private boolean paused;

	public VideoControl(Controller controller, String filename) {
		this(controller, filename, 0);
	}

	public VideoControl(Controller controller, String filename, int start) {
		super(controller, "Video Feed");
		this.filename = filename;
		this.start = start;
	}

	public void init() {
		video = new Video(filename, start);
	}

	public void update() {
		if (!paused) {
			lastFrame = video.getFrame();

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
