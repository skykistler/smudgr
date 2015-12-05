package me.skykistler.smudgr.alg;

import me.skykistler.smudgr.controller.DeviceObserver;

public interface Parameter extends DeviceObserver {
	public void getValue();

	public void setMidiBind();
}
