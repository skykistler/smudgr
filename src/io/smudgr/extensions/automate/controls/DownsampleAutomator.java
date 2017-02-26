package io.smudgr.extensions.automate.controls;

import io.smudgr.app.controller.Controller;
import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.engine.Rack;
import io.smudgr.engine.param.Parameter;

/**
 * The {@link DownsampleAutomator} will downsample the current {@link Rack}
 * according to the current application FPS, to try an achieve the
 * {@link Controller#TARGET_FPS}. This helps smoothen the visual experience and
 * provides an interesting effect.
 *
 */
public class DownsampleAutomator implements AutomatorControl {

	@Override
	public String getTypeName() {
		return "Auto-Downsampler";
	}

	@Override
	public String getTypeIdentifier() {
		return "auto-downsampler";
	}

	private Parameter downsample;
	private boolean enabled = true;

	private double maxDownsample = .9;
	private double currentDownsample = 0;

	@Override
	public void inputValue(int value) {
		maxDownsample = 1 - value / 127.0;
	}

	@Override
	public void inputOn() {
		enabled = true;
	}

	@Override
	public void inputOff() {
		enabled = false;
	}

	@Override
	public void increment() {
		maxDownsample += .01;

		if (maxDownsample > 1)
			maxDownsample = 1;
	}

	@Override
	public void decrement() {
		maxDownsample -= .01;

		if (maxDownsample < 0)
			maxDownsample = 0;
	}

	@Override
	public void init() {
		downsample = getProject().getRack().getParameter("Downsample");
	}

	@Override
	public void update() {
		downsample = getProject().getRack().getParameter("Downsample");

		if (!enabled)
			return;

		if (Controller.getInstance().getActualFPS() < Controller.getInstance().getTargetFPS() / 2.0) {
			currentDownsample -= .01;
		} else
			currentDownsample += .01;

		if (currentDownsample < 0)
			currentDownsample = 0;

		if (currentDownsample > 1)
			currentDownsample = 1;

		if (currentDownsample < 1 - maxDownsample)
			currentDownsample = 1 - maxDownsample;

		downsample.setValue(currentDownsample);
	}

	@Override
	public Parameter getParameter() {
		return downsample;
	}

	@Override
	public void save(PropertyMap pm) {
		AutomatorControl.super.save(pm);

		pm.setAttribute("max", maxDownsample);
		pm.setAttribute("current", currentDownsample);
		pm.setAttribute("enabled", enabled ? "true" : "false");
	}

	@Override
	public void load(PropertyMap pm) {
		AutomatorControl.super.load(pm);

		if (pm.hasAttribute("max"))
			maxDownsample = Double.parseDouble(pm.getAttribute("max"));

		if (pm.hasAttribute("current"))
			currentDownsample = Double.parseDouble(pm.getAttribute("current"));

		if (pm.hasAttribute("enabled"))
			enabled = Boolean.parseBoolean("enabled");
	}

}
