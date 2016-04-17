package io.smudgr.project;

import java.io.File;

import io.smudgr.app.Controller;
import io.smudgr.project.smudge.ComponentLibrary;
import io.smudgr.project.smudge.Smudge;

public class Project {

	public static final String PROJECT_EXTENSION = ".smudge";

	private IdProvider idProvider;
	private Smudge smudge;
	private ComponentLibrary componentLibrary;

	private String location;
	private String outputPath;
	private int bpm;

	public Project() {
		idProvider = new IdProvider();
		componentLibrary = new ComponentLibrary();
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
		setOutputPath(pm.getAttribute("outputPath"));
		setBPM(Integer.parseInt(pm.getAttribute("bpm")));

		smudge.load(pm.getChildren("smudge").get(0));

		Controller.getInstance().load(pm.getChildren("app").get(0));

		idProvider.finishLoading();
	}

	public void add(ProjectElement element) {
		idProvider.add(element);
	}

	public String getOutputPath() {
		return outputPath;
	}

	public String getProjectPath() {
		return location;
	}

	public void setProjectPath(String path) {
		location = path;
	}

	public void setOutputPath(String path) {
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

	public IdProvider getIdProvider() {
		return idProvider;
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

	public int getBPM() {
		return bpm;
	}

	public void setBPM(int bpm) {
		this.bpm = bpm;
	}

}
