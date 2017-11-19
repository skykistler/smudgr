package io.smudgr.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.smudgr.app.project.Project;
import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.engine.param.BooleanParameter;
import io.smudgr.engine.param.Parametric;
import io.smudgr.util.Frame;

/**
 * A {@link Smudge} is a {@link Parametric} {@link SmudgeComponent} container.
 * The application instance attempts to process each {@link Smudge} at a
 * constant rate.
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

	private List<SmudgeComponent> components = Collections.synchronizedList(new ArrayList<SmudgeComponent>());

	/**
	 * Initialize the smudge. This will be run when the {@link Smudge} is added
	 * to the {@link Rack}, and every time the project is started.
	 */
	public abstract void onInit();

	/**
	 * {@link Smudge} types implement this method to execute their behavior
	 * using any given {@link Frame}
	 *
	 * @param data
	 *            {@link Frame}
	 * @return resulting frame
	 */
	public abstract Frame smudge(Frame data);

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
		SmudgeComponent component = getProject().getSmudgeComponentLibrary().getNewInstance(this, state);

		if (component == null)
			return null;

		synchronized (components) {
			component.setParent(this);
			component.load(state);

			component.onInit();

			components.add(component);
			onAdd(component);

			return component;
		}
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

		synchronized (components) {
			component.setParent(this);
			getProject().add(component);

			component.onInit();

			components.add(component);
			onAdd(component);
		}
	}

	/**
	 * Move a {@link SmudgeComponent} from the first index to the second index.
	 *
	 * @param fromIndex
	 *            first index
	 * @param toIndex
	 *            second index
	 */
	public void move(int fromIndex, int toIndex) {
		synchronized (components) {
			SmudgeComponent toMove = components.remove(fromIndex);
			components.add(toIndex, toMove);
		}
	}

	/**
	 * Remove a {@link SmudgeComponent} instance from this
	 * {@link SmudgeComponent}. This will remove the instance from the project.
	 * 
	 * @param component
	 *            {@link SmudgeComponent}
	 * @return {@code true} if {@link Rack} contained {@link SmudgeComponent}
	 *         and removed
	 *         successfully, {@code false} if otherwise
	 */
	public boolean remove(SmudgeComponent component) {
		synchronized (components) {
			boolean success = components.remove(component);

			if (success)
				getProject().remove(component);

			return success;
		}
	}

	/**
	 * Gets all {@link SmudgeComponent}s added to this
	 * {@link Smudge}
	 *
	 * @return {@code List<SmudgeComponent>}
	 */
	public List<SmudgeComponent> getComponents() {
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

		for (PropertyMap component : pm.getChildren(getProject().getSmudgeComponentLibrary()))
			add(component);
	}

}
