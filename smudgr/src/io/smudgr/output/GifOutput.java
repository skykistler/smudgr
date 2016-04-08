package io.smudgr.output;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import io.smudgr.source.Frame;

public class GifOutput implements FrameOutput {
	public final static int TARGET_GIF_MS = 50;

	private String path;

	private ArrayList<GifFrame> frames = new ArrayList<GifFrame>();

	private ImageOutputStream output;
	private ImageWriter gifWriter;
	private ImageWriteParam imageWriteParam;
	private ImageTypeSpecifier imageTypeSpecifier;

	private IIOMetadataNode commentsNode;
	private IIOMetadataNode appExtensionsNode;

	private boolean closed;

	public GifOutput(String path) {
		this.path = path;
	}

	public void open() {
		Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("gif");

		gifWriter = iter.next();

		imageWriteParam = gifWriter.getDefaultWriteParam();
		imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);

		// Make necessary comment node
		commentsNode = new IIOMetadataNode("CommentExtensions");
		commentsNode.setAttribute("CommentExtension", "Made with smudgr");

		// Make necessary app extensions node
		appExtensionsNode = new IIOMetadataNode("ApplicationExtensions");
		IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");
		child.setAttribute("applicationID", "NETSCAPE");
		child.setAttribute("authenticationCode", "2.0");
		child.setUserObject(new byte[] { 0x1, 0, 0 });
		appExtensionsNode.appendChild(child);

		// Set output file
		try {
			output = new FileImageOutputStream(new File(path));
			gifWriter.setOutput(output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addFrame(Frame f) {
		if (closed)
			return;

		frames.add(new GifFrame(f, TARGET_GIF_MS));
	}

	public void close() {

		if (closed || frames.size() == 0)
			return;

		closed = true;

		(new Thread() {
			public void run() {
				try {
					gifWriter.prepareWriteSequence(null);

					for (GifFrame gf : frames)
						writeFrame(gf);

					gifWriter.endWriteSequence();
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void writeFrame(GifFrame frame) throws IOException {
		IIOMetadata imageMetaData = gifWriter.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);

		String metaFormatName = imageMetaData.getNativeMetadataFormatName();

		IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);

		// Make new graphics node for each frame, in order to set delay
		IIOMetadataNode graphicsControlExtensionNode = new IIOMetadataNode("GraphicControlExtension");
		graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
		graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
		graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
		graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString((int) (frame.getDelayMs() / 10d)));
		graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");

		// Reuse these nodes each frame
		root.appendChild(commentsNode);
		root.appendChild(appExtensionsNode);

		imageMetaData.setFromTree(metaFormatName, root);

		gifWriter.writeToSequence(new IIOImage(frame.getFrame().getBufferedImage(), null, imageMetaData), imageWriteParam);
	}

	private class GifFrame {
		private Frame frame;
		private int delayMs;

		public GifFrame(Frame frame, int delayMs) {
			this.frame = frame;
			this.delayMs = delayMs;
		}

		public Frame getFrame() {
			return frame;
		}

		public int getDelayMs() {
			return delayMs;
		}

	}

}
