package io.smudgr.source.smudge.alg.select;

import io.smudgr.source.Frame;

public class AllSelect extends Selector {

	public String getName() {
		return "Select All";
	}

	public boolean selectsPoint(Frame img, int x, int y) {
		return true;
	}

}