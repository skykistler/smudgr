package io.smudgr.extensions.image.utility;

import io.smudgr.engine.Smudge;
import io.smudgr.engine.SmudgeComponent;
import io.smudgr.extensions.image.utility.color.Painter;
import io.smudgr.extensions.image.utility.crop.Cropper;
import io.smudgr.extensions.image.utility.rotate.Rotator;
import io.smudgr.extensions.image.utility.scale.Scaler;
import io.smudgr.util.Frame;
import io.smudgr.util.PixelFrame;

/**
 * The {@link Utility} smudge is a container for {@link UtilityComponent}
 * instances. Each {@link UtilityComponent} serves a role in determining the
 * entire {@link Utility} behavior.
 */
public class Utility extends Smudge {

	@Override
	public String getTypeName() {
		return "Utility";
	}

	@Override
	public String getTypeIdentifier() {
		return "utility";
	}

	@Override
	public void onInit() {
		if (rotator == null)
			add(new Rotator());

		if (scaler == null)
			add(new Scaler());

		if (painter == null)
			add(new Painter());

		if (cropper == null)
			add(new Cropper());
	}

	protected PixelFrame lastFrame;
	protected PixelFrame lastProcessedFrame;

	private Rotator rotator;
	private Scaler scaler;
	private Painter painter;
	private Cropper cropper;

	@Override
	public Frame smudge(Frame data) {
		PixelFrame image = (PixelFrame) data;

		double currentZoomFactor = scaler.getZoomFactor();

		// Need to optimize this to reuse the last croppedFrame so that we don't
		// have to abuse Frame.dispose and new Frame.

		// We would prefer to color a smaller amount of space, and then let the
		// transformation utilities (crop, zoom, rotate) expand space as they
		// will do anyway. Zoom and crop could probably be jointly optimized.

		if (currentZoomFactor > 1.0) {
			painter.color(image);
			image = cropper.crop(image);
			image = rotator.rotate(image);
			image = scaler.scale(image);
		} else {
			image = cropper.crop(image);
			image = rotator.rotate(image);
			image = scaler.scale(image);
			painter.color(image);
		}

		return image;
	}

	@Override
	protected void onAdd(SmudgeComponent component) {
		if (component instanceof Rotator)
			rotator = (Rotator) component;

		if (component instanceof Scaler)
			scaler = (Scaler) component;

		if (component instanceof Painter)
			painter = (Painter) component;

		if (component instanceof Cropper)
			cropper = (Cropper) component;
	}

}
