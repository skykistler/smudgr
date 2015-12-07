package me.skykistler.smudgr.alg;

import me.skykistler.smudgr.view.View;
import processing.core.PImage;

public interface Algorithm {
	public void execute(View processor, PImage img);
}
