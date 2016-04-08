package io.smudgr.controller;

import java.util.ArrayList;
import java.util.Random;

import io.smudgr.controller.controls.Controllable;
import io.smudgr.output.FrameOutput;
import io.smudgr.output.GifOutput;
import io.smudgr.source.smudge.Smudge;
import io.smudgr.view.View;

public class BaseController implements Controller {

	private static volatile BaseController instance;

	public static BaseController getInstance() {
		return instance;
	}

	public static final int TARGET_FPS = 60;
	public static final int TICKS_PER_BEAT = 50;

	private Smudge smudge;
	private View view;

	private UpdateThread updater;
	private RenderThread renderer;
	private ViewThread viewer;
	private boolean started;
	private int beatsPerMinute = 120;

	private ArrayList<Controllable> controls = new ArrayList<Controllable>();
	private ArrayList<Integer> control_ids = new ArrayList<Integer>(1000);
	private Random idPicker = new Random();

	private ArrayList<ControllerExtension> extensions = new ArrayList<ControllerExtension>();

	private FrameOutput frameOutput;

	public BaseController() {
		for (int i = 0; i < 1000; i++)
			control_ids.add(i);

		instance = this;
	}

	public void start() {
		if (started) {
			updater.setPaused(false);
			return;
		}

		if (smudge == null) {
			System.out.println("Smudge not set... can not start");
			return;
		}
		if (view == null) {
			System.out.println("View not set... can not start");
			return;
		}

		smudge.init();

		for (Controllable c : controls) {
			if (c.getPropertyMap().isSet())
				c.getProperties();
			c.init();
		}

		System.out.println("Starting controller extensions...");
		for (ControllerExtension ext : extensions)
			ext.init();

		view.start();
		view.setSource(smudge);

		updater = new UpdateThread(this);
		renderer = new RenderThread(this);
		viewer = new ViewThread(this);

		started = true;
		updater.start();
		renderer.start();
		viewer.start();
	}

	public void pause() {
		updater.setPaused(true);
	}

	public void update() {
		if (!started)
			return;

		for (Controllable c : controls)
			c.update();

		for (ControllerExtension ext : extensions)
			ext.update();

		smudge.update();
	}

	public void stop() {
		if (!started)
			return;

		System.out.println("Stopping controller extensions...");
		for (ControllerExtension ext : extensions)
			ext.stop();

		started = false;
		System.out.println("Stopping...");
		updater.stop();
		renderer.stop();
		viewer.stop();

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
		return (int) (ticks * (updater.getTicksPerSecond() / 1000));
	}

	public int msToTicks(int ms) {
		return (int) (ms * (1000 / updater.getTicksPerSecond()));
	}

	public void startGifOutput(String filename) {
		if (frameOutput != null)
			return;

		frameOutput = new GifOutput("data/" + filename + "_" + System.currentTimeMillis() + ".gif");
		frameOutput.open();

		updater.setPaused(true);
		renderer.startOutput(frameOutput, msToTicks(GifOutput.TARGET_GIF_MS));
	}

	public void stopGifOutput() {
		if (frameOutput == null || !(frameOutput instanceof GifOutput))
			return;

		frameOutput.close();
		updater.setPaused(false);
	}

	public Smudge getSmudge() {
		return smudge;
	}

	public void setSmudge(Smudge s) {
		smudge = s;

		if (smudge.getController() != this)
			s.setController(this);
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
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

	public void add(Object o) {
		if (o instanceof Controllable) {
			addControl((Controllable) o);
		} else if (o instanceof ControllerExtension) {
			addExtension((ControllerExtension) o);
		}
	}

	public void add(Object o, int id) {
		if (o instanceof Controllable)
			addControl((Controllable) o, id);
		else if (o instanceof ControllerExtension)
			addExtension((ControllerExtension) o);
	}

	private void addControl(Controllable c) {
		addControl(c, getNewControlID());
	}

	private void addControl(Controllable c, int id) {
		if (!controls.contains(c)) {
			c.setID(id);
			pluckID(id);

			c.setController(this);

			if (started)
				c.init();

			controls.add(c);
		}
	}

	private void addExtension(ControllerExtension ext) {
		if (!extensions.contains(ext)) {
			extensions.add(ext);

			ext.setParent(this);

			if (started)
				ext.init();
		}
	}

	public ArrayList<ControllerExtension> getExtensions() {
		return extensions;
	}

	public int getNewControlID() {
		int index = idPicker.nextInt(control_ids.size());
		int id = control_ids.get(index);

		return id;
	}

	private void pluckID(int id) {
		for (int i = 0; i < control_ids.size(); i++) {
			if (control_ids.get(i) == id) {
				control_ids.remove(i);
				return;
			}
		}
	}

}
