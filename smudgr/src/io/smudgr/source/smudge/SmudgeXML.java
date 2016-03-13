package io.smudgr.source.smudge;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import io.smudgr.source.smudge.alg.Algorithm;
import io.smudgr.source.smudge.alg.AlgorithmComponent;
import io.smudgr.source.smudge.param.BooleanParameter;
import io.smudgr.source.smudge.param.Parameter;

public class SmudgeXML {
	public static final String extension = ".smudge";

	private String filepath;

	public SmudgeXML(String filepath) {
		this.filepath = filepath;

		if (!filepath.endsWith(extension)) {
			filepath += extension;
		}
	}

	public Smudge load() {
		File xml = new File(filepath);
		if (!xml.exists()) {
			System.out.println("Did not find file: " + xml.getAbsolutePath());
			return null;
		}

		try {
			Smudge smudge = new Smudge();

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xml);
			doc.getDocumentElement().normalize();

			Element root = doc.getDocumentElement();

			NodeList algorithms = root.getElementsByTagName("algorithm");
			for (int i = 0; i < algorithms.getLength(); i++) {
				Algorithm alg = new Algorithm();

				Element algNode = (Element) algorithms.item(i);
				int id = Integer.parseInt(algNode.getAttribute("id"));

				String enabled = algNode.getAttribute("enabled");
				if (enabled.length() > 0)
					alg.getParameter("Enable").setInitial(enabled);

				NodeList components = algNode.getElementsByTagName("component");
				for (int comp = 0; comp < components.getLength(); comp++) {
					Element compNode = (Element) components.item(comp);

					int compID = Integer.parseInt(algNode.getAttribute("id"));

					String className = compNode.getAttribute("class");
					if (className.length() == 0)
						continue;

					Class<?> c = null;
					AlgorithmComponent component = null;
					try {
						c = Class.forName(className);
						component = (AlgorithmComponent) c.newInstance();
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
						System.out.println("Unable to find component: " + className);
						continue;
					}

					NodeList parameters = compNode.getElementsByTagName("parameter");
					for (int param = 0; param < parameters.getLength(); param++) {
						Element paramNode = (Element) parameters.item(param);

						String name = paramNode.getAttribute("name").trim();
						String value = paramNode.getAttribute("value").trim();

						if (name.length() > 0 && value.length() > 0)
							component.getParameter(name).setInitial(value);
					}

					alg.add(component, compID);
				}

				smudge.add(alg, id);
			}

			return smudge;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Problem loading smudge at " + filepath);
			return null;
		}
	}

	public void save(Smudge smudge) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();

			Element root = doc.createElement("smudge");

			for (Algorithm alg : smudge.getAlgorithms()) {
				Element algNode = doc.createElement("algorithm");
				algNode.setAttribute("id", alg.getID() + "");
				algNode.setAttribute("name", alg.getName());

				boolean enabled = ((BooleanParameter) alg.getParameter("Enable")).getValue();
				algNode.setAttribute("enabled", enabled ? "true" : "false");

				for (AlgorithmComponent component : alg.getComponents()) {
					Element componentNode = doc.createElement("component");
					componentNode.setAttribute("id", component.getID() + "");
					componentNode.setAttribute("class", component.getClass().getCanonicalName());

					for (Parameter param : component.getParameters()) {
						Element paramNode = doc.createElement("parameter");
						paramNode.setAttribute("name", param.getName());
						paramNode.setAttribute("value", param.getStringValue());

						componentNode.appendChild(paramNode);
					}

					algNode.appendChild(componentNode);
				}

				root.appendChild(algNode);
			}

			doc.appendChild(root);

			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.setOutputProperty(OutputKeys.METHOD, "xml");
			tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(filepath)));

			System.out.println("Saved smudge to " + filepath);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("There was error saving the smudge.");
		}
	}
}
