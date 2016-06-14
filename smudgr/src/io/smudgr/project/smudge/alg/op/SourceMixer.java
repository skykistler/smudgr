package io.smudgr.project.smudge.alg.op;

import io.smudgr.project.smudge.alg.PixelIndexList;
import io.smudgr.project.smudge.alg.math.blend.AverageBlender;
import io.smudgr.project.smudge.alg.math.blend.BitwiseAndBlender;
import io.smudgr.project.smudge.alg.math.blend.BitwiseOrBlender;
import io.smudgr.project.smudge.alg.math.blend.Blender;
import io.smudgr.project.smudge.alg.math.blend.HueBlender;
import io.smudgr.project.smudge.alg.math.blend.MaxBlender;
import io.smudgr.project.smudge.alg.math.blend.MinBlender;
import io.smudgr.project.smudge.alg.math.blend.NormalBlender;
import io.smudgr.project.smudge.param.BlendParameter;
import io.smudgr.project.smudge.param.NumberParameter;
import io.smudgr.project.smudge.source.Image;
import io.smudgr.project.smudge.source.Source;
import io.smudgr.project.smudge.util.Frame;

public class SourceMixer extends Operation {

	public String getName() {
		return "Source Mixer";
	}

	NumberParameter size = new NumberParameter("Size", this, 1, 0, 1.5, 0.01);
	NumberParameter translateX = new NumberParameter("Translation X", this, 0, -1, 1, 0.01);
	NumberParameter translateY = new NumberParameter("Translation Y", this, 0, -1, 1, 0.01);
	BlendParameter blenders = new BlendParameter("Blender", this, new NormalBlender());

	private Frame mixFrame;
	private Source mixSource = new Image("data/mix/firemix.png");

	int MAX_HEIGHT = 4000;
	int MAX_WIDTH = 2200;

	int lastMixW = 0;
	int lastMixH = 0;

	Blender blender;

	public void init() {
		mixSource.init();
		blenders.add(new MaxBlender());
		blenders.add(new MinBlender());
		blenders.add(new BitwiseAndBlender());
		blenders.add(new BitwiseOrBlender());
		blenders.add(new AverageBlender());
		blenders.add(new HueBlender());
	}

	public void setSource(Source s) {
		if (mixSource != null)
			mixSource.dispose();
		mixSource = s;
		mixSource.init();
	}

	public void execute(Frame img) {
		blender = blenders.getValue();
		blend(img);
	}

	private void blend(Frame img) {

		int baseW = img.getWidth();
		int baseH = img.getHeight();

		if (mixFrame != null)
			mixFrame.dispose();

		Frame frameFromSource = mixSource.getFrame();
		if (frameFromSource != null) {
			int frameWidth = frameFromSource.getWidth();
			int frameHeight = frameFromSource.getHeight();

			/*- if the source frame we are mixing in is different, then change size to reflect fit to base frame*/
			if (frameWidth != lastMixW || frameHeight != lastMixH) {
				double newWidth = frameWidth;
				double newHeight = frameHeight;

				boolean tooSmall = newWidth < baseW && newHeight < baseH;

				if (frameWidth > baseW || tooSmall) {
					newWidth = baseW;
					newHeight = (newWidth * frameHeight) / frameWidth;
				}

				if (newHeight > baseH) {
					newHeight = baseH;
					newWidth = (newHeight * frameWidth) / frameHeight;
				}
				double w = newWidth;

				double scaleCoefficient = Math.max(0, Math.min(1.5, w / frameWidth));
				size.setValue(scaleCoefficient);

			}
			lastMixW = frameWidth;
			lastMixH = frameHeight;

			// Enforce limit on resize dimensions
			double scale = size.getValue();
			scale = scale * frameWidth > MAX_WIDTH ? (double) (MAX_WIDTH) / frameWidth : scale;
			scale = scale * frameHeight > MAX_HEIGHT ? (double) (MAX_HEIGHT) / frameHeight : scale;
			mixFrame = frameFromSource.resize(scale);
		} else
			return;

		// update the mix frame dimensions variables after resizing
		int mixW = mixFrame.getWidth();
		int mixH = mixFrame.getHeight();

		int transX = (int) (translateX.getValue() * baseW);
		int dx = baseW / 2 - mixW / 2 + transX;

		int transY = (int) (translateY.getValue() * baseH);
		int dy = baseH / 2 - mixH / 2 + transY;

		int x, y;
		for (PixelIndexList coords : getAlgorithm().getSelectedPixels()) {
			for (int index = 0; index < coords.size(); index++) {
				int coord = coords.get(index);
				x = (coord % baseW) - dx;
				y = ((coord - x) / baseW) - dy;

				if (inFrame(mixW, mixH, x, y)) {
					int baseColor = img.pixels[coord];
					int mixInColor = mixFrame.get(x, y);
					img.pixels[coord] = blender.blend(mixInColor, baseColor);
				}
			}
		}
	}

	private boolean inFrame(int mixW, int mixH, int x, int y) {
		return x < mixW && y < mixH && x >= 0 && y >= 0;
	}

}
