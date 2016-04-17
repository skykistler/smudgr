package io.smudgr.test;

import io.smudgr.project.ProjectLoader;

public class SkyTestMain {

	public static void startSmudge(String projectPath) {
		ProjectLoader loader = new ProjectLoader(projectPath);
		loader.load();

	}

	public static void main(String[] args) {
		startSmudge("data/test.smudge");
	}
}
