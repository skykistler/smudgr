package io.smudgr.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jcodec.api.JCodecException;
import org.jcodec.api.awt.FrameGrab;
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.NIOUtils;

public class Video {
	private int width = 1, height = 1;
	private String filename;
	private int start;

	private BufferThread bufferer;
	private final int bufferCap = 1000;
	private volatile BlockingQueue<Frame> buffer;

	public Video(String filename) {
		this(filename, 0);
	}

	public Video(String filename, int start) {
		this.filename = "data/" + filename;
		this.start = start;

		load();
	}

	private void load() {
		bufferer = new BufferThread();
		bufferer.start();
	}

	public Frame getFrame() {
		if (bufferer != null && bufferer.started) {
			try {
				return buffer.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Frame(width, height);
	}

	class BufferThread implements Runnable {

		private FrameGrab frameGrabber;
		private boolean started;

		public BufferThread() {
			buffer = new LinkedBlockingQueue<Frame>();
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
				frameGrabber.seekToSecondPrecise(start);
			} catch (IOException | JCodecException e1) {
				e1.printStackTrace();
				started = false;
			}

			while (started)
				while (buffer.size() < bufferCap) {
					try {
						BufferedImage frame = frameGrabber.getFrame();

						if (frame == null)
							started = false;

						width = frame.getWidth();
						height = frame.getHeight();

						buffer.add(new Frame(frame));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			System.out.println("End of buffer reached");
		}

	}

}
