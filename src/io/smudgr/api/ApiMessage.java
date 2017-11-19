package io.smudgr.api;

import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import io.smudgr.app.project.util.PropertyMap;

/**
 * The {@link ApiMessage} class represents a packet of information for
 * communicating with the API.
 * <p>
 * Instances are backed by a {@link JSONObject}, so this class can be thought of
 * as having key:value pairs and nested information.
 */
public class ApiMessage {

	private static final JSONParser parser = new JSONParser();

	private JSONObject payload = new JSONObject();

	/**
	 * Instantiate an empty {@link ApiMessage}
	 */
	public ApiMessage() {
	}

	/**
	 * Instantiate an {@link ApiMessage} with the given key and value.
	 * 
	 * @param initialKey
	 *            The initial key to set.
	 * @param initialValue
	 *            The value to store at the initial key.
	 */
	public ApiMessage(String initialKey, Object initialValue) {
		put(initialKey, initialValue);
	}

	/**
	 * Get the string value of a given key.
	 * 
	 * @param key
	 *            The key to lookup.
	 * @return The string value of the key, or {@code null} if the key doesn't
	 *         exist.
	 */
	public String get(String key) {
		if (!hasKey(key))
			return null;

		return payload.get(key).toString();
	}

	/**
	 * Get the {@link JSONObject} value of a given key.
	 * 
	 * @param key
	 *            The key to lookup.
	 * @return The {@link JSONObject} value of the key, or {@code null} if the
	 *         key doesn't
	 *         exist.
	 */
	public JSONObject object(String key) {
		if (!hasKey(key))
			return null;

		return (JSONObject) payload.get(key);
	}

	/**
	 * Get the numeric value of a given key.
	 * 
	 * @param key
	 *            The key to lookup.
	 * @return The numeric {@code double} value at the given key, or
	 *         {@code Double.NaN} if the key doesn't exist
	 */
	public double getNumber(String key) {
		if (!hasKey(key))
			return Double.NaN;

		return Double.parseDouble(get(key));
	}

	/**
	 * @return A set of keys contained in this map
	 */
	@SuppressWarnings("unchecked")
	public Set<String> keySet() {
		return payload.keySet();
	}

	/**
	 * Check whether this ApiMessage has a non-null value at the given key.
	 * 
	 * @param key
	 *            The key to lookup.
	 * @return {@code true} is this {@link ApiMessage} contains a non-null value
	 *         at this key, {@code false} if otherwise.
	 */
	public boolean hasKey(String key) {
		return payload.containsKey(key) && payload.get(key) != null;
	}

	/**
	 * Put an arbitrary value at the given key.
	 * 
	 * @param key
	 *            The key to set.
	 * @param value
	 *            The value to set the key to.
	 */
	@SuppressWarnings("unchecked")
	public void put(Object key, Object value) {
		payload.put(key, value);
	}

	/**
	 * Serialize this {@link ApiMessage} into a JSON string.
	 * 
	 * @return The serialized JSON string.
	 */
	public String serialize() {
		return payload.toString();
	}

	/**
	 * String representation of this {@link ApiMessage}. Same as
	 * {@link ApiMessage#serialize()}
	 * 
	 * @see ApiMessage#serialize()
	 */
	@Override
	public String toString() {
		return serialize();
	}

	/**
	 * Create a command packet with the 'success' status containing the intended
	 * response data.
	 * 
	 * @param command
	 *            The command to reference. Used for generic responses or
	 *            responses from a specific command.
	 * @param data
	 *            Optional data to send with packet, {@code null} if N/A
	 * @return A successful command packet with the given data.
	 * @see ApiMessage#command(String, String, ApiMessage)
	 */
	public static ApiMessage success(String command, ApiMessage data) {
		return command(command, "success", data);
	}

	/**
	 * Create a command packet with the 'failed' status containing the intended
	 * response data.
	 * 
	 * @param command
	 *            The command to reference. Used for generic responses or
	 *            responses from a specific command.
	 * @param data
	 *            Optional data to send with packet, {@code null} if N/A
	 * @return A failed command packet with the given data.
	 * @see ApiMessage#command(String, String, ApiMessage)
	 */
	public static ApiMessage failed(String command, ApiMessage data) {
		return command(command, "failed", data);
	}

	/**
	 * Create a command packet with the 'ok' status containing the intended
	 * response data.
	 * 
	 * @param command
	 *            The command to reference. Used for generic responses or
	 *            responses from a specific command.
	 * @param data
	 *            Optional data to send with packet, {@code null} if N/A
	 * @return An ok command packet with the given data.
	 * @see ApiMessage#command(String, String, ApiMessage)
	 */
	public static ApiMessage ok(String command, ApiMessage data) {
		return command(command, "ok", data);
	}

	/**
	 * Create a generic command packet with 'command', 'status', and 'data'
	 * properties.
	 * 
	 * @param command
	 *            The command to reference.
	 * @param status
	 *            The status to signify by this packet.
	 * @param data
	 *            Optional response data, or {@code null} is N/A
	 * @return A generic command packet with the given data.
	 * 
	 * @see ApiMessage#success(String, ApiMessage)
	 * @see ApiMessage#failed(String, ApiMessage)
	 * @see ApiMessage#ok(String, ApiMessage)
	 */
	public static ApiMessage command(String command, String status, ApiMessage data) {
		ApiMessage packet = new ApiMessage();
		packet.put("command", command);
		packet.put("status", status);

		packet.put("data", data != null ? data : new ApiMessage());

		return packet;
	}

	/**
	 * Deserialize a generic JSON packet string into an {@link ApiMessage}
	 * 
	 * @param message
	 *            {@link String} to deserialize.
	 * @return {@link ApiMessage} represented by {@code message}, or
	 *         {@code null} if failed;
	 */
	public static ApiMessage deserialize(String message) {
		ApiMessage result = new ApiMessage();

		try {
			JSONObject obj = (JSONObject) JSONValue.parse(message);
			result.payload = obj;
		} catch (Exception e) {
			System.out.println("Unable to parse JSON from message: " + message);
			return null;
		}

		if (result.payload == null) {
			result.payload = new JSONObject();
		}

		return result;
	}

	/**
	 * Create an {@link ApiMessage} from a given {@link PropertyMap}
	 * 
	 * @param map
	 *            The {@link PropertyMap} to normalize.
	 * @return {@link ApiMessage} representing the normalized
	 *         {@link PropertyMap}
	 * @see PropertyMap
	 */
	public static ApiMessage normalize(PropertyMap map) {
		ApiMessage result = new ApiMessage();
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
