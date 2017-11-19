package io.smudgr.api.commands;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.app.project.ProjectItem;
import io.smudgr.util.PixelFrame;
import io.smudgr.util.source.Source;

/**
 * Gets a thumbnail preview of a given source
 */
public class SourceThumbnail implements ApiCommand {

	@Override
	public String getCommand() {
		return "source.thumbnail";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		// Attempt to get ProjectItem at given ID
		int id = Integer.parseInt(data.get("id"));
		ProjectItem item = getProject().getItem(id);

		// Prepare response for this ID
		ApiMessage response = new ApiMessage("source", id);

		// Assert ID actually points to a Source
		if (item == null || !(item instanceof Source)) {
			response.put("message", "Did not find source with ID: " + id);
			return ApiMessage.failed(getCommand(), response);
		}

		// Get thumbnail from source
		Source source = (Source) item;
		PixelFrame thumbnail = source.getThumbnail();

		// Fail if source hasn't generated a thumbnail
		if (thumbnail == null) {
			response.put("message", "No thumbnail generated for: " + id);
			return ApiMessage.failed(getCommand(), response);
		}

		// Attempt to generate PNG bytes
		try {
			// Copy thumbnail to BufferedImage
			BufferedImage image = new BufferedImage(thumbnail.getWidth(), thumbnail.getHeight(), BufferedImage.TYPE_INT_ARGB);
			thumbnail.drawTo(image);

			// Write out PNG bytes
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			ImageIO.write(image, "png", bytes);

			// Base64 encode PNG bytes
			byte[] encodedBytes = Base64.getEncoder().encode(bytes.toByteArray());

			// Successfully return the thumbnail as a Base64 string
			response.put("thumbnail", new String(encodedBytes));
			return ApiMessage.success(getCommand(), response);

		} catch (IOException e) {
			e.printStackTrace();

			response.put("message", "Failed to generate thumbnail for: " + id);
			return ApiMessage.failed(getCommand(), response);
		}
	}

}
