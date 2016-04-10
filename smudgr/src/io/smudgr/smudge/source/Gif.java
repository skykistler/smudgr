package io.smudgr.smudge.source;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.smudgr.controller.Controller;

public class Gif implements Source {
	private String filename;

	private BufferThread bufferer;
	private volatile ArrayList<GifFrame> buffer;

	private int ticks;
	private int currentFrame;

	private Frame lastFrame;

	public Gif(String filename) {
		this.filename = filename;
	}

	public void init() {
		bufferer = new BufferThread();
		bufferer.start();
	}

	public void update() {
		if (buffer == null || buffer.size() == 0)
			return;

		ticks++;

		if (currentFrame >= buffer.size() || currentFrame < 0) {
			currentFrame = 0;
		}

		int delay = Controller.getInstance().ticksToMs(ticks);
		GifFrame frame = buffer.get(currentFrame);

		if (frame != null)
			if (frame.getDelay() <= delay) {
				currentFrame++;
				currentFrame %= buffer.size();
				ticks = 0;
			}

	}

	public Frame getFrame() {
		if (bufferer == null || !bufferer.started || buffer == null || buffer.size() == 0 || currentFrame < 0 || currentFrame >= buffer.size()) {
			return lastFrame;
		}

		return lastFrame = buffer.get(currentFrame).getFrame();
	}

	public void dispose() {
		buffer = null;
		bufferer.stop();
	}

	public String toString() {
		return filename;
	}

	class BufferThread implements Runnable {

		private boolean started;

		public BufferThread() {
			buffer = new ArrayList<GifFrame>();
		}

		public void start() {
			Thread t = new Thread(this);
			t.start();
		}

		public void stop() {
			started = false;
		}

		// This is just absolutely terrifying
		public void run() {
			started = true;

			ImageReader reader = (ImageReader) ImageIO.getImageReadersByFormatName("gif").next();
			try {
				reader.setInput(ImageIO.createImageInputStream(new FileInputStream(new File(filename))));

				int lastx = 0;
				int lasty = 0;

				int width = -1;
				int height = -1;

				IIOMetadata metadata = reader.getStreamMetadata();

				Color backgroundColor = null;

				if (metadata != null) {
					IIOMetadataNode globalRoot = (IIOMetadataNode) metadata.getAsTree(metadata.getNativeMetadataFormatName());

					NodeList globalColorTable = globalRoot.getElementsByTagName("GlobalColorTable");
					NodeList globalScreeDescriptor = globalRoot.getElementsByTagName("LogicalScreenDescriptor");

					if (globalScreeDescriptor != null && globalScreeDescriptor.getLength() > 0) {
						IIOMetadataNode screenDescriptor = (IIOMetadataNode) globalScreeDescriptor.item(0);

						if (screenDescriptor != null) {
							width = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenWidth"));
							height = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenHeight"));
						}
					}

					if (globalColorTable != null && globalColorTable.getLength() > 0) {
						IIOMetadataNode colorTable = (IIOMetadataNode) globalColorTable.item(0);

						if (colorTable != null) {
							String bgIndex = colorTable.getAttribute("backgroundColorIndex");

							IIOMetadataNode colorEntry = (IIOMetadataNode) colorTable.getFirstChild();
							while (colorEntry != null) {
								if (colorEntry.getAttribute("index").equals(bgIndex)) {
									int red = Integer.parseInt(colorEntry.getAttribute("red"));
									int green = Integer.parseInt(colorEntry.getAttribute("green"));
									int blue = Integer.parseInt(colorEntry.getAttribute("blue"));

									backgroundColor = new Color(red, green, blue);
									break;
								}

								colorEntry = (IIOMetadataNode) colorEntry.getNextSibling();
							}
						}
					}
				}

				BufferedImage master = null;
				boolean hasBackround = false;

				for (int frameIndex = 0;; frameIndex++) {
					if (!started)
						break;

					BufferedImage image;
					try {
						image = reader.read(frameIndex);
					} catch (IndexOutOfBoundsException e) {
						System.out.println("Finished loading gif");
						break;
					}

					if (width == -1 || height == -1) {
						width = image.getWidth();
						height = image.getHeight();
					}

					IIOMetadataNode root = (IIOMetadataNode) reader.getImageMetadata(frameIndex).getAsTree("javax_imageio_gif_image_1.0");
					IIOMetadataNode gce = (IIOMetadataNode) root.getElementsByTagName("GraphicControlExtension").item(0);
					NodeList children = root.getChildNodes();

					int delay = Integer.valueOf(gce.getAttribute("delayTime")) * 10;

					String disposal = gce.getAttribute("disposalMethod");

					if (master == null) {
						master = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
						master.createGraphics().setColor(backgroundColor);
						master.createGraphics().fillRect(0, 0, master.getWidth(), master.getHeight());

						hasBackround = image.getWidth() == width && image.getHeight() == height;

						master.createGraphics().drawImage(image, 0, 0, null);
					} else {
						int x = 0;
						int y = 0;

						for (int nodeIndex = 0; nodeIndex < children.getLength(); nodeIndex++) {
							Node nodeItem = children.item(nodeIndex);

							if (nodeItem.getNodeName().equals("ImageDescriptor")) {
								NamedNodeMap map = nodeItem.getAttributes();

								x = Integer.valueOf(map.getNamedItem("imageLeftPosition").getNodeValue());
								y = Integer.valueOf(map.getNamedItem("imageTopPosition").getNodeValue());
							}
						}

						if (disposal.equals("restoreToPrevious")) {
							BufferedImage from = null;
							for (int i = frameIndex - 1; i >= 0; i--) {
								if (!buffer.get(i).getDisposal().equals("restoreToPrevious") || frameIndex == 0) {
									from = buffer.get(i).getImage();
									break;
								}
							}

							ColorModel model = from.getColorModel();
							boolean alpha = from.isAlphaPremultiplied();
							WritableRaster raster = from.copyData(null);
							master = new BufferedImage(model, raster, alpha, null);

						} else if (disposal.equals("restoreToBackgroundColor") && backgroundColor != null) {
							if (!hasBackround || frameIndex > 1) {
								if (buffer == null)
									return;

								Frame last = buffer.get(frameIndex - 1).getFrame();
								master.createGraphics().fillRect(lastx, lasty, last.getWidth(), last.getHeight());
							}
						}
						master.createGraphics().drawImage(image, x, y, null);

						lastx = x;
						lasty = y;
					}

					{
						BufferedImage copy;

						{
							ColorModel model = master.getColorModel();
							boolean alpha = master.isAlphaPremultiplied();
							WritableRaster raster = master.copyData(null);
							copy = new BufferedImage(model, raster, alpha, null);
						}

						GifFrame gifframe = new GifFrame(copy, disposal, delay);

						if (buffer != null)
							buffer.add(gifframe);
						else
							return;
					}

					master.flush();

				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				reader.dispose();
			}
		}

	}

	private class GifFrame {
		private final Frame frame;
		private final String disposal;
		private final int delay;

		public GifFrame(BufferedImage image, String disposal, int delay) {
			frame = new Frame(image);
			this.disposal = disposal;

			if (delay == 0)
				delay = 100;
			this.delay = delay;
		}

		public BufferedImage getImage() {
			return frame.getBufferedImage();
		}

		public Frame getFrame() {
			return frame;
		}

		public String getDisposal() {
			return disposal;
		}

		public int getDelay() {
			return delay;
		}

	}
}
