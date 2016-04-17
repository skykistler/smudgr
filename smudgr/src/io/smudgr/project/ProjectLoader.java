package io.smudgr.project;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.smudgr.app.Controller;

public class ProjectLoader {

	private String path;

	public ProjectLoader(String path) {
		if (!path.endsWith(Project.PROJECT_EXTENSION))
			path += Project.PROJECT_EXTENSION;

		this.path = path;
	}

	public void load() {
		PropertyMap projectMap = loadXML();

		Controller controller = Controller.getInstance();

		if (controller == null)
			controller = new Controller();

		controller.pause();

		Project project = new Project();
		controller.setProject(project);

		project.load(projectMap);

		controller.start();
	}

	private PropertyMap loadXML() {
		PropertyMap project = new PropertyMap("project");

		File xml = new File(path);
		if (!xml.exists()) {
			System.out.println("Did not find file: " + xml.getAbsolutePath());
		} else
			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(xml);
				doc.getDocumentElement().normalize();

				Node projectNode = doc.getElementsByTagName("project").item(0);

				loadProperties(project, projectNode);

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Problem loading project at " + path);
			}

		return project;
	}

	private void loadProperties(PropertyMap parentMap, Node parentNode) {
		NodeList nodes = parentNode.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			Node childNode = nodes.item(i);
			if (!childNode.getParentNode().isSameNode(parentNode))
				continue;

			PropertyMap childMap = new PropertyMap(childNode.getLocalName());
			loadProperties(childMap, childNode);

			parentMap.add(childMap);
		}
	}
}
