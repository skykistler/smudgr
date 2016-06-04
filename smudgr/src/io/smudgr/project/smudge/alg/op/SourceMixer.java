package io.smudgr.project.smudge.alg.op;

import io.smudgr.project.smudge.alg.PixelIndexList;
import io.smudgr.project.smudge.alg.math.blend.Blender;
import io.smudgr.project.smudge.alg.math.blend.MaxBlender;
import io.smudgr.project.smudge.alg.math.blend.MinBlender;
import io.smudgr.project.smudge.alg.math.blend.MultiplyBlender;
import io.smudgr.project.smudge.alg.math.blend.NormalBlender;
import io.smudgr.project.smudge.param.BlendParameter;
import io.smudgr.project.smudge.param.BooleanParameter;
import io.smudgr.project.smudge.param.NumberParameter;
import io.smudgr.project.smudge.source.Image;
import io.smudgr.project.smudge.source.Source;
import io.smudgr.project.smudge.util.Frame;

public class SourceMixer extends Operation {

	public String getName() {
		return "Source Mixer";
	}

	private Frame mixFrame;

	private Source mixSource = new Image("data/firemix.png");

	NumberParameter size = new NumberParameter("Size", this, 1, 0, 1.5, 0.01);
	NumberParameter translateX = new NumberParameter("Translation X", this, 0, -1, 1, 0.01);
	NumberParameter translateY = new NumberParameter("Translation Y", this, 0, -1, 1, 0.01);
	BooleanParameter scaleToChange = new BooleanParameter("Scale To Change", this, false);
	BooleanParameter fitOnLoading = new BooleanParameter("Fit on Loading", this, false);
	BlendParameter blenders = new BlendParameter("Blender", this, new NormalBlender());

	private double lastTranslateX, lastTranslateY, lastSize;
	private int lastBaseW, lastBaseH;
	private int lastMixW, lastMixH;
	private int currentMixW, currentMixH;

	private double currentTransX, currentTransY, currentSize;

	Blender blender;

	// displacement from mix frame center to base frame center
	int dx, dy = 0;

	private void addBlenders() {
		blenders.add(new MaxBlender());
		blenders.add(new MinBlender());
		blenders.add(new MultiplyBlender());
	}

	public void init() {
		mixSource.init();
		addBlenders();
		lastTranslateX = translateX.getValue();
		lastTranslateY = translateY.getValue();
		lastSize = size.getValue();
	}

	public void execute(Frame img) {
		blender = blenders.getValue();

		update(img);

		if (mixFrame == null)
			return;

		blend(img);
	}

	private void blend(Frame img) {
		// Specific to dimension changes
		int mixW = mixFrame.getWidth();
		int mixH = mixFrame.getHeight();
		int baseW = img.getWidth();

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

	private void update(Frame img) {

		int baseW = img.getWidth();
		int baseH = img.getHeight();

		updateSize(img, baseW, baseH);

		// update the mix frame dimensions variables post updateSize
		int mixW = mixFrame.getWidth();
		int mixH = mixFrame.getHeight();

		// Update translation of frame we are mixing in...
		updateTranslation(baseW, baseH, mixW, mixH);

	}

	// Update the size of the mix frame, not the base frame we are blending into
	private void updateSize(Frame img, int baseW, int baseH) {

		// If the base image's dimensions have changed, adjust the size
		// Given the param scaleToChange is set to true.
		if (lastBaseW != baseW || lastBaseH != baseH) {

			double baseChangeY = 0;
			double baseChangeX = 0;
			if (scaleToChange.getValue() == true) {
				// How much has the base image dimenions changed?
				if (lastBaseW != 0 && lastBaseH == 0) {
					baseChangeX = lastBaseW - (baseW / lastBaseW);
					baseChangeY = lastBaseH - (baseH / lastBaseH);
				}

				double absBaseChangeX = Math.abs(baseChangeX);
				double absBaseChangeY = Math.abs(baseChangeY);

				// proportion of base image change will affect size of mix frame
				// Pick the greatest change between width and height change
				double sizeChange = absBaseChangeX > absBaseChangeY ? baseChangeX : baseChangeY;

				// Only change the size param, so we can change the size at the
				// end of this
				// update function
				adjustSizeParam(sizeChange);
			}
		}

		// update base dimensions to reflect current base dimensions
		lastBaseW = baseW;
		lastBaseH = baseH;

		currentSize = size.getValue();

		mixFrame = mixSource.getFrame(currentSize);

		// Update the lastSize variable to reflect current
		lastSize = currentSize;

	}

	private void updateTranslation(int baseW, int baseH, int mixW, int mixH) {
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

	private void mix(int coord, Frame mix, Frame img, int x, int y) {
		int baseColor = img.pixels[coord];
		int mixInColor = mix.get(x, y);

		int compositeColor = blender.blend(baseColor, mixInColor);

		img.pixels[coord] = compositeColor; /*- ColorHelper.color(255, newR, newG, newB); */
	}

	public void adjustSizeParam(double valueAdded) {
		double sizeValue = size.getValue();
		sizeValue += valueAdded;
		size.setValue(Math.min(Math.max(0, sizeValue), size.getMax()));
	}

	private boolean inFrame(int mixW, int mixH, int x, int y) {
		return x < mixW && y < mixH && x >= 0 && y >= 0;
	}

}
