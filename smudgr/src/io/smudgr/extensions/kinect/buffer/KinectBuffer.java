package io.smudgr.extensions.kinect.buffer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.jcodec.api.JCodecException;

import io.smudgr.project.smudge.util.Frame;

public abstract class KinectBuffer {

	private BufferThread bufferer;
	private volatile Queue<Frame> buffer;
	private final int bufferCap = 100;

	public void init() {
		bufferer = new BufferThread();
		bufferer.start();
	}

	public Frame getFrame() {
		if (!bufferer.started)
			return null;

		while (buffer.size() == 0)
			;

		return buffer.poll();
	}

	public abstract void initStream();

	class BufferThread implements Runnable {

		private volatile boolean started;

		public BufferThread() {
			buffer = new LinkedList<Frame>();
		}

		public void start() {
			Thread t = new Thread(this);
			t.start();
		}

		public void run() {
			started = true;

			initStream();

			try {
				// Open up device stream
			} catch (IOException | JCodecException e1) {
				e1.printStackTrace();
				started = false;
			}

			while (started)
				while (buffer.size() < bufferCap) {
					try {
						// Pull ByteBuffer from kinect stream

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
