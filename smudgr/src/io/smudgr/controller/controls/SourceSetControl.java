package io.smudgr.controller.controls;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import io.smudgr.controller.SmudgeController;
import io.smudgr.source.SourceSet;

public class SourceSetControl extends Controllable {

	private int currentSet = -1;
	private ArrayList<String> files = new ArrayList<String>();;
	private ArrayList<SourceSet> sourceSets = new ArrayList<SourceSet>();;

	public SourceSetControl(SmudgeController c, String location) {
		super(c, "Source Set Switcher");

		File directory = new File("data/" + location);

		if (!directory.exists()) {
			System.out.println("File " + location + " does not exist!");
			return;
		}

		// If not a directory, just add the one file
		if (!directory.isDirectory())
			files.add(location);
		else {
			String[] list = directory.list();
			Arrays.sort(list);
			for (int i = 0; i < list.length; i++) {
				String path = location + "/" + list[i];

				files.add(path);
			}

			new SourceControl(c);
		}

		requestBind();
	}

	public void init() {
		System.out.println("Loading " + files.size() + " source files...");

		for (String path : files) {
			SourceSet set = new SourceSet(path);
			if (set.size() > 0)
				sourceSets.add(set);
		}

		setCurrentSet(0);
		System.out.println("Successfully loaded " + sourceSets.size() + " source sets");
	}

	public void increment() {
		setCurrentSet(currentSet + 1);
	}

	public void decrement() {
		setCurrentSet(currentSet - 1);
	}

	private SourceSet getCurrentSet() {
		if (sourceSets.size() == 0 || currentSet == -1)
			return null;

		return sourceSets.get(currentSet);
	}

	private void setCurrentSet(int i) {
		if (sourceSets.size() == 0) {
			currentSet = 0;
			return;
		}

		if (i == currentSet)
			return;

		SourceSet current = getCurrentSet();
		if (current != null)
			current.stop();

		currentSet = i;

		if (currentSet >= sourceSets.size())
			currentSet %= sourceSets.size();
		else if (currentSet < 0)
			currentSet += sourceSets.size();

		current = sourceSets.get(currentSet);
		current.init();

		getController().getSmudge().setSource(current);
	}

	public void inputValue(int value) {
	}

	public void inputOn(int value) {
	}

	public void inputOff(int value) {
	}

}
