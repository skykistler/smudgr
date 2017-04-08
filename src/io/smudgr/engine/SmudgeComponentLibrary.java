/**
 *
 */
package io.smudgr.engine;

import java.util.HashMap;
import java.util.Set;

import io.smudgr.app.project.reflect.ReflectableType;
import io.smudgr.app.project.reflect.ReflectionLibrary;
import io.smudgr.app.project.util.PropertyMap;

/**
 * A {@link SmudgeComponentLibrary} provides the ability to detect and organize
 * all loaded
 * concrete implementations of a given {@link ReflectableType}
 *
 * @param <T>
 *            The {@link ReflectableType} this Library should manage
 */
public class SmudgeComponentLibrary<T extends SmudgeComponent> extends ReflectionLibrary<T> {

	private HashMap<String, HashMap<String, Class<T>>> implementationsBySmudge = new HashMap<String, HashMap<String, Class<T>>>();
	private HashMap<String, HashMap<String, String>> idToNameBySmudge = new HashMap<String, HashMap<String, String>>();
	private int size = 0;

	/**
	 * Instantiate a new {@link SmudgeComponentLibrary} to detect and catalog
	 * every {@link SmudgeComponent} for every type of {@link Smudge}
	 */
	@SuppressWarnings("unchecked")
	public SmudgeComponentLibrary() {
		super((Class<T>) SmudgeComponent.class);
		load();
	}

	/**
	 * Create a new instance of the {@link ReflectableType} this
	 * {@link SmudgeComponentLibrary} represents, using the given implementation
	 * identifier.
	 *
	 * @param smudgeTypeIdentifier
	 *            The identifier returned by
	 *            {@link Smudge#getTypeIdentifier()}
	 * @param identifier
	 *            The identifier returned by
	 *            {@link ReflectableType#getTypeIdentifier()}
	 * @return A new instance of the given identifier, or {@code null} if it
	 *         can't be found or instantiated.
	 */
	public T getNewInstance(String smudgeTypeIdentifier, String identifier) {
		try {
			return implementationsBySmudge.get(smudgeTypeIdentifier).get(identifier).newInstance();
		} catch (NullPointerException | InstantiationException | IllegalAccessException e) {
			System.out.println("Failed to instantiate a " + getTypeName() + ":" + identifier);
			return null;
		}
	}

	/**
	 * Create a new instance of the {@link ReflectableType} this
	 * {@link SmudgeComponentLibrary} represents, using the implementation
	 * identifier found in the given {@link PropertyMap}
	 *
	 * @param smudge
	 *            {@link Smudge}
	 * @param state
	 *            A {@link PropertyMap} to reference. This won't load the map to
	 *            the instance, just create a new one.
	 * @return A new instance of this type, or {@code null} if it
	 *         can't be found or instantiated.
	 */
	public T getNewInstance(Smudge smudge, PropertyMap state) {
		return getNewInstance(smudge.getTypeIdentifier(), state.getAttribute(PropertyMap.TYPE_ID_ATTR));
	}

	/**
	 * Gets a list of identifiers for every enumerated implementation.
	 *
	 * @param smudgeIdentifier
	 *            The identifier returned by
	 *            {@link Smudge#getTypeIdentifier()}
	 * @return List of implementation identifiers
	 * @see #getNewInstance(String, String) getNewInstance(smudge, identifier)
	 * @see #getNameById(String, String) getNameById(smudge, id)
	 */
	public Set<String> getIdList(String smudgeIdentifier) {
		return implementationsBySmudge.get(smudgeIdentifier).keySet();
	}

	/**
	 * Gets a list of names for every enumerated implementation.
	 *
	 * @param smudgeIdentifier
	 *            The identifier returned by
	 *            {@link Smudge#getTypeIdentifier()}
	 * @return List of implementation names
	 * @see #getNewInstance(String, String) getNewInstance(smudge, identifier)
	 */
	public Set<String> getNameList(String smudgeIdentifier) {
		return idToNameBySmudge.get(smudgeIdentifier).keySet();
	}

	/**
	 * Get the user-recognizable name of an implementation by ID
	 *
	 * @param smudgeIdentifier
	 *            The identifier returned by
	 *            {@link Smudge#getTypeIdentifier()}
	 * @param id
	 *            {@link ReflectableType#getTypeIdentifier()}
	 * @return {@link ReflectableType#getTypeName()}
	 * @see #getIdList(String) getIdList(smudge)
	 */
	public String getNameById(String smudgeIdentifier, String id) {
		return idToNameBySmudge.get(smudgeIdentifier).get(id);
	}

	@Override
	public boolean contains(ReflectableType implementation) {
		if (!(implementation instanceof SmudgeComponent))
			return false;

		SmudgeComponent component = (SmudgeComponent) implementation;

		if (!implementationsBySmudge.containsKey(component.getSmudgeTypeIdentifier()))
			return false;

		return implementationsBySmudge.get(component.getSmudgeTypeIdentifier()).get(component.getTypeIdentifier()) != null;
	}

	@Override
	protected void add(Class<T> type, ReflectableType implementation) {
		if (!(implementation instanceof SmudgeComponent))
			return;

		SmudgeComponent component = (SmudgeComponent) implementation;
		String smudgeType = component.getSmudgeTypeIdentifier();

		if (!implementationsBySmudge.containsKey(smudgeType))
			implementationsBySmudge.put(smudgeType, new HashMap<String, Class<T>>());

		if (!idToNameBySmudge.containsKey(smudgeType))
			idToNameBySmudge.put(smudgeType, new HashMap<String, String>());

		implementationsBySmudge.get(smudgeType).put(implementation.getTypeIdentifier(), type);
		idToNameBySmudge.get(smudgeType).put(implementation.getTypeIdentifier(), implementation.getTypeName());

		size++;
	}

	@Override
	public int count() {
		return size;
	}

}
