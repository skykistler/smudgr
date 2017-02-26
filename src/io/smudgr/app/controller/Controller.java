package io.smudgr.app.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiInvoker;
import io.smudgr.api.ApiMessage;
import io.smudgr.api.ApiServer;
import io.smudgr.app.project.Project;
import io.smudgr.app.project.reflect.TypeLibrary;
import io.smudgr.app.project.util.ProjectLoader;
import io.smudgr.app.project.util.ProjectSaver;
import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.app.threads.RenderThread;
import io.smudgr.app.threads.UpdateThread;
import io.smudgr.app.threads.ViewThread;
import io.smudgr.app.view.View;
import io.smudgr.util.output.FrameOutput;

/**
 * Root singleton for controlling the application instance.
 *
 * @see Project
 */
public class Controller {

	/**
	 * Ideal targeted frames-per-second for rendering.
	 * <p>
	 * Defaults to {@value}.
	 */
	public static final int TARGET_FPS = 60;

	/**
	 * Ideal count of update loops each beat.
	 * <p>
	 * Defaults to {@value}
	 *
	 * @see Project#getBPM()
	 */
	public static final int TICKS_PER_BEAT = 50;

	private static volatile Controller instance;

	/**
	 * Get the current app controller instance.
	 *
	 * @return The current {@link Controller} or {@code new} instance if one
	 *         doesn't exist.
	 */
	public static Controller getInstance() {
		if (instance == null)
			new Controller();

		return instance;
	}

	// Model
	private Project project;

	// API services
	private ApiInvoker apiInvoker;
	private ApiServer apiServer;

	// Threads
	private UpdateThread updater;
	private RenderThread renderer;
	private ArrayList<ViewThread> viewThreads = new ArrayList<ViewThread>();

	// States
	private volatile boolean started;
	private volatile boolean paused;

	// Views
	private ArrayList<View> views = new ArrayList<View>();

	// Type libraries
	private TypeLibrary<ControllerExtension> extensionLibrary;
	private TypeLibrary<AppControl> appControlLibrary;

	// Loaded instances
	private HashMap<String, AppControl> appControls;
	private HashMap<String, ControllerExtension> extensions;

	private FrameOutput frameOutput;

	private Controller() {
		instance = this;
		apiInvoker = new ApiInvoker();
	}

	/**
	 * Start or resume execution of the current application instance.
	 *
	 * @see Controller#pause()
	 * @see Controller#stop()
	 */
	public void start() {
		if (started) {
			paused = false;
			updater.setPaused(false);
			renderer.setPaused(false);
			return;
		}

		if (project == null) {
			System.out.println("Must load a project before starting controller");
			return;
		}

		System.out.println("Starting API...");
		apiServer = new ApiServer(ApiServer.API_PORT);
		apiInvoker.init();
		apiServer.setInvoker(apiInvoker);
		apiServer.start();

		System.out.println("Starting controller extensions...");

		for (ControllerExtension ext : getExtensions())
			ext.onInit();

		project.init();

		System.out.println("Starting processes...");
		started = true;

		updater = new UpdateThread();
		setUpdateSpeed();

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

	/**
	 * Run one update loop for the application.
	 *
	 * @see ControllerExtension#onUpdate()
	 * @see Project#update()
	 */
	public void update() {
		if (!started)
			return;

		setUpdateSpeed();

		for (ControllerExtension ext : getExtensions())
			ext.onUpdate();

		project.update();
	}

	private void setUpdateSpeed() {
		double beatsPerSecond = getProject().getBPM() / 60.0;
		double ticksPerSecond = Math.ceil(TICKS_PER_BEAT * beatsPerSecond);
		updater.setTarget((int) ticksPerSecond);
	}

	/**
	 * Pause execution of the current application instance.
	 *
	 * @see Controller#start()
	 */
	public void pause() {
		if (!started)
			return;

		paused = true;
		updater.setPaused(true);
		renderer.setPaused(true);

		System.gc();
	}

	/**
	 * Gets whether the current application instance is paused.
	 *
	 * @return {@code true} if the application is paused, {@code false} if
	 *         otherwise.
	 *
	 * @see Controller#pause()
	 * @see Controller#start()
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * Permanently stop execution of the current application instance. Stops
	 * threads and disposes of resources.
	 *
	 * @see Controller#pause()
	 */
	public void stop() {
		if (!started)
			return;

		System.out.println("Stopping controller extensions...");
		for (ControllerExtension ext : getExtensions())
			ext.onStop();

		System.out.println("Stopping API...");
		try {
			apiServer.stop();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		started = false;
		System.out.println("Stopping...");

		for (ViewThread viewer : viewThreads)
			viewer.stop();

		renderer.stop();
		updater.stop();

		project.disposeSources();

		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	/**
	 * Sends an {@link ApiMessage} to every client connected to the
	 * {@link ApiServer}. This also broadcasts the message to every
	 * {@link ControllerExtension}.
	 * <p>
	 * This is used to synchronize outgoing API messages between <i>all</i>
	 * extensions. Do not use this method if an {@link ApiMessage} should only
	 * be received by a single {@link ControllerExtension}.
	 *
	 * @param message
	 *            {@link ApiMessage} to send.
	 *
	 * @see Controller#getApiInvoker()
	 */
	public void broadcastMessage(ApiMessage message) {
		apiServer.sendMessage(message);

		for (ControllerExtension e : extensions.values())
			e.onMessage(message);
	}

	private void startView(View view) {
		ViewThread viewer = new ViewThread(view);
		viewer.start();

		viewThreads.add(viewer);
	}

	/**
	 * Save all loaded {@link ControllerExtension} states and {@link AppControl}
	 * states.
	 *
	 * @param pm
	 *            The property map to save to.
	 * @see ProjectSaver
	 */
	public void save(PropertyMap pm) {
		for (AppControl control : getAppControls()) {
			PropertyMap map = new PropertyMap(control);
			pm.add(map);
		}

		for (ControllerExtension extension : getExtensions()) {
			PropertyMap map = new PropertyMap(extensionLibrary.getTypeIdentifier());

			extension.save(map);
			map.setAttribute(PropertyMap.TYPE_ID_ATTR, extension.getTypeIdentifier());
			map.setAttribute(PropertyMap.NAME_ATTR, extension.getTypeName());

			pm.add(map);
		}
	}

	/**
	 * Load any {@link ControllerExtension} states and {@link AppControl} states
	 * from the given {@link PropertyMap}
	 *
	 * @param pm
	 *            The property map to load from.
	 * @see ProjectLoader
	 */
	public void load(PropertyMap pm) {
		loadAppControls(pm);
		loadExtensions(pm);
	}

	/**
	 * Gets the current loaded {@link Project} for the application instance.
	 *
	 * @return The current project.
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * Sets the current {@link Project} for the application instance.
	 *
	 * @param project
	 *            The project to use.
	 * @see Project#load(PropertyMap)
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	/**
	 * Gets the {@link ApiInvoker} in use by the application instance.
	 * The invoker is used to handle {@link ApiCommand} messages via
	 * {@link ApiInvoker#invoke(String)}
	 *
	 * @return {@link ApiInvoker}
	 *
	 * @see Controller#broadcastMessage(ApiMessage)
	 */
	public ApiInvoker getApiInvoker() {
		return apiInvoker;
	}

	/**
	 * Create an instance for every available AppControl and load any project
	 * states to them
	 */
	private void loadAppControls(PropertyMap pm) {
		// Manages available app control implementations
		appControlLibrary = new TypeLibrary<AppControl>(AppControl.class);

		// Manages an instance for every available app control
		appControls = new HashMap<String, AppControl>();

		// Create an instance for every available app control
		for (String id : appControlLibrary.getIdList()) {
			appControls.put(id, appControlLibrary.getNewInstance(id));
		}

		// Set project ID for saved controls
		for (PropertyMap mapping : pm.getChildren(appControlLibrary.getTypeIdentifier())) {
			AppControl control = getAppControl(mapping);
			control.load(mapping);
		}

		/**
		 * Add any project controls that weren't saved. Usually, when a new
		 * {@link AppControl} is added and this is an older save file.
		 */
		for (AppControl control : getAppControls()) {
			if (!getProject().contains(control))
				getProject().add(control);
		}
	}

	/**
	 * Create an instance of every available {@link ControllerExtension} and
	 * load and project states to them.
	 */
	private void loadExtensions(PropertyMap pm) {
		// Manages available extension types
		extensionLibrary = new TypeLibrary<ControllerExtension>(ControllerExtension.class);

		// Manages loaded extension instances
		extensions = new HashMap<String, ControllerExtension>();

		// Create an instance for every extension
		for (String id : extensionLibrary.getIdList()) {
			add(extensionLibrary.getNewInstance(id));
		}

		// Keep track of extensions loaded from the property map
		ArrayList<ControllerExtension> savedExtensions = new ArrayList<ControllerExtension>();

		// Load any states to the appropriate extension
		for (PropertyMap mapping : pm.getChildren(extensionLibrary.getTypeIdentifier())) {
			ControllerExtension ext = getExtension(mapping.getAttribute(PropertyMap.TYPE_ID_ATTR));

			if (ext != null) {
				ext.load(mapping);
				savedExtensions.add(ext);
			}
		}

		/**
		 * Dry load any extensions that weren't saved. Usually, when a user
		 * has a {@link ControllerExtension} installed that was not installed
		 * when the save file was created.
		 */
		for (ControllerExtension ext : getExtensions()) {
			if (!savedExtensions.contains(ext))
				ext.load(new PropertyMap(extensionLibrary.getTypeIdentifier()));
		}
	}

	/**
	 * Adds a {@link ControllerExtension} or {@link View} to the current
	 * application instance.
	 *
	 * @param o
	 *            Object to add to the current application instance.
	 */
	public void add(Object o) {
		if (o instanceof ControllerExtension)
			addExtension((ControllerExtension) o);
		else if (o instanceof View)
			addView((View) o);
	}

	private void addExtension(ControllerExtension ext) {
		if (!getExtensions().contains(ext)) {

			if (started)
				ext.onInit();

			extensions.put(ext.getTypeIdentifier(), ext);
		}
	}

	private void addView(View view) {
		if (!views.contains(view)) {

			if (started)
				startView(view);

			views.add(view);
		}
	}

	/**
	 * Get an {@link AppControl} by it's identifier.
	 *
	 * @param identifier
	 *            The identifier of the {@link AppControl}
	 * @return Loaded {@link AppControl} instance
	 *
	 * @see AppControl#getTypeIdentifier()
	 */
	public AppControl getAppControl(String identifier) {
		return appControls.get(identifier);
	}

	/**
	 * Get an {@link AppControl} by it's identifier.
	 *
	 * @param state
	 *            {@link PropertyMap} with type attribute
	 * @return Loaded {@link AppControl} instance
	 *
	 * @see AppControl#getTypeIdentifier()
	 */
	public AppControl getAppControl(PropertyMap state) {
		return appControls.get(state.getAttribute(PropertyMap.TYPE_ID_ATTR));
	}

	private Collection<AppControl> getAppControls() {
		return appControls.values();
	}

	/**
	 * Get a {@link ControllerExtension} instance by it's registered name.
	 *
	 * @param name
	 *            The registered name of the {@link ControllerExtension}
	 * @return Registered {@link ControllerExtension}
	 *
	 * @see ControllerExtension#getTypeName()
	 */
	public ControllerExtension getExtension(String name) {
		return extensions.get(name);
	}

	private Collection<ControllerExtension> getExtensions() {
		return extensions.values();
	}

	/**
	 * Convert a count of update loop cycles (ticks) to ideal real time
	 * milliseconds.
	 * <p>
	 * This conversion is based on the ideal {@link UpdateThread#getTarget()}
	 * update count, so the accuracy of this method may change if
	 * {@link Project#getBPM()} changes during tick counting.
	 * <p>
	 * However, even if the application is lagging, this estimate will continue
	 * to reflect the ideal milliseconds between updates. This is especially
	 * useful for getting the time between frames while recording.
	 *
	 * @param ticks
	 *            The count of updates.
	 * @return The ideal milliseconds that would pass during {@code ticks}
	 *         update loops.
	 */
	public int ticksToMs(int ticks) {
		return (int) (ticks / (updater.getTarget() / 1000d));
	}

	/**
	 * Gets the amount of frames rendered last second.
	 *
	 * @return amount of FPS last second
	 */
	public int getActualFPS() {
		return renderer.getActual();
	}

	/**
	 * Gets the target frames per second
	 *
	 * @return target FPS
	 */
	public int getTargetFPS() {
		return renderer.getTarget();
	}

	/**
	 * Starts output using a given {@link FrameOutput} class.
	 * <p>
	 * If the application {@link #isOutputting()}, this method does nothing.
	 * <p>
	 * Pauses the {@link UpdateThread} and delegates application updates to the
	 * {@link RenderThread} to ensure accurate in-time recording. Sets the
	 * target rendering FPS to the specified {@link FrameOutput#getTargetFPS()}.
	 *
	 * @param output
	 *            {@link FrameOutput} to use.
	 * @see Controller#stopOutput()
	 */
	public void startOutput(FrameOutput output) {
		if (isOutputting())
			return;

		frameOutput = output;
		frameOutput.open();

		updater.setPaused(true);
		renderer.setTarget(output.getTargetFPS());
		renderer.startOutput(frameOutput, updater.msToTicks(1000 / output.getTargetFPS()));
	}

	/**
	 * Stops any currently outputting process.
	 * <p>
	 * Resumes application updates using the {@link UpdateThread} and resets the
	 * target rendering FPS to {@link Controller#TARGET_FPS}
	 *
	 * @see Controller#startOutput(FrameOutput)
	 */
	public void stopOutput() {
		if (!isOutputting())
			return;

		frameOutput.close();
		renderer.stopOutput();
		renderer.setTarget(TARGET_FPS);
		updater.setPaused(false);

		frameOutput = null;
	}

	/**
	 * Gets whether the current application is recording to a
	 * {@link FrameOutput}
	 *
	 * @return {@code true} if currently recording, {@code false} if otherwise
	 * @see Controller#startOutput(FrameOutput)
	 * @see Controller#stopOutput()
	 */
	public boolean isOutputting() {
		return frameOutput != null;
	}
}
