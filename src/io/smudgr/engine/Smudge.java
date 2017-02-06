package io.smudgr.engine;

import java.util.ArrayList;

import io.smudgr.app.project.ProjectItem;
import io.smudgr.app.project.reflect.ReflectableType;
import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.engine.param.BooleanParameter;
import io.smudgr.engine.param.Parametric;
import io.smudgr.util.Frame;

/**
 * A {@link Smudge} is a {@link Parametric} image manipulation. The application
 * instance attempts to render each {@link Smudge} at a constant
 * frame-rate.
 */
public abstract class Smudge extends Parametric implements ReflectableType, ProjectItem {

	@Override
	public String getTypeName() {
		return "Smudge";
	}

	@Override
	public String getTypeIdentifier() {
		return "smudge";
	}

	private BooleanParameter enabled = new BooleanParameter("Enable", this, true);

	private ArrayList<SmudgeComponent> components = new ArrayList<SmudgeComponent>();

	/**
	 * Initialize the smudge. This will be run when the {@link Smudge} is added
	 * to the {@link Rack}, and every time the project is started.
	 */
	public void init() {

	}

	/**
	 * Update the smudge, in time with the application update cycle.
	 */
	public void update() {

	}

	/**
	 * Render the smudge using the given frame, in time with the application
	 * render cycle.
	 *
	 * @param image
	 *            {@link Frame}
	 */
	public void render(Frame image) {
		if (enabled.getValue()) {
			apply(image);
		}
	}

	/**
	 * Add a {@link SmudgeComponent} to this {@link Smudge}.
	 *
	 * @param componentState
	 *            {@link PropertyMap} representing a {@link SmudgeComponent}
	 *
	 * @return {@link SmudgeComponent} or {@code null}
	 */
	public SmudgeComponent add(PropertyMap componentState) {
		String componentType = componentState.getAttribute("type");
		SmudgeComponent component = getProject().getComponentLibrary().getNewInstance(componentType);

		if (!component.getSmudgeIdentifier().equals(getIdentifier()))
			return null;

		if (componentState.hasAttribute("id"))
			getProject().put(component, Integer.parseInt(componentState.getAttribute("id")));
		else
			getProject().add(component);

		components.add(component);

		return component;
	}

	/**
	 * {@link Smudge} types implement this method to apply their image
	 * manipulations to any given {@link Frame}
	 *
	 * @param image
	 */
	protected abstract void apply(Frame image);

	/**
	 * Return all of the {@link SmudgeComponent} instances added to this
	 * {@link Smudge}
	 * 
	 * @return {@code ArrayList<SmudgeComponent>}
	 */
	public ArrayList<SmudgeComponent> getComponents() {
		return components;
	}

	@Override
	public void save(PropertyMap pm) {
		super.save(pm);

		pm.setAttribute("type", getIdentifier());

		for (SmudgeComponent component : components) {
			PropertyMap map = new PropertyMap(component.getTypeIdentifier());

			map.setAttribute("component", component.getComponentIdentifier());
			map.setAttribute("type", component.getIdentifier());

			component.save(map);

			pm.add(map);
		}
	}

	@Override
	public void load(PropertyMap pm) {
		super.load(pm);

		for (PropertyMap component : pm.getChildren(getProject().getComponentLibrary().getTypeIdentifier())) {
			add(component);
		}
	}

}
