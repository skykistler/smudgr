package io.smudgr.engine.alg.select;

import io.smudgr.util.Frame;

/**
 * The {@link AllSelect} class simply returns {@code true} for all pixels.
 */
public class AllSelect extends Selector {

	@Override
	public String getElementName() {
		return "Select All";
	}

	@Override
	public boolean selectsPoint(Frame img, int x, int y) {
		return true;
	}

	@Override
	public void generate() {
		// do nothing to save performance time
	}
}
