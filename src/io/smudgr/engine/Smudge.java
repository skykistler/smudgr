package io.smudgr.engine;

import java.util.ArrayList;

import io.smudgr.app.project.Project;
import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.engine.param.BooleanParameter;
import io.smudgr.engine.param.Parametric;
import io.smudgr.util.Frame;

/**
 * A {@link Smudge} is a {@link Parametric} image manipulation. The application
 * instance attempts to render each {@link Smudge} at a constant
 * frame-rate.
 */
public abstract class Smudge extends Parametric {

	@Override
	public String getTypeCategoryName() {
		return "Smudge";
	}

	@Override
	public String getTypeCategoryIdentifier() {
		return "smudge";
	}

	private BooleanParameter enabled = new BooleanParameter("Enable", this, true);

	private ArrayList<SmudgeComponent> components = new ArrayList<SmudgeComponent>();

	/**
	 * Initialize the smudge. This will be run when the {@link Smudge} is added
	 * to the {@link Rack}, and every time the project is started.
	 */
	public abstract void onInit();

	/**
	 * {@link Smudge} types implement this method to apply their image
	 * manipulations using any given {@link Frame}
	 *
	 * @param image
	 *            {@link Frame}
	 * @return resulting frame
	 */
	public abstract Frame smudge(Frame image);

	/**
	 * Called when a {@link SmudgeComponent} has been added to this
	 * {@link Smudge}
	 *
	 * @param component
	 */
	protected abstract void onAdd(SmudgeComponent component);

	/**
	 * Add a {@link SmudgeComponent} to this {@link Smudge} using a
	 * {@link PropertyMap} state.
	 * <p>
	 * If the given {@link PropertyMap} isn't a type that belongs to this
	 * {@link Smudge}, this method will return {@code null}.
	 *
	 * @param state
	 *            {@link PropertyMap} representing a {@link SmudgeComponent}
	 *
	 * @return {@link SmudgeComponent} or {@code null}
	 */
	public SmudgeComponent add(PropertyMap state) {
		SmudgeComponent component = getProject().getComponentLibrary().getNewInstance(this, state);

		if (component == null)
			return null;

		component.setParent(this);
		component.load(state);

		component.onInit();

		components.add(component);
		onAdd(component);

		return component;
	}

	/**
	 * Adds an instantiated {@link SmudgeComponent} to this {@link Smudge} and
	 * registers it with the whole {@link Project}
	 *
	 * @param component
	 *            {@link SmudgeComponent}
	 */
	public void add(SmudgeComponent component) {
		if (this.getTypeIdentifier() != component.getSmudgeTypeIdentifier())
			return;

		component.setParent(this);
		getProject().add(component);

		component.onInit();

		components.add(component);
		onAdd(component);
	}

	/**
	 * Gets all {@link SmudgeComponent}s added to this
	 * {@link Smudge}
	 *
	 * @return {@code ArrayList<SmudgeComponent>}
	 */
	public ArrayList<SmudgeComponent> getComponents() {
		return components;
	}

	/**
	 * Gets whether this {@link Smudge} is currently enabled. If false,
	 * {@link Smudge#smudge(Frame)} won't be executed and any manipulations
	 * won't be applied.
	 *
	 * @return {@code true} if this {@link Smudge} is enabled, {@code false} if
	 *         otherwise
	 */
	public boolean isEnabled() {
		return enabled.getValue();
	}

	@Override
	public void save(PropertyMap pm) {
		super.save(pm);

		for (SmudgeComponent component : components) {
			PropertyMap map = new PropertyMap(component);
			pm.add(map);
		}
	}

	@Override
	public void load(PropertyMap pm) {
		super.load(pm);

		for (PropertyMap component : pm.getChildren(getProject().getComponentLibrary()))
			add(component);
	}

}
