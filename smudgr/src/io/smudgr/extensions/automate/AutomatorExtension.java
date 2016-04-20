package io.smudgr.extensions.automate;

import java.util.ArrayList;
import java.util.HashMap;

import io.smudgr.extensions.ControllerExtension;
import io.smudgr.extensions.automate.controls.AutomatorControl;
import io.smudgr.project.PropertyMap;
import io.smudgr.reflect.Reflect;

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

	public AutomatorControl add(String type, PropertyMap properties) {
		AutomatorControl control = getNewAutomator(type);

		if (control == null) {
			System.out.println("Could not make automator of type: " + type);
			return null;
		}

		control.load(properties);
		automators.add(control);

		getProject().add(control);

		return control;
	}

	public void save(PropertyMap pm) {
		for (AutomatorControl automator : automators) {
			PropertyMap map = new PropertyMap("automator");

			map.setAttribute("id", getProject().getId(automator));
			map.setAttribute("name", automator.getName());

			automator.save(map);

			pm.add(map);
		}
	}

	public void load(PropertyMap pm) {
		reflectAutomators();

		for (PropertyMap map : pm.getChildren("automator")) {
			AutomatorControl control = getNewAutomator(map.getAttribute("name"));

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
