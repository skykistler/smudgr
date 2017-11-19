package io.smudgr.util.source.image;

import java.awt.Color;
import java.awt.Graphics;
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

import io.smudgr.app.controller.Controller;
import io.smudgr.util.PixelFrame;

/**
 * The {@link Gif} source loads an animate GIF and returns the current frame in
 * time with the application.
 */
public class Gif implements AnimatedSource {

	@Override
	public String getTypeIdentifier() {
		return "gif";
	}

	@Override
	public String getTypeName() {
		return "Gif";
	}

	@Override
	public String getName() {
		return filename;
	}

	private String filename;

	private BufferThread bufferer;
	private volatile ArrayList<GifFrame> buffer = new ArrayList<GifFrame>();

	private PixelFrame thumbnail;
	private boolean generatedFinalThumbnail;

	private int ticks;
	private volatile int currentFrame;
	private double speedFactor = 1;

	/**
	 * Create a new {@link Gif} loaded from the given filename.
	 *
	 * @param filename
	 *            path
	 */
	public Gif(String filename) {
		this.filename = filename;
	}

	@Override
	public void init() {
		bufferer = new BufferThread();
		bufferer.start();
	}

	@Override
	public void update() {
		if (buffer.size() == 0)
			return;

		ticks++;

		currentFrame %= buffer.size();
		GifFrame frame = buffer.get(currentFrame);
		if (frame == null) {
			currentFrame = 0;
			ticks = 0;
			return;
		}

		int delay = Controller.getInstance().ticksToMs(ticks);
		if (frame.getDelay() / speedFactor <= delay) {
			currentFrame++;

			ticks = 0;
		}
	}

	@Override
	public PixelFrame getFrame() {
		if (buffer.size() == 0)
			return null;

		currentFrame %= buffer.size();
		return buffer.get(currentFrame).getFrame();
	}

	@Override
	public PixelFrame getThumbnail() {
		// If no frames loaded yet, no preview
		if (buffer.size() == 0)
			return null;

		// Preview the latest frame while buffering
		if (bufferer.loading) {
			if (thumbnail != null)
				thumbnail.dispose();

			thumbnail = buffer.get(buffer.size() - 1).getFrame().generateThumbnail();
		}
		// Otherwise, make a thumbnail of the middle frame
		else if (!generatedFinalThumbnail) {
			if (thumbnail != null)
				thumbnail.dispose();

			/*
			 * If there's two frames or less, this will select just the first
			 * frame.
			 */
			int frameIndex = Math.max(2, buffer.size()) / 2 - 1;

			thumbnail = buffer.get(frameIndex).getFrame().generateThumbnail();

			// Prevent further thumbnail generation
			generatedFinalThumbnail = true;
		}

		return thumbnail;
	}

	@Override
	public void dispose() {
		bufferer.stop();

		for (int i = 0; i < buffer.size(); i++)
			buffer.get(i).dispose();

		buffer.clear();
	}

	@Override
	public void setSpeed(double speed) {
		speed = speed < 0 ? 0 : speed;
		speed = speed > 4 ? 4 : speed;

		speedFactor = speed;
	}

	@Override
	public double getSpeed() {
		return speedFactor;
	}

	static ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();

	class BufferThread implements Runnable {

		private boolean loading;

		public void start() {
			Thread t = new Thread(this);
			t.setName("GIF Reader: " + filename);
			t.start();
		}

		public void stop() {
			loading = false;
		}

		// This is just absolutely terrifying
		@Override
		public void run() {
			loading = true;

			synchronized (reader) {
				try {
					reader.setInput(ImageIO.createImageInputStream(new FileInputStream(new File(filename))));

					int lastx = 0;
					int lasty = 0;

					int width = -1;
					int height = -1;

					IIOMetadata metadata = reader.getStreamMetadata();

					Color backgroundColor = null;

					if (metadata != null) {
						IIOMetadataNode globalRoot = (IIOMetadataNode) metadata
								.getAsTree(metadata.getNativeMetadataFormatName());

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
					int frameIndex = 0;

					while (loading) {
						BufferedImage image;
						try {
							image = reader.read(frameIndex);
						} catch (IndexOutOfBoundsException e) {
							System.out.println("Finished loading a GIF: " + filename);
							break;
						}

						if (width == -1 || height == -1) {
							width = image.getWidth();
							height = image.getHeight();
						}

						IIOMetadataNode root = (IIOMetadataNode) reader.getImageMetadata(frameIndex)
								.getAsTree("javax_imageio_gif_image_1.0");
						IIOMetadataNode gce = (IIOMetadataNode) root.getElementsByTagName("GraphicControlExtension")
								.item(0);
						NodeList children = root.getChildNodes();

						int delay = Integer.valueOf(gce.getAttribute("delayTime")) * 10;

						String disposal = gce.getAttribute("disposalMethod");

						if (master == null) {
							master = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
							Graphics g = master.createGraphics();
							g.setColor(backgroundColor);
							g.fillRect(0, 0, master.getWidth(), master.getHeight());

							hasBackround = image.getWidth() == width && image.getHeight() == height;

							g.drawImage(image, 0, 0, null);
							g.dispose();
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

								for (int i = frameIndex - 1; i >= 0; i--)
									if (!buffer.get(i).getDisposal().equals("restoreToPrevious") || frameIndex == 0) {
										from = buffer.get(i).getImage();
										break;
									}

								ColorModel model = from.getColorModel();
								boolean alpha = from.isAlphaPremultiplied();
								WritableRaster raster = from.copyData(null);
								master = new BufferedImage(model, raster, alpha, null);

							} else if (disposal.equals("restoreToBackgroundColor") && backgroundColor != null) {
								if (!hasBackround || frameIndex > 1) {
									PixelFrame last = buffer.get(frameIndex - 1).getFrame();

									Graphics g = master.createGraphics();
									g.fillRect(lastx, lasty, last.getWidth(), last.getHeight());
									g.dispose();
								}
							}

							Graphics g = master.createGraphics();
							g.drawImage(image, x, y, null);
							g.dispose();

							lastx = x;
							lasty = y;
						}

						ColorModel model = master.getColorModel();
						boolean alpha = master.isAlphaPremultiplied();
						WritableRaster raster = master.copyData(null);
						BufferedImage copy = new BufferedImage(model, raster, alpha, null);

						GifFrame gifframe = new GifFrame(copy, disposal, delay);

						buffer.add(frameIndex, gifframe);

						master.flush();

						frameIndex++;

					}
				} catch (IndexOutOfBoundsException e) {
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					reader.dispose();

					for (GifFrame frame : buffer) {
						frame.reduce();
					}

					loading = false;

					/*
					 * Generate a thumbnail to keep in memory even if this
					 * source is disposed
					 */
					getThumbnail();

					System.gc();
				}
			}
		}

	}

	private class GifFrame {
		private PixelFrame frame;
		private BufferedImage image;
		private String disposal;
		private int delay;

		public GifFrame(BufferedImage image, String disposal, int delay) {
			frame = new PixelFrame(image);

			this.image = image;

			this.disposal = disposal;

			if (delay == 0)
				delay = 100;
			this.delay = delay;
		}

		public BufferedImage getImage() {
			return image;
		}

		public PixelFrame getFrame() {
			return frame;
		}

		public String getDisposal() {
			return disposal;
		}

		public int getDelay() {
			return delay;
		}

		public void reduce() {
			if (image != null)
				image.flush();

			image = null;
			disposal = null;
		}

		public void dispose() {
			frame.dispose();
		}

	}
}
