package io.smudgr.util.source;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import io.smudgr.util.Frame;

/**
 * The {@link Image} source represents a static frame from a loaded image file
 * (i.e. png, jpg).
 */
public class Image implements Source {

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

	private Frame frame;
	private Frame thumbnail;

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

			frame = new Frame(loaded);
		} catch (IOException e) {
			System.out.println("Error loading: " + filename);
			e.printStackTrace();
		}
	}

	@Override
	public void update() {

	}

	@Override
	public Frame getFrame() {
		return frame;
	}

	@Override
	public Frame getThumbnail() {
		if (thumbnail != null)
			return thumbnail;

		if (frame == null || frame.isDisposed())
			return null;

		return thumbnail = frame.generateThumbnail();
	}

	@Override
	public void dispose() {
		frame.dispose();
	}

	@Override
	public String toString() {
		return filename;
	}

}
