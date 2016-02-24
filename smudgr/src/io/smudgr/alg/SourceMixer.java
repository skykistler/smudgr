package io.smudgr.alg;

import java.util.ArrayList;

import io.smudgr.alg.math.ColorHelper;
import io.smudgr.source.Frame;
import io.smudgr.source.Smudge;
import io.smudgr.source.Source;

public class SourceMixer extends Algorithm {

	private Frame mixFrame;
	private Source source;
	
	public SourceMixer(Smudge s, Source mySource) {
		super(s);
		this.source = mySource;
	}
	
	public void init() {
		//source.init();
	}
	
	public void execute(Frame img) {
		
		/*
		 * Grab the frame that will be mixed into img
		*/
		mixFrame = source.getFrame();
		
		if(mixFrame == null)
			return;
		
		int w = mixFrame.getWidth();
		int h = mixFrame.getHeight();
		int width = img.getWidth();
		
		int x, y;
		for (ArrayList<Integer> coords : getCoordFunction().getCoordSet()) {
			for(Integer coord : coords) {
				x = coord % width;
				y = (coord - x)/width;
				if(inFrame(w, h, x, y)) 
					mix(coord, mixFrame, img, x, y);
			}
		}
	}

	private boolean inFrame(int mixW, int mixH, int x, int y) {
		return x < mixW && y < mixH;
	}
	
	private void mix(int coord, Frame mix, Frame img, int x, int y) {
		int color = mix.get(x, y);
		if(ColorHelper.alpha(color) == 255)
			img.pixels[coord] = color;
	}
	
	public String getName() {
		return "Source Mixer";
	}

}
