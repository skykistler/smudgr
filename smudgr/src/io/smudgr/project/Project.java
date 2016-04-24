package io.smudgr.project;

import java.io.File;
import java.util.ArrayList;

import io.smudgr.app.Controller;
import io.smudgr.project.smudge.Smudge;
import io.smudgr.project.smudge.util.ComponentLibrary;
import io.smudgr.project.smudge.util.DisposedFrameProvider;
import io.smudgr.project.smudge.util.SourceLibrary;

public class Project {

	public static final String PROJECT_EXTENSION = ".smudge";

	private IdProvider idProvider;
	private ComponentLibrary componentLibrary;
	private SourceLibrary sourceLibrary;

	private Smudge smudge;

	private String location;
	private String outputPath;
	private int bpm = 120;

	public Project() {
		idProvider = new IdProvider();
		componentLibrary = new ComponentLibrary();
		sourceLibrary = new SourceLibrary();
	}

	public void init() {
		smudge.init();
	}

	public void update() {
		DisposedFrameProvider.getInstance().update();
		smudge.update();
	}

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

	public void add(ProjectElement element) {
		idProvider.add(element);
	}

	public void put(ProjectElement element, int id) {
		idProvider.put(element, id);
	}

	public void remove(ProjectElement element) {
		idProvider.remove(element);
	}

	public boolean contains(ProjectElement element) {
		return idProvider.getId(element) > -1;
	}

	public int getId(ProjectElement element) {
		return idProvider.getId(element);
	}

	public ProjectElement getElement(int id) {
		return idProvider.getElement(id);
	}

	public String getOutputPath() {
		return outputPath;
	}

	public String getProjectPath() {
		return location;
	}

	public void setProjectPath(String path) {
		File output = new File(path);

		path = output.getAbsolutePath();

		if (output.isDirectory()) {
			if (!path.endsWith(File.separator))
				path += File.separator;

			path += "Untitled" + PROJECT_EXTENSION;
		}

		location = path;
	}

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

	public Smudge getSmudge() {
		return smudge;
	}

	public void setSmudge(Smudge smudge) {
		this.smudge = smudge;
	}

	public ComponentLibrary getComponentLibrary() {
		return componentLibrary;
	}

	public SourceLibrary getSourceLibrary() {
		return sourceLibrary;
	}

	public int getBPM() {
		return bpm;
	}

	public void setBPM(int bpm) {
		this.bpm = bpm;
	}

}
