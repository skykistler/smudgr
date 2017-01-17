package io.smudgr.extensions.automate;

import java.util.ArrayList;
import java.util.HashMap;

import io.smudgr.api.ApiMessage;
import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.extensions.ControllerExtension;
import io.smudgr.extensions.automate.controls.AutomatorControl;
import io.smudgr.util.Reflect;

public class AutomatorExtension implements ControllerExtension {

	public String getName() {
		return "Automator";
	}

	private HashMap<String, Class<?>> automatorTypes;
	private ArrayList<AutomatorControl> automators = new ArrayList<AutomatorControl>();

	public void init() {
		for (AutomatorControl automator : automators)
			automator.init();
	}

	public void update() {
		for (AutomatorControl automator : automators)
			automator.update();
	}

	public void stop() {

	}

	public void sendMessage(ApiMessage message) {

	}

	public AutomatorControl add(String type, PropertyMap properties) {
		AutomatorControl control = getNewAutomator(type);

		if (control == null) {
			System.out.println("Could not make automator of type: " + type);
			return null;
		}

		automators.add(control);
		getProject().add(control);

		control.load(properties);

		return control;
	}

	public void save(PropertyMap pm) {
		for (AutomatorControl automator : automators) {
			PropertyMap map = new PropertyMap(AutomatorControl.PROJECT_MAP_TAG);

			map.setAttribute("id", getProject().getId(automator));
			map.setAttribute("name", automator.getName());

			automator.save(map);

			pm.add(map);
		}
	}

	public void load(PropertyMap pm) {
		reflectAutomators();

		for (PropertyMap map : pm.getChildren(AutomatorControl.PROJECT_MAP_TAG)) {
			AutomatorControl control = getNewAutomator(map.getAttribute("name"));

			automators.add(control);
			getProject().put(control, Integer.parseInt(map.getAttribute("id")));

			control.load(map);
		}
	}

	private void reflectAutomators() {
		automatorTypes = new HashMap<String, Class<?>>();

		Reflect reflectControls = new Reflect(AutomatorControl.class);

		for (Class<?> c : reflectControls.get()) {
			try {
				AutomatorControl control = (AutomatorControl) c.newInstance();

				automatorTypes.put(control.getName(), control.getClass());
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	private AutomatorControl getNewAutomator(String name) {
		try {
			Class<?> type = automatorTypes.get(name);

			if (type == null)
				return null;

			return (AutomatorControl) type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}

}
