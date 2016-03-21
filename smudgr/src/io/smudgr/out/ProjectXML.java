package io.smudgr.out;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

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

import io.smudgr.controller.BaseController;
import io.smudgr.controller.Controller;
import io.smudgr.controller.ControllerExtension;
import io.smudgr.controller.controls.Controllable;
import io.smudgr.midi.controller.MidiControlMap;
import io.smudgr.midi.controller.MidiController;
import io.smudgr.source.smudge.Smudge;
import io.smudgr.source.smudge.alg.Algorithm;
import io.smudgr.source.smudge.alg.AlgorithmComponent;
import io.smudgr.source.smudge.param.Parameter;
import io.smudgr.source.smudge.param.Parametric;

public class ProjectXML {
	public static final String extension = ".smudge";

	private String filepath;

	public ProjectXML(String filepath) {
		this.filepath = filepath;

		if (!filepath.endsWith(extension)) {
			filepath += extension;
		}
	}

	public Controller load() {
		File xml = new File(filepath);
		if (!xml.exists()) {
			System.out.println("Did not find file: " + xml.getAbsolutePath());
			return null;
		}

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xml);
			doc.getDocumentElement().normalize();

			BaseController controller = new BaseController();

			MidiControlMap midiMap = null;
			if (doc.getElementsByTagName("midi").getLength() > 0) {
				MidiController midiExtension = new MidiController();
				controller.add(midiExtension);
				midiMap = midiExtension.getMidiMap();
			}

			Smudge smudge = new Smudge();

			Element projectNode = doc.getDocumentElement();

			NodeList controls = projectNode.getElementsByTagName("control");
			for (int control = 0; control < controls.getLength(); control++) {
				Element controlNode = (Element) controls.item(control);

				String className = controlNode.getAttribute("class");
				int id = Integer.parseInt(controlNode.getAttribute("id"));

				Class<?> c = null;
				Controllable controllable = null;
				try {
					c = Class.forName(className);
					controllable = (Controllable) c.newInstance();

					if (controllable == null)
						throw new ClassNotFoundException();
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
					System.out.println("Unable to find component: " + className);
					continue;
				}

				NodeList properties = controlNode.getElementsByTagName("property");
				for (int prop = 0; prop < properties.getLength(); prop++) {
					Element propertyNode = (Element) properties.item(prop);
					String key = propertyNode.getAttribute("key");
					String value = propertyNode.getAttribute("value");

					controllable.getPropertyMap().setProperty(key, value);
				}

				loadMidi(controlNode, controllable, midiMap);

				controller.add(controllable, id);
			}

			Element smudgeNode = (Element) projectNode.getElementsByTagName("smudge").item(0);
			loadParameters(smudgeNode, smudge, midiMap);

			NodeList algorithms = smudgeNode.getElementsByTagName("algorithm");
			for (int i = 0; i < algorithms.getLength(); i++) {
				Algorithm alg = new Algorithm();

				Element algNode = (Element) algorithms.item(i);
				int id = Integer.parseInt(algNode.getAttribute("id"));

				loadParameters(algNode, alg, midiMap);

				NodeList components = algNode.getElementsByTagName("component");
				for (int comp = 0; comp < components.getLength(); comp++) {
					Element compNode = (Element) components.item(comp);

					int compID = Integer.parseInt(compNode.getAttribute("id"));

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

					loadParameters(compNode, component, midiMap);

					alg.add(component, compID);
				}

				smudge.add(alg, id);
			}

			controller.setSmudge(smudge);

			return controller;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Problem loading smudge at " + filepath);
			return null;
		}
	}

	private void loadParameters(Element node, Parametric parametric, MidiControlMap midiMap) {
		NodeList parameters = node.getElementsByTagName("parameter");
		for (int param = 0; param < parameters.getLength(); param++) {
			Element paramNode = (Element) parameters.item(param);
			if (paramNode.getParentNode() != node)
				continue;

			String name = paramNode.getAttribute("name").trim();
			String value = paramNode.getAttribute("value").trim();

			Parameter p = parametric.getParameter(name);
			if (name.length() > 0 && value.length() > 0)
				p.setInitial(value);

			loadMidi(paramNode, p, midiMap);
		}
	}

	private void loadMidi(Element node, Controllable c, MidiControlMap midiMap) {
		NodeList midiBinds = node.getElementsByTagName("midi");
		for (int bind = 0; bind < midiBinds.getLength(); bind++) {
			Element midiNode = (Element) midiBinds.item(bind);
			int channel = Integer.parseInt(midiNode.getAttribute("channel"));
			int key = Integer.parseInt(midiNode.getAttribute("key"));
			midiMap.assign(c, channel, key);
		}
	}

	public void save(Controller controller) {
		try {
			Smudge smudge = controller.getSmudge();

			MidiControlMap midiMap = null;
			for (ControllerExtension ext : controller.getExtensions())
				if (ext instanceof MidiController) {
					midiMap = ((MidiController) ext).getMidiMap();
					break;
				}

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();

			Element projectNode = doc.createElement("project");

			Element smudgeNode = doc.createElement("smudge");

			saveParameters(smudgeNode, smudge, midiMap, doc);

			for (Algorithm alg : smudge.getAlgorithms()) {
				Element algNode = doc.createElement("algorithm");
				algNode.setAttribute("id", alg.getID() + "");
				algNode.setAttribute("name", alg.getName());

				saveParameters(algNode, alg, midiMap, doc);

				for (AlgorithmComponent component : alg.getComponents()) {
					Element componentNode = doc.createElement("component");
					componentNode.setAttribute("id", component.getID() + "");
					componentNode.setAttribute("class", component.getClass().getCanonicalName());

					saveParameters(componentNode, component, midiMap, doc);

					algNode.appendChild(componentNode);
				}

				smudgeNode.appendChild(algNode);
			}

			projectNode.appendChild(smudgeNode);

			for (Controllable c : controller.getControls()) {
				if (c instanceof Parameter)
					continue;

				Element controlNode = doc.createElement("control");
				controlNode.setAttribute("class", c.getClass().getCanonicalName());
				controlNode.setAttribute("id", c.getID() + "");

				c.setProperties();

				HashMap<String, String> properties = c.getPropertyMap().getProperties();
				for (String key : properties.keySet()) {
					Element propertyNode = doc.createElement("property");
					propertyNode.setAttribute("key", key);
					propertyNode.setAttribute("value", properties.get(key));

					controlNode.appendChild(propertyNode);
				}

				saveMidi(controlNode, c, midiMap, doc);

				projectNode.appendChild(controlNode);
			}

			doc.appendChild(projectNode);

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

	private void saveParameters(Element node, Parametric parametric, MidiControlMap midiMap, Document doc) {
		for (Parameter param : parametric.getParameters()) {
			Element paramNode = doc.createElement("parameter");
			paramNode.setAttribute("name", param.getName());
			paramNode.setAttribute("value", param.getStringValue());

			node.appendChild(paramNode);

			saveMidi(paramNode, param, midiMap, doc);
		}
	}

	private void saveMidi(Element node, Controllable c, MidiControlMap midiMap, Document doc) {
		if (midiMap == null)
			return;

		int[] bind = midiMap.getKeyBind(c);
		if (bind != null) {
			Element midiNode = doc.createElement("midi");
			midiNode.setAttribute("channel", bind[0] + "");
			midiNode.setAttribute("key", bind[1] + "");

			node.appendChild(midiNode);
		}
	}
}
