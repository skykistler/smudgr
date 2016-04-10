package io.smudgr.app;

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

import io.smudgr.controller.Controller;
import io.smudgr.controller.controls.Controllable;
import io.smudgr.ext.ControllerExtension;
import io.smudgr.ext.midi.MidiControlMap;
import io.smudgr.ext.midi.MidiExtension;
import io.smudgr.smudge.Smudge;
import io.smudgr.smudge.alg.Algorithm;
import io.smudgr.smudge.alg.AlgorithmComponent;
import io.smudgr.smudge.param.Parameter;
import io.smudgr.smudge.param.Parametric;

public class ProjectXML {
	public static final String extension = ".smudge";

	private String filepath;

	public ProjectXML(String filepath) {
		this.filepath = filepath;

		if (!filepath.endsWith(extension)) {
			filepath += extension;
		}
	}

	public void load() {
		File xml = new File(filepath);
		if (!xml.exists()) {
			System.out.println("Did not find file: " + xml.getAbsolutePath());
			return;
		}

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xml);
			doc.getDocumentElement().normalize();

			Controller controller = Controller.getInstance();
			if (controller == null)
				controller = new Controller();
			else
				controller.stop();

			ProjectIdManager idManager = Controller.getInstance().getIdManager();

			MidiControlMap midiMap = null;
			if (doc.getElementsByTagName("midi").getLength() > 0) {
				MidiExtension midiExtension = (MidiExtension) controller.getExtension("MIDI Extension");

				if (midiExtension == null) {
					midiExtension = new MidiExtension();
					controller.add(midiExtension);
				}

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

				idManager.put(controllable, id);
				controller.add(controllable);
			}

			Element smudgeNode = (Element) projectNode.getElementsByTagName("smudge").item(0);
			loadParameters(smudgeNode, smudge, midiMap, idManager);

			NodeList algorithms = smudgeNode.getElementsByTagName("algorithm");
			for (int i = 0; i < algorithms.getLength(); i++) {
				Algorithm alg = new Algorithm();

				Element algNode = (Element) algorithms.item(i);
				int id = Integer.parseInt(algNode.getAttribute("id"));

				loadParameters(algNode, alg, midiMap, idManager);

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

					loadParameters(compNode, component, midiMap, idManager);

					idManager.put(component, compID);
					alg.add(component);
				}

				idManager.put(alg, id);
				smudge.add(alg);
			}

			for (Controllable c : controller.getControls())
				c.loadPropertyMap();

			controller.setSmudge(smudge);

			return;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Problem loading smudge at " + filepath);
			return;
		}
	}

	private void loadParameters(Element node, Parametric parametric, MidiControlMap midiMap, ProjectIdManager idManager) {
		NodeList parameters = node.getElementsByTagName("parameter");
		for (int param = 0; param < parameters.getLength(); param++) {
			Element paramNode = (Element) parameters.item(param);
			if (paramNode.getParentNode() != node)
				continue;

			String name = paramNode.getAttribute("name").trim();
			int id = Integer.parseInt(paramNode.getAttribute("id"));
			String value = paramNode.getAttribute("value").trim();

			Parameter p = parametric.getParameter(name);
			if (name.length() > 0 && value.length() > 0)
				p.setInitial(value);

			idManager.put(p, id);
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

	public void save() {
		try {
			ProjectIdManager idManager = Controller.getInstance().getIdManager();

			Smudge smudge = Controller.getInstance().getSmudge();

			MidiControlMap midiMap = null;
			for (ControllerExtension ext : Controller.getInstance().getExtensions())
				if (ext instanceof MidiExtension) {
					midiMap = ((MidiExtension) ext).getMidiMap();
					break;
				}

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();

			Element projectNode = doc.createElement("project");

			Element smudgeNode = doc.createElement("smudge");

			saveParameters(smudgeNode, smudge, midiMap, doc, idManager);

			for (Algorithm alg : smudge.getAlgorithms()) {
				Element algNode = doc.createElement("algorithm");
				algNode.setAttribute("id", idManager.getId(alg) + "");
				algNode.setAttribute("name", alg.getName());

				saveParameters(algNode, alg, midiMap, doc, idManager);

				for (AlgorithmComponent component : alg.getComponents()) {
					Element componentNode = doc.createElement("component");
					componentNode.setAttribute("id", idManager.getId(component) + "");
					componentNode.setAttribute("class", component.getClass().getCanonicalName());

					saveParameters(componentNode, component, midiMap, doc, idManager);

					algNode.appendChild(componentNode);
				}

				smudgeNode.appendChild(algNode);
			}

			projectNode.appendChild(smudgeNode);

			for (Controllable c : Controller.getInstance().getControls()) {
				if (c instanceof Parameter)
					continue;

				Element controlNode = doc.createElement("control");
				controlNode.setAttribute("class", c.getClass().getCanonicalName());
				controlNode.setAttribute("id", idManager.getId(c) + "");

				c.savePropertyMap();

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

	private void saveParameters(Element node, Parametric parametric, MidiControlMap midiMap, Document doc, ProjectIdManager idManager) {
		for (Parameter param : parametric.getParameters()) {
			Element paramNode = doc.createElement("parameter");
			paramNode.setAttribute("name", param.getName());
			paramNode.setAttribute("id", idManager.getId(param) + "");
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
