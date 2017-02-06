package io.smudgr.extensions.automate;

import java.util.ArrayList;

import io.smudgr.api.ApiMessage;
import io.smudgr.app.controller.ControllerExtension;
import io.smudgr.app.project.reflect.TypeLibrary;
import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.engine.param.Parameter;
import io.smudgr.extensions.automate.controls.AutomatorControl;

/**
 * The {@link AutomatorExtension} provides functionality for automatically
 * controlling a {@link Parameter} using any {@link AutomatorControl}
 * strategies.
 */
public class AutomatorExtension implements ControllerExtension {

	@Override
	public String getName() {
		return "Automator";
	}

	@Override
	public String getIdentifier() {
		return "automator";
	}

	private TypeLibrary<AutomatorControl> automatorLibrary;
	private ArrayList<AutomatorControl> automators = new ArrayList<AutomatorControl>();

	@Override
	public void init() {
		for (AutomatorControl automator : automators)
			automator.init();
	}

	@Override
	public void update() {
		for (AutomatorControl automator : automators)
			automator.update();
	}

	@Override
	public void stop() {

	}

	@Override
	public void onMessage(ApiMessage message) {

	}

	/**
	 * Add an {@link AutomatorControl} to the extension.
	 *
	 * @param identifier
	 *            Fully-qualified type identifier of automator to add
	 * @param properties
	 *            State information to pass to the new automator
	 * @return {@link AutomatorControl}
	 * @see AutomatorControl#getIdentifier()
	 */
	public AutomatorControl add(String identifier, PropertyMap properties) {
		AutomatorControl control = automatorLibrary.getNewInstance(identifier);

		if (control == null) {
			System.out.println("Could not find automator with identifier: " + identifier);
			return null;
		}

		automators.add(control);

		// If the control was registered with the project, use the same ID
		if (properties.hasAttribute("id"))
			getProject().put(control, Integer.parseInt(properties.getAttribute("id")));
		else
			getProject().add(control);

		control.load(properties);

		return control;
	}

	@Override
	public void save(PropertyMap pm) {
		for (AutomatorControl automator : automators) {
			PropertyMap map = new PropertyMap(automator.getTypeIdentifier());

			map.setAttribute("id", getProject().getId(automator));
			map.setAttribute("type", automator.getIdentifier());

			automator.save(map);

			pm.add(map);
		}
	}

	@Override
	public void load(PropertyMap pm) {
		automatorLibrary = new TypeLibrary<AutomatorControl>(AutomatorControl.class);

		for (PropertyMap map : pm.getChildren(automatorLibrary.getTypeIdentifier())) {
			add(map.getAttribute("type"), map);
		}
	}

}
