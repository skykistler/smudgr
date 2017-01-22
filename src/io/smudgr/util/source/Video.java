package io.smudgr.util.source;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.jcodec.api.JCodecException;
import org.jcodec.api.awt.FrameGrab;
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.NIOUtils;

import io.smudgr.util.Frame;

/**
 * The {@link Video} source loads a video file and returns the current frame of
 * the video, in time with the application.
 */
public class Video implements Source {
	private String filename;
	private int start;

	private BufferThread bufferer;
	private final int bufferCap = 1000;
	private volatile Queue<Frame> buffer;

	/**
	 * Create a new {@link Video} loaded from the given file
	 *
	 * @param filename
	 *            path
	 */
	public Video(String filename) {
		this(filename, 0);
	}

	/**
	 * Create a new {@link Video} loaded from the given file, and start it at
	 * the given second.
	 *
	 * @param filename
	 *            path
	 * @param start
	 *            seconds
	 */
	public Video(String filename, int start) {
		this.filename = filename;
		this.start = start;
	}

	@Override
	public void init() {
		bufferer = new BufferThread();
		bufferer.start();
	}

	@Override
	public void update() {
		// update every frame delay ms
	}

	@Override
	public Frame getFrame() {
		if (!bufferer.started)
			return null;

		while (buffer.size() == 0)
			;

		Frame f = buffer.poll();

		return f;
	}

	@Override
	public void dispose() {

		if (bufferer != null)
			bufferer.stop();
	}

	class BufferThread implements Runnable {

		private FrameGrab frameGrabber;
		private volatile boolean started;

		public BufferThread() {
			buffer = new LinkedList<Frame>();
		}

		public void start() {
			Thread t = new Thread(this);
			t.start();
		}

		@Override
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

	@Override
	public String toString() {
		return filename;
	}

}
