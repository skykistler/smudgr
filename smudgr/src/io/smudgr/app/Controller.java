package io.smudgr.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import io.smudgr.app.controls.AppControl;
import io.smudgr.app.output.FrameOutput;
import io.smudgr.app.threads.RenderThread;
import io.smudgr.app.threads.UpdateThread;
import io.smudgr.app.threads.ViewThread;
import io.smudgr.app.view.NativeView;
import io.smudgr.app.view.View;
import io.smudgr.extensions.ControllerExtension;
import io.smudgr.project.IdProvider;
import io.smudgr.project.Project;
import io.smudgr.project.PropertyMap;
import io.smudgr.reflect.Reflect;

public class Controller {

	public static final int TARGET_FPS = 60;
	public static final int TICKS_PER_BEAT = 50;

	private static volatile Controller instance;

	public static Controller getInstance() {
		return instance;
	}

	private Project project;

	private UpdateThread updater;
	private RenderThread renderer;
	private ArrayList<ViewThread> viewThreads = new ArrayList<ViewThread>();

	private boolean started;

	private ArrayList<View> views = new ArrayList<View>();

	private HashMap<String, AppControl> appControls;
	private HashMap<String, ControllerExtension> extensions;

	private FrameOutput frameOutput;

	public Controller() {
		instance = this;
	}

	public void start() {
		if (started) {
			updater.setPaused(false);
			return;
		}

		if (project == null)
			setProject(new Project());

		if (views.size() == 0)
			add(new NativeView());

		project.getSmudge().init();

		for (AppControl c : getAppControls())
			c.init();

		System.out.println("Starting controller extensions...");
		for (ControllerExtension ext : getExtensions())
			ext.init();

		System.out.println("Starting processes...");
		started = true;

		updater = new UpdateThread();
		renderer = new RenderThread();

		updater.start();
		renderer.start();

		// If viewers haven't been started already
		if (viewThreads.size() == 0) {
			System.out.println("Starting views...");
			for (View view : views)
				startView(view);
		}
	}

	public void update() {
		if (!started)
			return;

		for (Controllable c : getAppControls())
			c.update();

		for (ControllerExtension ext : getExtensions())
			ext.update();

		project.getSmudge().update();
	}

	public void pause() {
		if (!started)
			return;

		updater.setPaused(true);
		renderer.setPaused(true);
	}

	public void stop() {
		if (!started)
			return;

		System.out.println("Stopping controller extensions...");
		for (ControllerExtension ext : getExtensions())
			ext.stop();

		started = false;
		System.out.println("Stopping...");

		for (ViewThread viewer : viewThreads)
			viewer.stop();

		renderer.stop();
		updater.stop();

		project.getSmudge().dispose();

		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	public void add(Object o) {
		if (o instanceof ControllerExtension)
			addExtension((ControllerExtension) o);
		else if (o instanceof View)
			addView((View) o);
	}

	private void addExtension(ControllerExtension ext) {
		if (!getExtensions().contains(ext)) {

			if (started)
				ext.init();

			extensions.put(ext.getName(), ext);
		}
	}

	private void addView(View view) {
		if (!views.contains(view)) {

			if (started)
				startView(view);

			views.add(view);
		}
	}

	private void startView(View view) {
		view.start();

		ViewThread viewer = new ViewThread(view);
		viewer.start();

		viewThreads.add(viewer);
	}

	public int ticksToMs(int ticks) {
		return (int) (ticks / (updater.getTicksPerSecond() / 1000));
	}

	// TODO move this shit somewhere else
	public void startOutput(FrameOutput output) {
		if (frameOutput != null)
			return;

		frameOutput = output;
		frameOutput.open();

		updater.setPaused(true);
		renderer.setTargetFPS(output.getTargetFPS());
		renderer.startOutput(frameOutput, updater.msToTicks(1000 / output.getTargetFPS()));
	}

	public void stopOutput() {
		if (frameOutput == null)
			return;

		frameOutput.close();
		renderer.stopOutput();
		renderer.setTargetFPS(TARGET_FPS);
		updater.setPaused(false);

		frameOutput = null;
	}

	public void save(PropertyMap pm) {
		IdProvider idProvider = getProject().getIdProvider();

		for (AppControl control : getAppControls()) {
			PropertyMap map = new PropertyMap("control");

			control.save(map);
			map.setAttribute("id", idProvider.getId(control));
			map.setAttribute("name", control.getName());

			pm.add(map);
		}

		for (ControllerExtension extension : getExtensions()) {
			PropertyMap map = new PropertyMap("extension");

			extension.save(map);
			map.setAttribute("name", extension.getName());

			pm.add(map);
		}
	}

	public void load(PropertyMap pm) {
		reflectAppControls();

		// Set project ID for saved controls
		for (PropertyMap mapping : pm.getChildren("control")) {
			AppControl control = getAppControl(mapping.getAttribute("name"));

			if (control != null) {
				int id = Integer.parseInt(mapping.getAttribute("id"));

				getProject().getIdProvider().put(control, id);
			}
		}

		// Add any project controls that weren't saved
		for (AppControl control : getAppControls()) {
			if (getProject().getIdProvider().getId(control) == -1)
				getProject().add(control);
		}

		reflectExtensions();

		for (PropertyMap mapping : pm.getChildren("extension")) {
			ControllerExtension ext = getExtension(mapping.getAttribute("name"));

			if (ext != null)
				ext.load(mapping);
		}
	}

	private void reflectAppControls() {
		appControls = new HashMap<String, AppControl>();

		Reflect reflectControls = new Reflect(AppControl.class);

		for (Class<?> c : reflectControls.get()) {
			try {
				AppControl control = (AppControl) c.newInstance();
				appControls.put(c.getName(), control);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	private void reflectExtensions() {
		extensions = new HashMap<String, ControllerExtension>();

		Reflect reflectExt = new Reflect(ControllerExtension.class);

		for (Class<?> c : reflectExt.get()) {
			try {
				ControllerExtension ext = (ControllerExtension) c.newInstance();

				add(ext);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Project getProject() {
		return project;
	}

	public AppControl getAppControl(String name) {
		return appControls.get(name);
	}

	private Collection<AppControl> getAppControls() {
		return appControls.values();
	}

	public ControllerExtension getExtension(String name) {
		return extensions.get(name);
	}

	private Collection<ControllerExtension> getExtensions() {
		return extensions.values();
	}

}
