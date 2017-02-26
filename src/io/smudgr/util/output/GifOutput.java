package io.smudgr.util.output;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.java_websocket.util.DisposedBytesProvider;

import io.smudgr.app.controller.Controller;
import io.smudgr.util.Frame;

/**
 * The {@link GifOutput} output stream records an animated GIF at this ideal GIF
 * frame rate.
 */
public class GifOutput implements FrameOutput {

	private final static int TARGET_GIF_MS = 50;

	private String path;

	private int maxWidth, maxHeight;
	private BlockingQueue<Frame> frames = new LinkedBlockingQueue<Frame>();

	private File cache;
	private FileOutputStream cacheOut;
	private FileInputStream cacheIn;
	private AnimatedGifEncoder gifEncoder;

	private volatile boolean closed = false;
	private volatile boolean successful = false;

	/**
	 * Create a new {@link GifOutput} with the given name
	 *
	 * @param name
	 *            {@link String}
	 */
	public GifOutput(String name) {
		path = Controller.getInstance().getProject().getOutputPath() + name + "_" + System.currentTimeMillis() + ".gif";
	}

	@Override
	public int getTargetFPS() {
		return 1000 / TARGET_GIF_MS;
	}

	@Override
	public void open() {
		gifEncoder = new AnimatedGifEncoder();
		gifEncoder.setDelay(TARGET_GIF_MS);
		gifEncoder.setRepeat(0);
		// gifEncoder.setQuality();

		try {
			cache = new File(path + ".part");
			cacheOut = new FileOutputStream(cache);

			cache.deleteOnExit();
			new GifWriterThread();

		} catch (IOException e) {
			e.printStackTrace();
			closed = true;
		}
	}

	@Override
	public void addFrame(Frame f) {
		if (closed)
			return;

		if (maxWidth < f.getWidth())
			maxWidth = f.getWidth();

		if (maxHeight < f.getHeight())
			maxHeight = f.getHeight();

		frames.add(f);
	}

	@Override
	public void close() {
		if (closed)
			return;

		closed = true;
	}

	private void onClose() {
		closed = true;

		if (gifEncoder.started)
			gifEncoder.finish();

		try {
			if (cacheOut != null)
				cacheOut.close();
		} catch (Exception e) {
		}

		try {
			if (cacheIn != null)
				cacheIn.close();
		} catch (Exception e) {
		}

		File cache = new File(path + ".part");
		if (cache.isFile())
			cache.delete();

		if (!successful) {
			File gif = new File(path);

			if (gif.isFile())
				gif.delete();

			System.out.println("GIF record failed!");
		} else {
			System.out.println("Saved a GIF: " + path);
		}
	}

	private class GifWriterThread implements Runnable {

		public GifWriterThread() {
			Thread t = new Thread(this);
			t.setName("GIF Writer");
			t.start();
		}

		@Override
		public void run() {
			ByteBuffer buffer = ByteBuffer.allocate(0);
			int len;
			int i;

			while (!closed) {

				try {
					Frame frame = frames.poll(2, TimeUnit.SECONDS);
					if (frame == null)
						continue;

					len = 8 + frame.pixels.length * 4;

					if (buffer.capacity() < len) {
						DisposedBytesProvider.getInstance().disposeBytes(buffer);
						buffer = DisposedBytesProvider.getInstance().getDisposedBytes(len, false);
					}

					buffer.clear();

					IntBuffer ints = buffer.asIntBuffer();

					ints.put(frame.getWidth());
					ints.put(frame.getHeight());

					for (i = 0; i < frame.pixels.length; i++)
						ints.put(0xFF000000 | frame.pixels[i]);

					frame.dispose();

					ints.flip();
					cacheOut.write(buffer.array(), 0, len);

					cacheOut.flush();
					System.gc();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			try {
				cacheOut.flush();
				cacheOut.close();

				writeFromCacheToGif();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				onClose();
			}
		}

		private void writeFromCacheToGif() throws IOException {
			cacheIn = new FileInputStream(cache);

			gifEncoder.start(path);

			ByteBuffer frameInfo = ByteBuffer.allocate(8);
			ByteBuffer frameData = null;
			BufferedImage gifFrame = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);
			int width;
			int height;

			while (cacheIn.available() > 0) {
				frameInfo.clear();

				cacheIn.read(frameInfo.array());
				frameInfo.rewind();

				IntBuffer frameInfoInt = frameInfo.asIntBuffer();
				width = frameInfoInt.get();
				height = frameInfoInt.get();

				// Something went wrong!
				if (width <= 0 || height <= 0) {
					onClose();
					break;
				}

				frameData = DisposedBytesProvider.getInstance().getDisposedBytes(width * height * 4, false);
				cacheIn.read(frameData.array());
				frameData.position(0);

				Frame frame = new Frame(width, height);
				frameData.asIntBuffer().get(frame.pixels);

				frame.drawTo(gifFrame);
				frame.dispose();

				gifEncoder.addFrame(gifFrame);

				DisposedBytesProvider.getInstance().disposeBytes(frameData);
			}

			successful = true;
		}
	}

}
