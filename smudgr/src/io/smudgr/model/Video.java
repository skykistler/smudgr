package io.smudgr.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.jcodec.api.JCodecException;
import org.jcodec.api.awt.FrameGrab;
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.NIOUtils;

public class Video {
	private FrameGrab frameGrabber;
	private int width, height;

	public Video(String filename) {
		this(filename, 0);
	}

	public Video(String filename, int start) {
		try {
			load("data/" + filename);
			frameGrabber.seekToSecondPrecise(start);
		} catch (IOException | JCodecException e) {
			e.printStackTrace();
		}
	}

	private void load(String filename) throws IOException, JCodecException {
		FileChannelWrapper ch = NIOUtils.readableFileChannel(new File(filename));
		frameGrabber = new FrameGrab(ch);
	}

	public Frame getFrame() {
		try {
			BufferedImage frame = frameGrabber.getFrame();

			width = frame.getWidth();
			height = frame.getHeight();

			return new Frame(frame);

		} catch (IOException e) {
			e.printStackTrace();

			return new Frame(width, height);
		}
	}

}
