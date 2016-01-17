package io.smudgr.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.jcodec.api.JCodecException;
import org.jcodec.api.awt.FrameGrab;
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.NIOUtils;

public class Video implements Model {
	private String filename;
	private int start;

	private BufferThread bufferer;
	private final int bufferCap = 1000;
	private volatile Queue<Frame> buffer;

	public Video(String filename) {
		this(filename, 0);
	}

	public Video(String filename, int start) {
		this.filename = "data/" + filename;
		this.start = start;
	}

	private void load() {
		bufferer = new BufferThread();
		bufferer.start();
	}

	public void stop() {
		if (bufferer != null)
			bufferer.stop();
	}

	public Frame getFrame() {
		if (bufferer == null || !bufferer.started) {
			load();
			return null;
		}

		while (buffer.size() == 0)
			;

		return buffer.poll();
	}

	class BufferThread implements Runnable {

		private FrameGrab frameGrabber;
		private boolean started;

		public BufferThread() {
			buffer = new LinkedList<Frame>();
		}

		public void start() {
			Thread t = new Thread(this);
			t.start();
		}

		public void run() {
			started = true;

			try {
				FileChannelWrapper ch = NIOUtils.readableFileChannel(new File(filename));
				frameGrabber = new FrameGrab(ch);
				frameGrabber.seekToSecondSloppy(start);
			} catch (IOException | JCodecException e1) {
				e1.printStackTrace();
				started = false;
			}

			while (started)
				while (buffer.size() < bufferCap) {
					try {
						BufferedImage frame = frameGrabber.getFrame();

						if (frame == null) {
							started = false;
							break;
						}

						buffer.add(new Frame(frame));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			run();
		}

		public void stop() {
			started = false;
			buffer = null;
		}

	}

}
