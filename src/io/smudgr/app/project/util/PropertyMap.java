package io.smudgr.app.project.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class PropertyMap {

	private String tag;

	private HashMap<String, String> attributes = new HashMap<String, String>();
	private HashMap<String, ArrayList<PropertyMap>> children = new HashMap<String, ArrayList<PropertyMap>>();

	public PropertyMap(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	public boolean hasAttribute(String attribute) {
		return attributes.containsKey(attribute);
	}

	public String getAttribute(String attribute) {
		return attributes.get(attribute);
	}

	public void setAttribute(String attribute, Object value) {
		attributes.put(attribute, value.toString());
	}

	public Collection<String> getAttributeKeys() {
		return attributes.keySet();
	}

	public void add(PropertyMap pm) {
		if (pm != null && pm != this)
			getChildren(pm.getTag()).add(pm);
	}

	public ArrayList<PropertyMap> getChildren(String tag) {
		if (!children.containsKey(tag)) {
			ArrayList<PropertyMap> childs = new ArrayList<PropertyMap>();
			children.put(tag, childs);
		}

		return children.get(tag);
	}

	public Collection<String> getChildrenTags() {
		return children.keySet();
	}

}
