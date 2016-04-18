package io.smudgr.test;

import io.smudgr.app.Controllable;
import io.smudgr.app.Controller;
import io.smudgr.app.view.NativeView;
import io.smudgr.extensions.automate.AutomatorExtension;
import io.smudgr.extensions.automate.controls.AutomatorControl;
import io.smudgr.extensions.midi.MidiExtension;
import io.smudgr.project.ProjectLoader;
import io.smudgr.project.PropertyMap;
import io.smudgr.project.smudge.Smudge;
import io.smudgr.project.smudge.alg.Algorithm;
import io.smudgr.project.smudge.alg.op.PixelSort;
import io.smudgr.project.smudge.alg.select.RangeSelect;
import io.smudgr.project.smudge.param.Parameter;

public class SkyTestMain {

	public static void main(String[] args) {
		appStart("data/test.smudge", "data", "Arturia BeatStepPro");

		// Comment this out if you're just trying to load a smudge
		buildSmudge();

		Controller.getInstance().getProject().getSourceLibrary().setLocation("data/venture/oceans");

		// Which monitor to display fullscreen, -1 means no fullscreen
		int monitor = -1;

		// Non-fullscreen window
		boolean debugMonitor = true;

		Controller.getInstance().add(new NativeView(monitor, debugMonitor));
	}

	public static void appStart(String projectPath, String outputDir, String device) {
		ProjectLoader loader = new ProjectLoader(projectPath);
		loader.load();
		Controller.getInstance().getProject().setOutputPath(outputDir);
		getMidi().bindDevice(device);
	}

	public static void buildSmudge() {
		// Pro-tip: In eclipse, you can Ctrl+Click on a class name to quickly open that class
		// There, you can see the names of available parameters
		Smudge smudge = Controller.getInstance().getProject().getSmudge();

		// Put algorithm/smudge building stuff here
		Algorithm alg = new Algorithm();

		// Example Selector
		RangeSelect range = new RangeSelect();
		alg.add(range);

		// Example operation
		PixelSort sort = new PixelSort();
		alg.add(sort);

		// Make sure to add any new algorithms to the smudge
		smudge.add(alg);

		// This is how you make an automated thingy, I've included a method writing this easier
		//		AutomatorControl automator1 = addAutomator("Animate", databend.getParameter("Target"));

		// Pause app while binding parameters
		Controller.getInstance().pause();

		// Bind all ur shit here, I've included a handy method for cleanliness
		waitForBind(smudge.getParameter("Downsample"));
		//		waitForBind(databend.getParameter("Amount"));
		//		waitForBind(automator1);
		waitForBind(range.getParameter("Range Length"));

		//		waitForBind(Controller.getInstance().getAppControl("Source Set Switcher"));
		//		waitForBind(Controller.getInstance().getAppControl("Source Switcher"));
		//		waitForBind(Controller.getInstance().getAppControl("Record GIF"));
		//		waitForBind(Controller.getInstance().getAppControl("Save Project"));

		// Continue app after binding parameters
		Controller.getInstance().start();
	}

	public static MidiExtension getMidi() {
		return (MidiExtension) Controller.getInstance().getExtension("MIDI");
	}

	public static void waitForBind(Controllable c) {
		if (c == null) {
			System.out.println("Error: Can not bind to null");
			return;
		}

		int control_id = Controller.getInstance().getProject().getId(c);

		if (control_id > -1)
			getMidi().waitForBind(control_id);
	}

	public static AutomatorExtension getAutomator() {
		return (AutomatorExtension) Controller.getInstance().getExtension("Automator");
	}

	public static AutomatorControl addAutomator(String type, Parameter param) {
		if (param == null)
			return null;

		PropertyMap properties = new PropertyMap("automator");
		properties.setAttribute("parameter", Controller.getInstance().getProject().getId(param));

		return getAutomator().add(type, properties);
	}
}
