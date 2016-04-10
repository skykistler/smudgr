package io.smudgr.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import io.smudgr.controller.controls.Controllable;
import io.smudgr.output.FrameOutput;
import io.smudgr.output.GifOutput;
import io.smudgr.smudge.Smudge;
import io.smudgr.view.NativeView;
import io.smudgr.view.View;

public class BaseController implements Controller {

	private static volatile BaseController instance;

	public static BaseController getInstance() {
		return instance;
	}

	public static final int TARGET_FPS = 60;
	public static final int TICKS_PER_BEAT = 50;

	private Smudge smudge;
	private ProjectIdManager idManager;

	private UpdateThread updater;
	private RenderThread renderer;
	private boolean started;
	private int beatsPerMinute = 120;

	private HashMap<String, ControllerExtension> extensions = new HashMap<String, ControllerExtension>();

	private ArrayList<Controllable> controls = new ArrayList<Controllable>();

	private ArrayList<View> views = new ArrayList<View>();
	private ArrayList<ViewThread> viewers = new ArrayList<ViewThread>();

	private FrameOutput frameOutput;

	public BaseController() {
		instance = this;

		idManager = new ProjectIdManager();
	}

	public void start() {
		if (started) {
			updater.setPaused(false);
			return;
		}

		if (smudge == null)
			setSmudge(new Smudge());

		if (views.size() == 0)
			add(new NativeView());

		smudge.init();

		for (Controllable c : controls)
			c.init();

		System.out.println("Starting controller extensions...");
		for (ControllerExtension ext : getExtensions())
			ext.init();

		System.out.println("Starting processes...");
		started = true;

		updater = new UpdateThread(this);
		renderer = new RenderThread(this);

		updater.start();
		renderer.start();

		System.out.println("Starting views...");
		for (View view : views)
			startView(view);
	}

	public void pause() {
		updater.setPaused(true);
	}

	public void update() {
		if (!started)
			return;

		for (Controllable c : controls)
			c.update();

		for (ControllerExtension ext : getExtensions())
			ext.update();

		smudge.update();
	}

	public void stop() {
		if (!started)
			return;

		System.out.println("Stopping controller extensions...");
		for (ControllerExtension ext : getExtensions())
			ext.stop();

		started = false;
		System.out.println("Stopping...");

		for (ViewThread viewer : viewers)
			viewer.stop();

		renderer.stop();
		updater.stop();

		if (smudge != null)
			smudge.dispose();

		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	public int ticksToMs(int ticks) {
		return (int) (ticks / (updater.getTicksPerSecond() / 1000));
	}

	public void startGifOutput(String filename) {
		if (frameOutput != null)
			return;

		frameOutput = new GifOutput("data/" + filename + "_" + System.currentTimeMillis() + ".gif");
		frameOutput.open();

		updater.setPaused(true);
		renderer.setTargetFPS(1000 / GifOutput.TARGET_GIF_MS);
		renderer.startOutput(frameOutput, updater.msToTicks(GifOutput.TARGET_GIF_MS));
	}

	public void stopGifOutput() {
		if (frameOutput == null || !(frameOutput instanceof GifOutput))
			return;

		frameOutput.close();
		renderer.stopOutput();
		renderer.setTargetFPS(TARGET_FPS);
		updater.setPaused(false);

		frameOutput = null;
	}

	public void add(Object o) {
		if (o instanceof Controllable)
			addControl((Controllable) o);
		else if (o instanceof ControllerExtension)
			addExtension((ControllerExtension) o);
		else if (o instanceof View)
			addView((View) o);
	}

	private void addControl(Controllable c) {
		if (!controls.contains(c)) {
			idManager.add(c);

			if (started)
				c.init();

			controls.add(c);
		}
	}

	private void addExtension(ControllerExtension ext) {
		if (!getExtensions().contains(ext)) {
			idManager.add(ext);

			if (started)
				ext.init();

			extensions.put(ext.getName(), ext);
		}
	}

	private void addView(View view) {
		if (!views.contains(view)) {
			idManager.add(view);

			if (started)
				startView(view);

			views.add(view);
		}
	}

	private void startView(View view) {
		view.start();

		ViewThread viewer = new ViewThread(view);
		viewer.start();

		viewers.add(viewer);
	}

	public ProjectIdManager getIdManager() {
		return idManager;
	}

	public Smudge getSmudge() {
		return smudge;
	}

	public void setSmudge(Smudge s) {
		smudge = s;
	}

	public void setBPM(int bpm) {
		beatsPerMinute = bpm;
	}

	public int getBPM() {
		return beatsPerMinute;
	}

	public ArrayList<Controllable> getControls() {
		return controls;
	}

	public ControllerExtension getExtension(String name) {
		return extensions.get(name);
	}

	public Collection<ControllerExtension> getExtensions() {
		return extensions.values();
	}

}
