package io.smudgr.engine.alg.op;

import io.smudgr.engine.alg.PixelIndexList;
import io.smudgr.engine.alg.math.blend.AverageBlender;
import io.smudgr.engine.alg.math.blend.BitwiseAndBlender;
import io.smudgr.engine.alg.math.blend.BitwiseOrBlender;
import io.smudgr.engine.alg.math.blend.Blender;
import io.smudgr.engine.alg.math.blend.HueBlender;
import io.smudgr.engine.alg.math.blend.LumaBlender;
import io.smudgr.engine.alg.math.blend.MaxBlender;
import io.smudgr.engine.alg.math.blend.MinBlender;
import io.smudgr.engine.alg.math.blend.NormalBlender;
import io.smudgr.engine.param.BlendParameter;
import io.smudgr.engine.param.NumberParameter;
import io.smudgr.util.PixelFrame;
import io.smudgr.util.source.Image;
import io.smudgr.util.source.Source;

/**
 * Source Mixer allows the traditional blending of two images, one being the
 * passed in {@link PixelFrame} and another being a configurable {@link Source}
 */
public class SourceMixer extends ParallelOperation {

	@Override
	public String getTypeName() {
		return "Source Mixer";
	}

	NumberParameter size = new NumberParameter("Size", this, 1, 0, 1.5, 0.01);
	NumberParameter translateX = new NumberParameter("Translation X", this, 0, -1, 1, 0.01);
	NumberParameter translateY = new NumberParameter("Translation Y", this, 0, -1, 1, 0.01);
	BlendParameter blenders = new BlendParameter("Blender", this, new NormalBlender());

	private PixelFrame mixFrame;
	private Source mixSource = new Image("data/mix/firemix.png");

	private int MAX_HEIGHT = 4000;
	private int MAX_WIDTH = 2200;
	private int lastMixW = 0;
	private int lastMixH = 0;

	private int frameWidth, frameHeight;
	private int baseW, baseH, dx, dy, mixW, mixH, transX, transY;
	private double scale, newWidth, newHeight, scaleCoefficient, w;
	private boolean tooSmall;

	private Blender blender;

	@Override
	public void onInit() {
		mixSource.init();
		blenders.add(new MaxBlender());
		blenders.add(new MinBlender());
		blenders.add(new BitwiseAndBlender());
		blenders.add(new BitwiseOrBlender());
		blenders.add(new AverageBlender());
		blenders.add(new HueBlender());
		blenders.add(new LumaBlender());
	}

	/**
	 * Hacky function to set the source to mix with
	 *
	 * @param s
	 *            {@link Source}
	 */
	public void setSource(Source s) {
		if (mixSource != null)
			mixSource.dispose();
		mixSource = s;
		mixSource.init();
	}

	@Override
	public void preParallel(PixelFrame img) {
		blender = blenders.getValue();

		baseW = img.getWidth();
		baseH = img.getHeight();

		if (mixFrame != null)
			mixFrame.dispose();

		PixelFrame frameFromSource = mixSource.getFrame();
		if (frameFromSource != null) {
			frameWidth = frameFromSource.getWidth();
			frameHeight = frameFromSource.getHeight();

			/*- if the source frame we are mixing in is different, then change size to reflect fit to base frame*/
			if (frameWidth != lastMixW || frameHeight != lastMixH) {
				newWidth = frameWidth;
				newHeight = frameHeight;

				tooSmall = newWidth < baseW && newHeight < baseH;

				if (frameWidth > baseW || tooSmall) {
					newWidth = baseW;
					newHeight = (newWidth * frameHeight) / frameWidth;
				}

				if (newHeight > baseH) {
					newHeight = baseH;
					newWidth = (newHeight * frameWidth) / frameHeight;
				}
				w = newWidth;

				scaleCoefficient = Math.max(0, Math.min(1.5, w / frameWidth));
				size.setValue(scaleCoefficient);

			}
			lastMixW = frameWidth;
			lastMixH = frameHeight;

			// Enforce limit on resize dimensions
			scale = size.getValue();
			scale = scale * frameWidth > MAX_WIDTH ? (double) (MAX_WIDTH) / frameWidth : scale;
			scale = scale * frameHeight > MAX_HEIGHT ? (double) (MAX_HEIGHT) / frameHeight : scale;
			mixFrame = frameFromSource.resize(scale);
		} else
			return;

		// update the mix frame dimensions variables after resizing
		mixW = mixFrame.getWidth();
		mixH = mixFrame.getHeight();

		transX = (int) (translateX.getValue() * baseW);
		dx = baseW / 2 - mixW / 2 + transX;

		transY = (int) (translateY.getValue() * baseH);
		dy = baseH / 2 - mixH / 2 + transY;
	}

	@Override
	public ParallelOperationTask getParallelTask() {
		return new SourceMixerTask();
	}

	class SourceMixerTask extends ParallelOperationTask {

		private int index, coord, x, y, baseColor, mixInColor;

		@Override
		public void executeParallel(PixelFrame img, PixelIndexList coords) {
			for (index = 0; index < coords.size(); index++) {
				coord = coords.get(index);
				x = (coord % baseW) - dx;
				y = ((coord - x) / baseW) - dy;

				if (x < mixW && y < mixH && x >= 0 && y >= 0) {
					baseColor = img.pixels[coord];
					mixInColor = mixFrame.get(x, y);
					img.pixels[coord] = blender.blend(mixInColor, baseColor);
				}
			}
		}

	}

}
