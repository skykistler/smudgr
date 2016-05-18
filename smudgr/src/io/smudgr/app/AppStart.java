package io.smudgr.app;

import java.io.File;
import java.util.ArrayList;

import io.smudgr.app.view.FullscreenView;
import io.smudgr.app.view.MonitorView;
import io.smudgr.extensions.automate.AutomatorExtension;
import io.smudgr.extensions.automate.controls.AutomatorControl;
import io.smudgr.extensions.midi.MidiExtension;
import io.smudgr.project.ProjectLoader;
import io.smudgr.project.PropertyMap;
import io.smudgr.project.smudge.param.Parameter;

public abstract class AppStart {

	private boolean newSmudge;
	private ArrayList<MidiBinding> toBind = new ArrayList<MidiBinding>();

	public AppStart(String projectPath, String sourceLocation, String outputDir, String device, boolean overwriteSmudge) {
		File project = new File(projectPath);
		if (project.exists()) {
			if (overwriteSmudge) {
				project.delete();
				newSmudge = true;
			}
		} else
			newSmudge = true;

		ProjectLoader loader = new ProjectLoader(projectPath);
		loader.load();

		Controller.getInstance().getProject().setOutputPath(outputDir);
		Controller.getInstance().getProject().getSourceLibrary().setLocation(sourceLocation);

		getMidi().bindDevice(device);
	}

	public void start() {
		if (newSmudge)
			buildSmudge();

		for (MidiBinding c : toBind)
			waitForBind(c);

		// Continue app after binding parameters
		Controller.getInstance().start();
	}

	public abstract void buildSmudge();

	public void bind(Controllable c) {
		bind(c, false, -1);
	}

	public void bind(Controllable c, boolean absolute, int ignoreKey) {
		toBind.add(new MidiBinding(c, absolute, ignoreKey));
	}

	public AutomatorControl addAutomator(String type, Parameter param) {
		if (param == null)
			return null;

		PropertyMap properties = new PropertyMap("automator");
		properties.setAttribute("parameter", Controller.getInstance().getProject().getId(param));

		return getAutomator().add(type, properties);
	}

	public void fullscreenView(int display) {
		if (display > -1)
			Controller.getInstance().add(new FullscreenView(display));
	}

	public void monitorView() {
		Controller.getInstance().add(new MonitorView());
	}

	private void waitForBind(MidiBinding binding) {
		if (binding.control == null) {
			System.out.println("Error: Can not bind to null");
			return;
		}

		int control_id = Controller.getInstance().getProject().getId(binding.control);

		if (control_id > -1)
			getMidi().waitForBind(control_id, binding.absolute, binding.ignoreKey);
	}

	private AutomatorExtension getAutomator() {
		return (AutomatorExtension) Controller.getInstance().getExtension("Automator");
	}

	private MidiExtension getMidi() {
		return (MidiExtension) Controller.getInstance().getExtension("MIDI");
	}

	// TODO: write a better way of handling binding metadata
	private class MidiBinding {
		private Controllable control;
		private boolean absolute;
		private int ignoreKey;

		private MidiBinding(Controllable c, boolean a, int i) {
			control = c;
			absolute = a;
			ignoreKey = i;
		}
	}

	public static boolean isLinux() {
		return System.getProperty("os.name").equals("Linux");
	}
}
