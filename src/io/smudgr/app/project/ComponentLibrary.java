package io.smudgr.app.project;

import java.util.HashMap;

import io.smudgr.engine.Smudge;
import io.smudgr.engine.alg.AlgorithmComponent;
import io.smudgr.util.Reflect;

/**
 * Enumerates all classes currently loaded by the JVM that implement the
 * {@link AlgorithmComponent} type. This system is set to change until the
 * {@link Smudge} pipeline has been finalized.
 */
public class ComponentLibrary {

	private HashMap<String, HashMap<String, Class<?>>> components = new HashMap<String, HashMap<String, Class<?>>>();

	/**
	 * Instantiate a new {@link ComponentLibrary} and start reflecting every
	 * {@link AlgorithmComponent} type.
	 */
	public ComponentLibrary() {
		Reflect reflectComponents = new Reflect(AlgorithmComponent.class);

		for (Class<?> c : reflectComponents.get()) {
			try {
				AlgorithmComponent comp = (AlgorithmComponent) c.newInstance();

				HashMap<String, Class<?>> type = components.get(comp.getType());
				if (type == null) {
					type = new HashMap<String, Class<?>>();
					components.put(comp.getType(), type);
				}

				type.put(comp.getName(), c);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Get a new instance of an {@link AlgorithmComponent} of the given type and
	 * name or null if the type doesn't exist in the current application
	 * instance.
	 * <p>
	 * {@link AlgorithmComponent} classes are grouped by type and identified by
	 * name. Both must be specified in order to a specify a specific
	 * {@link AlgorithmComponent} implementation.
	 * 
	 * @param type
	 *            Registered type of the component.
	 * @param name
	 *            Registered name of the component.
	 * @return new {@link AlgorithmComponent} of given type/name or null if
	 *         type/name not found
	 */
	public AlgorithmComponent getNewComponent(String type, String name) {
		if (type == null || name == null)
			return null;

		try {
			HashMap<String, Class<?>> ofType = components.get(type);
			if (ofType == null)
				return null;

			Class<?> compClass = ofType.get(name);

			if (compClass == null)
				return null;

			return (AlgorithmComponent) compClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}
}
