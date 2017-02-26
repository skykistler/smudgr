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

	private double reactivity = .8;
	private double currentDownsample = 1;

	@Override
	public void inputValue(int value) {
		reactivity = value / 127.0;
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
		reactivity += .01;

		if (reactivity > 1)
			reactivity = 1;
	}

	@Override
	public void decrement() {
		reactivity -= .01;

		if (reactivity < 0)
			reactivity = 0;
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

		if (Controller.getInstance().getActualFPS() < Controller.getInstance().getTargetFPS() / (1.5 + 2.5 * (1 - reactivity))) {
			currentDownsample *= .9999 - (reactivity * .02);
		} else
			currentDownsample *= 1.02;

		if (currentDownsample < 0)
			currentDownsample = 0;

		if (currentDownsample > 1)
			currentDownsample = 1;

		downsample.setValue(currentDownsample);
	}

	@Override
	public Parameter getParameter() {
		return downsample;
	}

	@Override
	public void save(PropertyMap pm) {
		AutomatorControl.super.save(pm);

		pm.setAttribute("reactivity", reactivity);
		pm.setAttribute("current", currentDownsample);
		pm.setAttribute("enabled", enabled ? "true" : "false");
	}

	@Override
	public void load(PropertyMap pm) {
		AutomatorControl.super.load(pm);

		if (pm.hasAttribute("reactivity"))
			reactivity = Double.parseDouble(pm.getAttribute("reactivity"));

		if (pm.hasAttribute("current"))
			currentDownsample = Double.parseDouble(pm.getAttribute("current"));

		if (pm.hasAttribute("enabled"))
			enabled = Boolean.parseBoolean("enabled");
	}

}
