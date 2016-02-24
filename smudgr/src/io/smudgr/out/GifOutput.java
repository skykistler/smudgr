package io.smudgr.out;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

public class GifOutput implements Output {
	private String path;
	private int delay;
	private int loop;

	private ImageOutputStream output;
	private ImageWriter gifWriter;
	private ImageWriteParam imageWriteParam;
	private IIOMetadata imageMetaData;

	public GifOutput(String path, int delayMS, boolean loop) {
		this.path = path;
		this.delay = delayMS;
		this.loop = loop ? 0 : 1;
	}

	public void open() {
		Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("gif");

		gifWriter = iter.next();

		imageWriteParam = gifWriter.getDefaultWriteParam();
		ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);

		imageMetaData = gifWriter.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);

		String metaFormatName = imageMetaData.getNativeMetadataFormatName();

		IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);

		IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");

		graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
		graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
		graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
		graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString(delay / 10));
		graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");

		IIOMetadataNode commentsNode = getNode(root, "CommentExtensions");
		commentsNode.setAttribute("CommentExtension", "Created by MAH");

		IIOMetadataNode appEntensionsNode = getNode(root, "ApplicationExtensions");

		IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");

		child.setAttribute("applicationID", "NETSCAPE");
		child.setAttribute("authenticationCode", "2.0");

		child.setUserObject(new byte[] { 0x1, (byte) (loop & 0xFF), (byte) ((loop >> 8) & 0xFF) });
		appEntensionsNode.appendChild(child);
		try {
			imageMetaData.setFromTree(metaFormatName, root);

			output = new FileImageOutputStream(new File(path));
			gifWriter.setOutput(output);

			gifWriter.prepareWriteSequence(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addFrame(Frame f) {
		try {
			gifWriter.writeToSequence(new IIOImage(f.getBufferedImage(), null, imageMetaData), imageWriteParam);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			gifWriter.endWriteSequence();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
		int nNodes = rootNode.getLength();
		for (int i = 0; i < nNodes; i++) {
			if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName) == 0) {
				return ((IIOMetadataNode) rootNode.item(i));
			}
		}
		IIOMetadataNode node = new IIOMetadataNode(nodeName);
		rootNode.appendChild(node);

		return node;
	}

}
