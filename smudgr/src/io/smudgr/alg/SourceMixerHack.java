package io.smudgr.alg;

import java.awt.Graphics2D;
import java.util.ArrayList;

import io.smudgr.Smudge;
import io.smudgr.alg.math.ColorHelper;
import io.smudgr.alg.param.NumberParameter;
import io.smudgr.source.Frame;
import io.smudgr.source.Source;

public class SourceMixerHack extends Algorithm {

	private Frame mixFrame;
	private Source source;
	
	public SourceMixerHack(Smudge s, Source mySource) {
		super(s);
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
		
		if(mixFrame == null)
			return;
		
		int w = mixFrame.getWidth();
		int h = mixFrame.getHeight();
		int width = img.getWidth();
		int height = img.getHeight();
		
		Graphics2D g = (Graphics2D) img.getFrame().getBufferedImage().getGraphics();
		g.drawImage(mixFrame.getBufferedImage(), 0, 0, width, height, null);
		g.dispose();
		
	}
	
	public String getName() {
		return "Source Mixer";
	}

}
