package io.smudgr.app.project.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import io.smudgr.api.ApiMessage;
import io.smudgr.app.project.Project;
import io.smudgr.app.project.ProjectItem;
import io.smudgr.app.project.reflect.ReflectionLibrary;

/**
 * The {@link PropertyMap} class can be used to store hierarchical DOM-style
 * data.
 * <p>
 * Property maps have attributes and value pairs, as well as nested child maps.
 * <p>
 * Property maps can be serialized into a JSON-ready structure with
 * {@link ApiMessage#normalize(PropertyMap)} or persisted as XML using the
 * {@link ProjectSaver}
 * <p>
 * Nested property maps are passed recursively through the {@link Project}
 * structure and are used to persist and load project data.
 */
public class PropertyMap {

	/**
	 * The universal attribute for serializing a project ID
	 */
	public static final String PROJECT_ID_ATTR = "id";

	/**
	 * The universal attribute for serializing an element type
	 */
	public static final String TYPE_ID_ATTR = "type";

	/**
	 * The universal attribute for serializing an element's user-recognizable
	 * name.
	 */
	public static final String NAME_ATTR = "name";

	private String tag;
	private HashMap<String, String> attributes = new HashMap<String, String>();
	private HashMap<String, ArrayList<PropertyMap>> children = new HashMap<String, ArrayList<PropertyMap>>();

	/**
	 * Create a new {@link PropertyMap} identified by the given tag name.
	 *
	 * @param tag
	 *            Not necessarily unique identifier of the type of data this map
	 *            holds.
	 */
	public PropertyMap(String tag) {
		this.tag = tag;
	}

	/**
	 * Create a property map with the given {@link ProjectItem}.
	 * <p>
	 * This immediately calls {@link ProjectItem#save(PropertyMap)}.
	 * <p>
	 * This will set the map tag to
	 * {@link ProjectItem#getTypeCategoryIdentifier()} and the type attribute to
	 * {@link ProjectItem#getTypeIdentifier()}.
	 * <p>
	 * If the given element has a project ID, the id attribute is set to
	 * {@link Project#getId(ProjectItem)}
	 *
	 * @param item
	 *            {@link ProjectItem}
	 * @see ProjectItem#save(PropertyMap)
	 */
	public PropertyMap(ProjectItem item) {
		this.tag = item.getTypeCategoryIdentifier();
		item.save(this);
	}

	/**
	 * Get the tag used to specify the type of data held in this map.
	 *
	 * @return Map type.
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * {@code true} if the given attribute exists in this mapping.
	 *
	 * @param attribute
	 *            Given key.
	 * @return {@code true} if this map contains a mapping for the specified key
	 * @see PropertyMap#getAttributeKeys()
	 */
	public boolean hasAttribute(String attribute) {
		return attributes.containsKey(attribute);
	}

	/**
	 * Get the value of the specified attribute, or {@code null} if no mapping
	 * exists.
	 *
	 * @param attribute
	 *            Given key.
	 * @return the value at the specified attribute or {@code null} if no
	 *         mapping exists.
	 * @see PropertyMap#setAttribute(String, Object)
	 */
	public String getAttribute(String attribute) {
		return attributes.get(attribute);
	}

	/**
	 * Sets the value of a given attribute
	 *
	 * @param attribute
	 *            Given key.
	 * @param value
	 *            Given value, persisted as a {@link String}
	 * @see PropertyMap#getAttribute(String)
	 */
	public void setAttribute(String attribute, Object value) {
		attributes.put(attribute, value.toString());
	}

	/**
	 * Gets full list of currently mapped attributes
	 *
	 * @return currently mapped attributes
	 * @see PropertyMap#hasAttribute(String)
	 */
	public Collection<String> getAttributeKeys() {
		return attributes.keySet();
	}

	/**
	 * Add a {@link PropertyMap} as a child to this map.
	 *
	 * @param pm
	 *            Child map.
	 */
	public void add(PropertyMap pm) {
		if (pm != null && pm != this)
			getChildren(pm.getTag()).add(pm);
	}

	/**
	 * Gets all children of this map of the given tag.
	 *
	 * @param tag
	 *            Type of map.
	 * @return List of children of given type.
	 * @see PropertyMap#getChildrenTags()
	 */
	public ArrayList<PropertyMap> getChildren(String tag) {
		if (!children.containsKey(tag)) {
			ArrayList<PropertyMap> childs = new ArrayList<PropertyMap>();
			children.put(tag, childs);
		}

		return children.get(tag);
	}

	/**
	 * Gets all children that correspond to the given {@link ReflectionLibrary}
	 *
	 * @param library
	 *            Reflection library of desired children types.
	 * @return List of children of given type.
	 * @see PropertyMap#getChildren(String)
	 */
	public ArrayList<PropertyMap> getChildren(ReflectionLibrary<?> library) {
		return getChildren(library.getTypeIdentifier());
	}

	/**
	 * Returns if the map has any children with the given tag
	 *
	 * @param tag
	 *            children
	 * @return {@code true} if there's at least one entry with the given tag,
	 *         {@code false} if otherwise
	 */
	public boolean hasChildren(String tag) {
		// If there's no child entry for this tag, there's no children
		if (!children.containsKey(tag))
			return false;

		// If there is at least one child, return true
		return children.get(tag).size() > 0;
	}

	/**
	 * Gets whether the given child {@link PropertyMap} is contained in this
	 * map.
	 *
	 * @param child
	 *            {@link PropertyMap}
	 * @return {@code true} if this map contains the given child
	 */
	public boolean contains(PropertyMap child) {
		if (child == null || !hasChildren(child.getTag()))
			return false;

		ArrayList<PropertyMap> children = getChildren(child.getTag());
		return children.contains(child);
	}

	/**
	 * Gets all the tag types of children on this map.
	 *
	 * @return all children tags
	 * @see PropertyMap#getChildren(String)
	 */
	public Collection<String> getChildrenTags() {
		return children.keySet();
	}

	/**
	 * Removes the given child {@link PropertyMap} if it's contained in this
	 * map.
	 *
	 * @param child
	 *            {@link PropertyMap}
	 */
	public void removeChild(PropertyMap child) {
		if (!contains(child))
			return;

		ArrayList<PropertyMap> children = getChildren(child.getTag());
		children.remove(child);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;

		if (!(o instanceof PropertyMap))
			return false;

		PropertyMap pm = (PropertyMap) o;

		// Must have project IDs
		if (!pm.hasAttribute(PROJECT_ID_ATTR) || !this.hasAttribute(PROJECT_ID_ATTR))
			return false;

		// Must have same project ID
		if (pm.getAttribute(PROJECT_ID_ATTR) != this.getAttribute(PROJECT_ID_ATTR))
			return false;

		// If they have type IDs, must be the same type ID
		if (pm.hasAttribute(TYPE_ID_ATTR) && this.hasAttribute(TYPE_ID_ATTR))
			if (pm.getAttribute(TYPE_ID_ATTR) != this.getAttribute(TYPE_ID_ATTR))
				return false;

		return true;
	}

}
