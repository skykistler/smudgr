package io.smudgr.project;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import io.smudgr.app.Controller;

public class ProjectSaver {

	private String path;

	private Document doc;

	public ProjectSaver() {
		path = Controller.getInstance().getProject().getProjectPath();

		if (!path.endsWith(Project.PROJECT_EXTENSION))
			path += Project.PROJECT_EXTENSION;
	}

	public void save() {
		Project project = Controller.getInstance().getProject();
		try {
			openXML();

			PropertyMap map = new PropertyMap("project");
			project.save(map);

			savePropertyMap(map, null);

			closeXML();

			System.out.println("Saved project to " + path);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("There was error saving the smudge.");
		}

	}

	private void openXML() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		doc = db.newDocument();
	}

	private void savePropertyMap(PropertyMap map, Element parent) {
		Element node = doc.createElement(map.getTag());

		Collection<String> attr = map.getAttributeKeys();
		for (String key : attr)
			node.setAttribute(key, map.getAttribute(key));

		Collection<String> children = map.getChildrenTags();
		for (String key : children) {
			for (PropertyMap child : map.getChildren(key)) {
				savePropertyMap(child, node);
			}
		}

		if (parent != null)
			parent.appendChild(node);
		else
			doc.appendChild(node);
	}

	private void closeXML() throws TransformerFactoryConfigurationError, FileNotFoundException, TransformerException {
		Transformer tr = TransformerFactory.newInstance().newTransformer();
		tr.setOutputProperty(OutputKeys.INDENT, "yes");
		tr.setOutputProperty(OutputKeys.METHOD, "xml");
		tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(path)));
	}

}
