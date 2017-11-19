package io.smudgr.engine.alg.select;

import io.smudgr.util.PixelFrame;

/**
 * The {@link AllSelect} class simply returns {@code true} for all pixels.
 */
public class AllSelect extends Selector {

	@Override
	public String getTypeName() {
		return "Select All";
	}

	@Override
	public boolean selectsPoint(PixelFrame img, int x, int y) {
		return true;
	}

	@Override
	public void generate() {
		// do nothing to save performance time
	}
}
