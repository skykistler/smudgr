package io.smudgr.project;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
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

		controller.pause();

		Project project = new Project();
		project.setProjectPath(path);

		try {
			project.load(projectMap);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Could not load save file. Possibly corrupted");

			project = new Project();
			project.load(new PropertyMap("project"));
		}

		controller.start();
	}

	private PropertyMap loadXML() {
		PropertyMap project = new PropertyMap("project");

		File xml = new File(path);
		if (!xml.exists()) {
			System.out.println("Did not find file: " + xml.getAbsolutePath());
			System.out.println("Creating new project...");
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

		NamedNodeMap attributes = parentNode.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);

			parentMap.setAttribute(attribute.getNodeName(), attribute.getNodeValue());
		}

		NodeList nodes = parentNode.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			Node childNode = nodes.item(i);
			if (!childNode.getParentNode().isSameNode(parentNode) || childNode.getNodeName().startsWith("#"))
				continue;

			PropertyMap childMap = new PropertyMap(childNode.getNodeName());
			loadProperties(childMap, childNode);

			parentMap.add(childMap);
		}
	}
}
