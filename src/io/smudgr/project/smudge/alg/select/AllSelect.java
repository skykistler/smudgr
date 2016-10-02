package io.smudgr.project.smudge.alg.select;

import io.smudgr.project.util.Frame;

public class AllSelect extends Selector {

	public String getName() {
		return "Select All";
	}

	public boolean selectsPoint(Frame img, int x, int y) {
		return true;
	}

	// Hacks to prevent new array allocation
	public void update() {
		return;
	}
}