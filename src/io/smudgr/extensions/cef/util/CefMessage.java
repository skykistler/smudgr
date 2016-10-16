package io.smudgr.extensions.cef.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import io.smudgr.project.PropertyMap;

public class CefMessage {

	private JSONObject payload;

	public CefMessage() {
		payload = new JSONObject();
	}

	public CefMessage(String initialKey, String initialValue) {
		this();
		put(initialKey, initialValue);
	}

	public String get(String key) {
		if (!hasKey(key))
			return null;

		return payload.get(key).toString();
	}

	public double getNumber(String key) {
		if (!hasKey(key))
			return -1;

		return Double.parseDouble(get(key));
	}

	public boolean hasKey(String key) {
		return payload.containsKey(key) && payload.get(key) != null;
	}

	@SuppressWarnings("unchecked")
	public void put(String key, Object value) {
		payload.put(key, value);
	}

	public String serialize() {
		return payload.toString();
	}

	public String toString() {
		return serialize();
	}

	public static CefMessage command(String command, CefMessage data) {
		return command(command, "success", data);
	}

	public static CefMessage command(String command, String status, CefMessage data) {
		CefMessage packet = new CefMessage();
		packet.put("command", command);
		packet.put("status", status);

		packet.put("data", data != null ? data : new CefMessage());

		return packet;
	}

	public static CefMessage deserialize(String message) {
		CefMessage result = new CefMessage();

		try {
			JSONObject obj = (JSONObject) JSONValue.parse(message);
			result.payload = obj;
		} catch (Exception e) {
			System.out.println("Unable to parse JSON from message: " + message);
		}

		if (result.payload == null) {
			result.payload = new JSONObject();
		}

		return result;
	}

	public static CefMessage normalize(PropertyMap map) {
		CefMessage result = new CefMessage();
		result.put(map.getTag(), buildJson(map));

		return result;
	}

	@SuppressWarnings("unchecked")
	private static JSONObject buildJson(PropertyMap map) {
		JSONObject json = new JSONObject();

		for (String attribute : map.getAttributeKeys())
			json.put(attribute, map.getAttribute(attribute));

		for (String childTag : map.getChildrenTags()) {
			JSONArray children = new JSONArray();

			for (PropertyMap childMap : map.getChildren(childTag)) {
				try {
					JSONObject child = buildJson(childMap);
					children.add(child);
				} catch (Exception e) {
					System.out.println("Unable to parse child: " + childTag);
				}
			}

			json.put(childTag + (childTag.equals("app") ? "" : "s"), children);
		}

		return json;
	}

}
