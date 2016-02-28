package io.smudgr.source.smudge.alg.op;

import java.util.ArrayList;

import io.smudgr.source.Frame;
import io.smudgr.source.Source;
import io.smudgr.source.smudge.alg.math.ColorHelper;

public class SourceMixer extends Operation {

	private Frame mixFrame;
	private Source source;

	public SourceMixer(Source mySource) {
		this.source = mySource;
	}

	public void init() {
		source.init();
	}

	public void execute(Frame img) {

		/*
		 * Grab the frame that will be mixed into img
		*/
		mixFrame = source.getFrame();

		if (mixFrame == null)
			return;

		int w = mixFrame.getWidth();
		int h = mixFrame.getHeight();
		int width = img.getWidth();

		int x, y;
		for (ArrayList<Integer> coords : getAlgorithm().getCoordFunction().getCoordSet()) {
			for (Integer coord : coords) {
				x = coord % width;
				y = (coord - x) / width;
				if (inFrame(w, h, x, y))
					mix(coord, mixFrame, img, x, y);
			}
		}
	}

	private boolean inFrame(int mixW, int mixH, int x, int y) {
		return x < mixW && y < mixH;
	}

	private void mix(int coord, Frame mix, Frame img, int x, int y) {
		int color = mix.get(x, y);
		if (ColorHelper.alpha(color) == 255)
			img.pixels[coord] = color;
	}

	public String getName() {
		return "Source Mixer";
	}

}
