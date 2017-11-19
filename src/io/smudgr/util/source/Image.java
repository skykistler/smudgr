package io.smudgr.util.source;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import io.smudgr.util.PixelFrame;

/**
 * The {@link Image} source represents a static frame from a loaded image file
 * (i.e. png, jpg).
 */
public class Image implements Source {

	// Rudimentary limits on max pixel resolution allowed for live processing.
	private static final int MAX_LIVE_WIDTH = 2000;
	private static final int MAX_LIVE_HEIGHT = 2000;

	@Override
	public String getTypeIdentifier() {
		return "image";
	}

	@Override
	public String getTypeName() {
		return "Image";
	}

	@Override
	public String getName() {
		return filename;
	}

	private String filename;

	private PixelFrame frame;
	private PixelFrame liveScaled;
	private PixelFrame thumbnail;

	/**
	 * Create a new {@link Image} loaded from the given filename.
	 * 
	 * @param filename
	 *            path
	 */
	public Image(String filename) {
		this.filename = filename;
	}

	@Override
	public void init() {
		try {
			BufferedImage loaded = ImageIO.read(new File(filename));

			if (loaded == null) {
				System.out.println("Unable to load: " + filename);
				return;
			}

			frame = new PixelFrame(loaded);

			if (frame.getWidth() > MAX_LIVE_WIDTH || frame.getHeight() > MAX_LIVE_HEIGHT)
				liveScaled = frame.fitToSize(MAX_LIVE_WIDTH, MAX_LIVE_HEIGHT, false);

		} catch (IOException e) {
			System.out.println("Error loading: " + filename);
			e.printStackTrace();
		}
	}

	@Override
	public void update() {

	}

	@Override
	public PixelFrame getFrame() {
		// If there is a downsampled version for live processing, return that
		if (liveScaled != null)
			return liveScaled;

		return frame;
	}

	@Override
	public PixelFrame getThumbnail() {
		if (thumbnail != null)
			return thumbnail;

		if (frame == null || frame.isDisposed())
			return null;

		return thumbnail = frame.generateThumbnail();
	}

	@Override
	public void dispose() {
		frame.dispose();
		liveScaled.dispose();
	}

	@Override
	public String toString() {
		return filename;
	}

}
