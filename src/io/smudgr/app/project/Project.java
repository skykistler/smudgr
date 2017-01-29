package io.smudgr.app.project;

import java.io.File;
import java.util.ArrayList;

import io.smudgr.app.controller.Controller;
import io.smudgr.app.controller.ControllerExtension;
import io.smudgr.app.project.util.IdProvider;
import io.smudgr.app.project.util.ProjectLoader;
import io.smudgr.app.project.util.ProjectSaver;
import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.engine.Smudge;
import io.smudgr.engine.param.ParameterObserverNotifier;
import io.smudgr.util.DisposedFrameProvider;

/**
 * The {@link Project} class tracks the state of all loaded {@link ProjectItem}
 * items and their relative hierarchy.
 * <p>
 * A {@link Project} includes {@link ControllerExtension} settings,
 * {@link SourceLibrary} locations, the default output path, and the structure
 * and composition of user-configured {@link Smudge} components.
 * <p>
 * Users are meant to load a single project at a time and treat a project as a
 * unified workspace. The composition of the {@link Project} is still subject to
 * change, until a definitive smudge building process is decided upon.
 * <p>
 * The {@link Project} uses a {@link PropertyMap} structure to record a
 * normalized model of itself. The {@link PropertyMap} is persisted to and
 * loaded from an XML file with a ".smudge" extension.
 * 
 * @see Smudge
 * @see ProjectLoader
 * @see ProjectSaver
 */
public class Project {

	/**
	 * The path extension used to identify project files.
	 * <p>
	 * Currently set to: {@value}
	 */
	public static final String PROJECT_EXTENSION = ".smudge";

	private IdProvider idProvider;
	private ComponentLibrary componentLibrary;
	private SourceLibrary sourceLibrary;
	private ParameterObserverNotifier paramObserverNotifier;

	private Smudge smudge;

	private String location;
	private String outputPath;
	private int bpm = 120;

	/**
	 * Instantiate a new, empty {@link Project}
	 */
	public Project() {
		idProvider = new IdProvider();
		componentLibrary = new ComponentLibrary();
		sourceLibrary = new SourceLibrary();
		paramObserverNotifier = new ParameterObserverNotifier();
	}

	/**
	 * Initialize the project which initializes the {@link Smudge}..
	 * 
	 * @see Smudge#init()
	 */
	public void init() {
		smudge.init();
	}

	/**
	 * Update the project, which updates the current {@link Smudge}
	 * 
	 * @see Smudge#update()
	 */
	public void update() {
		DisposedFrameProvider.getInstance().update();
		smudge.update();
	}

	/**
	 * Save the project to the given {@link PropertyMap}. Recursively saves the
	 * entire {@link ProjectItem} hierarchy.
	 * 
	 * @param pm
	 *            The property map to save to.
	 * 
	 * @see ProjectSaver
	 * @see Project#load(PropertyMap)
	 */
	public void save(PropertyMap pm) {
		if (outputPath != null)
			pm.setAttribute("outputPath", outputPath);

		pm.setAttribute("bpm", bpm);

		PropertyMap smudgeMap = new PropertyMap("smudge");
		smudge.save(smudgeMap);
		pm.add(smudgeMap);

		PropertyMap appMap = new PropertyMap("app");
		Controller.getInstance().save(appMap);
		pm.add(appMap);
	}

	/**
	 * Load the project from the given {@link PropertyMap}. Recursively loads
	 * the entire {@link ProjectItem} hierarchy.
	 * 
	 * @param pm
	 *            The property map to load from.
	 * 
	 * @see ProjectLoader
	 * @see Project#save(PropertyMap)
	 */
	public void load(PropertyMap pm) {
		if (Controller.getInstance().getProject() != this)
			Controller.getInstance().setProject(this);

		smudge = new Smudge();

		if (pm.hasAttribute("outputPath"))
			setOutputPath(pm.getAttribute("outputPath"));

		if (pm.hasAttribute("bpm"))
			setBPM(Integer.parseInt(pm.getAttribute("bpm")));

		// If our smudge tag existed, load with it; else make a new smudge entry
		ArrayList<PropertyMap> smudgeMap = pm.getChildren("smudge");
		if (smudgeMap.size() == 1)
			smudge.load(smudgeMap.get(0));
		else
			smudge.load(new PropertyMap("smudge"));

		// If our app tag existed, load with it; else make a new app entry
		ArrayList<PropertyMap> appMap = pm.getChildren("app");
		if (appMap.size() == 1)
			Controller.getInstance().load(appMap.get(0));
		else
			Controller.getInstance().load(new PropertyMap("app"));

		// If our sources tag existed, load with it; else make a new sources tag
		ArrayList<PropertyMap> sources = pm.getChildren("sources");
		if (sources.size() == 1)
			sourceLibrary.load(sources.get(0));
		else
			sourceLibrary.load(new PropertyMap("sources"));

		idProvider.finishLoading();
	}

	/**
	 * Set the path to save the project file to.
	 * 
	 * @param path
	 *            Project path.
	 * @see Project#setOutputPath(String)
	 */
	public void setProjectPath(String path) {
		File output = new File(path);

		path = output.getAbsolutePath();

		if (output.isDirectory()) {
			if (!path.endsWith(File.separator))
				path += File.separator;

			path += "Untitled";
		}

		if (!path.endsWith(PROJECT_EXTENSION))
			path += PROJECT_EXTENSION;

		location = path;
	}

	/**
	 * Set the location to output recorded files to.
	 * 
	 * @param path
	 *            Output path location.
	 * @see Project#setProjectPath(String)
	 */
	public void setOutputPath(String path) {
		if (path == null)
			return;

		File output = new File(path);
		if (!output.exists()) {
			outputPath = null;
			return;
		}

		if (!output.isDirectory())
			output = output.getParentFile();

		path = output.getAbsolutePath();
		if (!path.endsWith(File.separator)) {
			path += File.separator;
		}

		outputPath = path;
	}

	/**
	 * Add a {@link ProjectItem} to the {@link Project}. This method should be
	 * used for items that do not yet have an ID for this project.
	 * 
	 * @param item
	 *            {@link ProjectItem}
	 * 
	 * @see Project#put(ProjectItem, int)
	 * @see Project#remove(ProjectItem)
	 */
	public void add(ProjectItem item) {
		idProvider.add(item);
	}

	/**
	 * Put a {@link ProjectItem} at the given ID. This method should be used to
	 * register items that already have a project ID for this {@link Project}.
	 * 
	 * @param item
	 *            {@link ProjectItem}
	 * @param id
	 *            {@code int} Positive unique ID.
	 * 
	 * @see Project#add(ProjectItem)
	 */
	public void put(ProjectItem item, int id) {
		idProvider.put(item, id);
	}

	/**
	 * Remove a {@link ProjectItem} from the project.
	 * 
	 * @param item
	 *            {@link ProjectItem}
	 * 
	 * @see Project#add(ProjectItem)
	 */
	public void remove(ProjectItem item) {
		idProvider.remove(item);
	}

	/**
	 * Check whether the project contains a given {@link ProjectItem}
	 * 
	 * @param item
	 *            {@link ProjectItem}
	 * @return {@code true} if the project contains this item, {@code false} if
	 *         otherwise
	 */
	public boolean contains(ProjectItem item) {
		return idProvider.getId(item) > -1;
	}

	/**
	 * Get the ID of a given {@link ProjectItem}
	 * 
	 * @param item
	 *            {@link ProjectItem}
	 * @return {@code int} id
	 * 
	 * @see Project#getItem(int)
	 */
	public int getId(ProjectItem item) {
		return idProvider.getId(item);
	}

	/**
	 * Get the {@link ProjectItem} associated with a given id.
	 * 
	 * @param id
	 *            {@code int}
	 * @return {@link ProjectItem} or null
	 * 
	 * @see Project#getId(ProjectItem)
	 */
	public ProjectItem getItem(int id) {
		return idProvider.getItem(id);
	}

	/**
	 * Gets the current path for outputted files.
	 * 
	 * @return Output location for files.
	 */
	public String getOutputPath() {
		return outputPath;
	}

	/**
	 * Gets the current filename of the project.
	 * 
	 * @return The project filename.
	 */
	public String getProjectPath() {
		return location;
	}

	/**
	 * Gets the currently loaded {@link Smudge} persisted by this
	 * {@link Project}
	 * 
	 * @return {@link Smudge}
	 * @see Project#setSmudge(Smudge)
	 */
	public Smudge getSmudge() {
		return smudge;
	}

	/**
	 * Set the project {@link Smudge}
	 * 
	 * @param smudge
	 *            {@link Smudge}
	 * @see Project#getSmudge()
	 */
	public void setSmudge(Smudge smudge) {
		this.smudge = smudge;
	}

	/**
	 * Get the currently loaded component library in order to access all
	 * available component types.
	 * 
	 * @return {@link ComponentLibrary}
	 * @see Project#getSourceLibrary()
	 */
	public ComponentLibrary getComponentLibrary() {
		return componentLibrary;
	}

	/**
	 * Get the currently loaded source library in order to access loaded sources
	 * for this project.
	 * 
	 * @return {@link SourceLibrary}
	 * @see Project#getComponentLibrary()
	 */
	public SourceLibrary getSourceLibrary() {
		return sourceLibrary;
	}

	/**
	 * Get the {@link ParameterObserverNotifier} in order to attach a listener
	 * to parameter changes.
	 * 
	 * @return {@link ParameterObserverNotifier}
	 */
	public ParameterObserverNotifier getParameterObserverNotifier() {
		return paramObserverNotifier;
	}

	/**
	 * Get the current BPM of the {@link Project}
	 * 
	 * @return {@code int} typically between 60 and 230
	 */
	public int getBPM() {
		return bpm;
	}

	/**
	 * Set the BPM for the {@link Project}
	 * 
	 * @param bpm
	 *            integer typically between 60 and 230, although not enforced.
	 */
	public void setBPM(int bpm) {
		if (bpm < 1 || bpm > 400)
			return;

		this.bpm = bpm;
	}

}
