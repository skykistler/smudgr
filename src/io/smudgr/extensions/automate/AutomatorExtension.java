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
	public String getElementName() {
		return "Automator";
	}

	@Override
	public String getElementIdentifier() {
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
	 * @param state
	 *            State information to pass to the new automator
	 * @return {@link AutomatorControl}
	 * @see AutomatorControl#getElementIdentifier()
	 */
	public AutomatorControl add(PropertyMap state) {
		AutomatorControl control = automatorLibrary.getNewInstance(state);

		if (control == null) {
			System.out.println("Could not find automator type: " + state.getAttribute(PropertyMap.ELEMENT_ATTR));
			return null;
		}

		control.load(state);
		automators.add(control);

		return control;
	}

	@Override
	public void save(PropertyMap pm) {
		for (AutomatorControl automator : automators) {
			PropertyMap map = new PropertyMap(automator);
			automator.save(map);

			pm.add(map);
		}
	}

	@Override
	public void load(PropertyMap pm) {
		automatorLibrary = new TypeLibrary<AutomatorControl>(AutomatorControl.class);

		for (PropertyMap map : pm.getChildren(automatorLibrary))
			add(map);
	}

}
