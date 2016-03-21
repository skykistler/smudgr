package io.smudgr.controller.controls;

import java.util.HashMap;

public class PropertyMap {
	private boolean set;
	private HashMap<String, String> properties = new HashMap<String, String>();

	public HashMap<String, String> getProperties() {
		return properties;
	}

	public String getProperty(String property) {
		return properties.get(property);
	}

	public void setProperty(String property, Object value) {
		set = true;
		properties.put(property, value.toString());
	}

	public boolean isSet() {
		return set;
	}
}
