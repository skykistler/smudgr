package io.smudgr.project.smudge.alg.op;

import io.smudgr.project.smudge.alg.PixelIndexList;
import io.smudgr.project.smudge.alg.math.ColorHelper;
import io.smudgr.project.smudge.param.BooleanParameter;
import io.smudgr.project.smudge.param.NumberParameter;
import io.smudgr.project.smudge.source.Source;
import io.smudgr.project.smudge.util.Frame;

public class SourceMixer extends Operation {

	public String getName() {
		return "Source Mixer";
	}

	private Frame mixFrame;

	private Source mixSource;

	NumberParameter size = new NumberParameter("Size", this, 0, -1, 1, 0.01);
	NumberParameter translateX = new NumberParameter("Translation X", this, 0, -1, 1, 0.01);
	NumberParameter translateY = new NumberParameter("Translation Y", this, 0, -1, 1, 0.01);
	BooleanParameter scaleToChange = new BooleanParameter("Scale To Change", this, true);
	BooleanParameter fitOnLoading = new BooleanParameter("Fit on Loading", this, false);

	private double lastTranslateX, lastTranslateY, lastSize;
	private int lastBaseW, lastBaseH;
	private int lastMixW, lastMixH;
	private int currentMixW, currentMixH;

	private double currentTransX, currentTransY, currentSize;
	private double k;

	// displacement from mix frame center to base frame center
	int dx, dy = 0;

	public void init() {
		mixSource.init();
		lastTranslateX = translateX.getValue();
		lastTranslateY = translateY.getValue();
		lastSize = size.getValue();
	}

	public void execute(Frame img) {
		update(img);

		if (mixFrame == null)
			return;

		blend(img);
	}

	public void blend(Frame img) {
		// Specific to dimension changes
		int mixW = mixFrame.getWidth();
		int mixH = mixFrame.getHeight();
		int baseW = img.getWidth();
		int baseH = img.getHeight();

		int x, y;
		for (PixelIndexList coords : getAlgorithm().getSelectedPixels()) {
			for (int index = 0; index < coords.size(); index++) {
				int coord = coords.get(index);
				x = (coord % baseW) - dx;
				y = ((coord - x) / baseW) - dy;

				if (inFrame(mixW, mixH, x, y))
					mix(coord, mixFrame, img, x, y);
			}
		}
	}

	public void update(Frame img) {
		// need to adjust this update function to work with new mixFrames so
		// fitting them to screen etc.

		// pull from the source if there was a change in the source
		// This is the part that needs to be improved
		// TODO improve grabbing frames for mixing
		if (mixSource.getFrame() != null)
			mixFrame = mixSource.getFrame().copy();
		else
			return;

		// Specific to dimension changes
		int mixW = mixFrame.getWidth();
		int mixH = mixFrame.getHeight();
		int baseW = img.getWidth();
		int baseH = img.getHeight();

		updateSize(img, baseW, baseH, mixW, mixH);

		// update the mix frame dimensions variables post updateSize
		mixW = mixFrame.getWidth();
		mixH = mixFrame.getHeight();

		// Update translation of frame we are mixing in...
		updateTranslation(baseW, baseH, mixW, mixH);

	}

	public void updateTranslation(int baseW, int baseH, int mixW, int mixH) {
		// translation from displacement vector <dx, dy>
		int transX, transY;

		// set current translation parameter values
		currentTransX = translateX.getValue();
		currentTransY = translateY.getValue();

		// translation on X
		if (currentTransX != lastTranslateX) {
			transX = (int) (translateX.getValue() * baseW); // Maybe it should
															// be times mixW
			dx = baseW / 2 - mixW / 2 + transX;
		}
		lastTranslateX = currentTransX;

		// translation on Y
		if (currentTransY != lastTranslateY) {
			transY = (int) (translateY.getValue() * baseH); // Maybe it should
															// be times mixH
			dy = baseH / 2 - mixH / 2 + transY;
		}
		lastTranslateY = currentTransY;

	}

	// Update the size of the mix frame, not the base frame we are blending into
	public void updateSize(Frame img, int baseW, int baseH, int mixW, int mixH) {

		Boolean sizeChanged = false;

		// If the base image's dimensions have changed, adjust the size
		// Given the param scaleToChange is set to true.
		if (lastBaseW != baseW || lastBaseH != baseH) {

			double baseChangeY, baseChangeX;
			if (scaleToChange.getValue() == true) {
				// How much has the base image dimenions changed?
				baseChangeX = lastBaseW - (baseW / lastBaseW);
				baseChangeY = lastBaseH - (baseH / lastBaseH);

				double absBaseChangeX = Math.abs(baseChangeX);
				double absBaseChangeY = Math.abs(baseChangeY);

				// proportion of base image change will affect size of mix frame
				// Pick the greatest change between width and height change
				double sizeChange = absBaseChangeX > absBaseChangeY ? baseChangeX : baseChangeY;

				// Only change the size param, so we can change the size at the
				// end of this
				// update function
				adjustSizeParam(sizeChange);
				sizeChanged = true;
			}
		}

		// update base dimensions to reflect current base dimensions
		lastBaseW = baseW;
		lastBaseH = baseH;

		currentSize = size.getValue();
		// Did the size parameter value change?
		if (currentSize != lastSize) {
			sizeChanged = true;
		}

		// If the size has adjusted for the mix frame, then resize.
		if (sizeChanged) {
			// If the change of size is down, use the same mixFrame but
			// calculate the adjustment
			// and set the size param to reflect this change.
			int newMixW = (int) (mixSource.getFrame().getWidth() * currentSize);
			int newMixH = (int) (mixSource.getFrame().getHeight() * currentSize);
			mixFrame.dispose();
			Frame bufferFrame = mixSource.getFrame().resize(newMixW, newMixH);
			mixFrame = bufferFrame;
		}
		// Update the lastSize variable to reflect current
		lastSize = currentSize;

	}

	private void mix(int coord, Frame mix, Frame img, int x, int y) {
		int mixColor = mix.get(x, y);
		int origColor = img.pixels[coord];

		int alpha = ColorHelper.alpha(origColor);
		if (alpha == 255) {
			img.pixels[coord] = origColor;
			return;
		} else if (alpha == 0)
			return;

		double a = alpha / 255;

		int newR = (int) (a * ColorHelper.red(origColor) + (1 - a) * ColorHelper.red(origColor));
		int newG = (int) (a * ColorHelper.green(origColor) + (1 - a) * ColorHelper.green(origColor));
		int newB = (int) (a * ColorHelper.blue(origColor) + (1 - a) * ColorHelper.blue(origColor));

		img.pixels[coord] = ColorHelper.color(255, newR, newG, newB);
	}

	public void adjustSizeParam(double valueAdded) {
		double sizeValue = size.getValue();
		sizeValue += valueAdded;
		size.setValue(Math.min(Math.max(0, sizeValue), 1.5));
	}

	private boolean inFrame(int mixW, int mixH, int x, int y) {
		return x < mixW && y < mixH && x >= 0 && y >= 0;
	}

}
