package io.smudgr.app.project.util;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.smudgr.app.controller.Controller;
import io.smudgr.app.project.Project;

/**
 * The {@link ProjectLoader} class can load an XML file containing
 * {@link Project} data.
 * 
 * @see ProjectSaver
 */
public class ProjectLoader {

	private String path;

	/**
	 * Create a new {@link ProjectLoader} without a specified path. This can be
	 * used to bootstrap a new {@link Project} without immediately specifying a
	 * save location.
	 * 
	 * @see ProjectLoader#ProjectLoader(String)
	 */
	public ProjectLoader() {
		this(null);
	}

	/**
	 * Create a new {@link ProjectLoader} to load a project from an existing
	 * file.
	 * 
	 * @param path
	 *            Absolute or relative project file location.
	 * @see ProjectLoader#ProjectLoader()
	 */
	public ProjectLoader(String path) {
		if (path != null && !path.endsWith(Project.PROJECT_EXTENSION))
			path += Project.PROJECT_EXTENSION;

		this.path = path;
	}

	/**
	 * Loads the project.
	 * <p>
	 * The currently running application instance (if any) must be paused and is
	 * not resumed automatically.
	 */
	public void load() {
		PropertyMap projectMap = loadXML();

		Controller controller = Controller.getInstance();

		controller.pause();

		Project project = new Project();

		if (path != null)
			project.setProjectPath(path);

		try {
			project.load(projectMap);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Could not load save file. Possibly corrupted");

			project = new Project();
			project.load(new PropertyMap("project"));
		}
	}

	private PropertyMap loadXML() {
		PropertyMap project = new PropertyMap("project");

		if (path == null) {
			System.out.println("Creating new project...");
			return project;
		}

		File xml = new File(path);
		if (!xml.exists()) {
			System.out.println("Did not find file: " + xml.getAbsolutePath());
			System.out.println("Creating new project...");
			return project;
		}

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
