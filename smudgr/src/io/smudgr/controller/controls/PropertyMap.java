package io.smudgr.controller.controls;

import java.util.HashMap;

public class PropertyMap {
	private HashMap<String, String> properties = new HashMap<String, String>();

	public HashMap<String, String> getProperties() {
		return properties;
	}

	public String getProperty(String property) {
		return properties.get(property);
	}

	public void setProperty(String property, Object value) {
		properties.put(property, value.toString());
	}
}
