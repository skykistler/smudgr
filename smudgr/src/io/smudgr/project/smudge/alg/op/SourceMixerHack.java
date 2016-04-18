package io.smudgr.project.smudge.alg.op;

import java.awt.Graphics2D;

import io.smudgr.project.smudge.source.Frame;
import io.smudgr.project.smudge.source.Source;

public class SourceMixerHack extends Operation {

	private Frame mixFrame;
	private Source source;

	public void init() {
		source.init();
	}

	public void update() {
		source.update();
	}

	public void execute(Frame img) {

		/*
		 * Grab the frame that will be mixed into img
		*/
		mixFrame = source.getFrame();

		if (mixFrame == null)
			return;

		int width = img.getWidth();
		int height = img.getHeight();

		Graphics2D g = (Graphics2D) img.getFrame().getBufferedImage().getGraphics();
		g.drawImage(mixFrame.getBufferedImage(), 0, 0, width, height, null);
		g.dispose();

	}

	public String getName() {
		return "Source Mixer for " + source;
	}

}
