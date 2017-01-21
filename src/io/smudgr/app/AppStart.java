package io.smudgr.app;

import java.io.File;
import java.util.ArrayList;

import io.smudgr.app.controller.AppControl;
import io.smudgr.app.controller.Controllable;
import io.smudgr.app.controller.Controller;
import io.smudgr.app.project.util.ProjectLoader;
import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.app.view.FullscreenView;
import io.smudgr.app.view.MonitorView;
import io.smudgr.engine.param.Parameter;
import io.smudgr.extensions.automate.AutomatorExtension;
import io.smudgr.extensions.automate.controls.AutomatorControl;
import io.smudgr.extensions.midi.MidiExtension;

/**
 * The abstract {@link AppStart} class defines generic behavior for
 * bootstrapping an application instance, mostly for testing purposes. Basically
 * this class is total garbage and is gonna be totally trashed by the time we go
 * live.
 */
public abstract class AppStart {

	private boolean newSmudge;
	private ArrayList<MidiBinding> toBind = new ArrayList<MidiBinding>();

	/**
	 *
	 * @param projectPath
	 *            project to load
	 * @param sourceLocation
	 *            sources to load
	 * @param outputDir
	 *            where to record
	 * @param device
	 *            MIDI controller to bind to
	 * @param overwriteSmudge
	 *            Whether to delete the existing project file on application
	 *            start
	 * @param deviceServer
	 *            Whether to start a MIDI device server
	 */
	public AppStart(String projectPath, String sourceLocation, String outputDir, String device, boolean overwriteSmudge,
			boolean deviceServer) {
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

		getMidi().bindDevice(device, deviceServer);
	}

	/**
	 * Start the application, bind to controller
	 */
	public void start() {
		if (newSmudge)
			buildSmudge();

		for (MidiBinding c : toBind)
			waitForBind(c);

		// Continue app after binding parameters
		Controller.getInstance().start();
	}

	/**
	 * Implementations of this method should create a new smudge object and add
	 * algorithms and configure parameters
	 */
	public abstract void buildSmudge();

	/**
	 * Register a control to bind to a MIDI input on start
	 *
	 * @param c
	 *            {@link Controllable} could be a {@link Parameter} or
	 *            {@link AppControl}
	 */
	public void bind(Controllable c) {
		bind(c, false, -1);
	}

	/**
	 * Register a control to bind to a MIDI input
	 *
	 * @param c
	 *            {@link Controllable}
	 * @param absolute
	 *            Should a knob input be absolute (0-127) or relative (knob up
	 *            increments, knob down decrements)
	 * @param ignoreKey
	 *            Optional key to ignore (not bind if accidentally pressed)
	 */
	public void bind(Controllable c, boolean absolute, int ignoreKey) {
		toBind.add(new MidiBinding(c, absolute, ignoreKey));
	}

	/**
	 * Add an automator to the project of given type for given {@link Parameter}
	 *
	 * @param type
	 *            Automator type
	 * @param param
	 *            {@link Parameter}
	 * @return {@link AutomatorControl}
	 */
	public AutomatorControl addAutomator(String type, Parameter param) {
		PropertyMap properties = new PropertyMap("automator");

		if (param != null)
			properties.setAttribute("parameter", Controller.getInstance().getProject().getId(param));

		return getAutomator().add(type, properties);
	}

	/**
	 * Create a fullscreen view on the given display
	 *
	 * @param display
	 *            Monitor to use
	 */
	public void fullscreenView(int display) {
		if (display > -1)
			Controller.getInstance().add(new FullscreenView(display));
	}

	/**
	 * Create a monitor (non-fullscreen) view
	 */
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

	protected MidiExtension getMidi() {
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

	/**
	 * @return Is current OS windows
	 */
	public static boolean isWindows() {
		return System.getProperty("os.name").startsWith("Windows");
	}

	/**
	 * @return Is current OS Mac
	 */
	public static boolean isMac() {
		return System.getProperty("os.name").startsWith("Mac");
	}

	/**
	 * @return Is current OS Linux
	 */
	public static boolean isLinux() {
		return System.getProperty("os.name").equals("Linux");
	}
}
