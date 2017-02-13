package io.smudgr.app.project.reflect;

import java.util.HashMap;
import java.util.Set;

import io.smudgr.app.project.util.PropertyMap;

/**
 * A {@link TypeLibrary} provides the ability to detect and organize all loaded
 * concrete implementations of a given {@link ReflectableType}
 *
 * @param <T>
 *            The {@link ReflectableType} this Library should manage
 */
public class TypeLibrary<T extends ReflectableType> extends ReflectionLibrary<T> {

	private HashMap<String, Class<T>> implementations = new HashMap<String, Class<T>>();
	private HashMap<String, String> idToName = new HashMap<String, String>();

	/**
	 * Instantiate a new {@link TypeLibrary} to detect and catalog every
	 * implementation of the given type.
	 *
	 * @param type
	 *            {@link Class} of the type to organize
	 */
	public TypeLibrary(Class<T> type) {
		super(type);
		load();
	}

	/**
	 * Create a new instance of the {@link ReflectableType} this
	 * {@link TypeLibrary} represents, using the given implementation
	 * identifier.
	 *
	 * @param identifier
	 *            The identifier returned by
	 *            {@link ReflectableType#getIdentifier()}
	 * @return A new instance of the given identifier, or {@code null} if it
	 *         can't be found or instantiated.
	 */
	public T getNewInstance(String identifier) {
		try {
			return implementations.get(identifier).newInstance();
		} catch (NullPointerException | InstantiationException | IllegalAccessException e) {
			System.out.println("Failed to instantiate a " + getTypeName() + ":" + identifier);
			return null;
		}
	}

	/**
	 * Create a new instance of the {@link ReflectableType} this
	 * {@link TypeLibrary} represents, using the implementation
	 * identifier found in the given {@link PropertyMap}
	 *
	 * @param state
	 *            A {@link PropertyMap} to reference. This won't load the map to
	 *            the instance, just create a new one.
	 * @return A new instance of this type, or {@code null} if it
	 *         can't be found or instantiated.
	 */
	public T getNewInstance(PropertyMap state) {
		return getNewInstance(state.getAttribute("type"));
	}

	/**
	 * Gets a list of identifiers for every enumerated implementation.
	 *
	 * @return List of implementation identifiers
	 * @see #getNewInstance(String) getNewInstance(identifier)
	 * @see #getNameById(String) getNameById(id)
	 */
	public Set<String> getIdList() {
		return implementations.keySet();
	}

	/**
	 * Gets a list of names for every enumerated implementation.
	 *
	 * @return List of implementation names
	 * @see #getNewInstance(String) getNewInstance(identifier)
	 */
	public Set<String> getNameList() {
		return idToName.keySet();
	}

	/**
	 * Get the user-recognizable name of an implementation by ID
	 *
	 * @param id
	 *            {@link ReflectableType#getIdentifier()}
	 * @return {@link ReflectableType#getName()}
	 * @see #getIdList()
	 */
	public String getNameById(String id) {
		return idToName.get(id);
	}

	@Override
	public boolean contains(ReflectableType implementation) {
		return implementations.get(implementation.getIdentifier()) != null;
	}

	@Override
	protected void add(Class<T> type, ReflectableType implementation) {
		implementations.put(implementation.getIdentifier(), type);
		idToName.put(implementation.getIdentifier(), implementation.getName());
	}

	@Override
	public int count() {
		return implementations.size();
	}

}
