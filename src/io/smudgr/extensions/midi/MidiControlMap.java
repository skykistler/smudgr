package io.smudgr.extensions.midi;

import java.util.ArrayList;
import java.util.HashMap;

import io.smudgr.app.project.util.PropertyMap;

public class MidiControlMap {
	
	public static final String PROPERTY_MAP_KEY = "midi";

	private ArrayList<Integer> bound = new ArrayList<Integer>();
	private HashMap<Integer, HashMap<Integer, Integer>> midiMap = new HashMap<Integer, HashMap<Integer, Integer>>();

	private HashMap<Integer, HashMap<Integer, Boolean>> absoluteBinds = new HashMap<Integer, HashMap<Integer, Boolean>>();

	public void setBind(int control, int channel, int key, boolean absolute) {
		HashMap<Integer, Integer> channelMap = midiMap.get(channel);

		if (channelMap == null) {
			channelMap = new HashMap<Integer, Integer>();
			midiMap.put(channel, channelMap);
		}

		channelMap.put(key, control);
		bound.add(control);

		if (absolute)
			setAbsoluteBind(channel, key);
	}

	public int getBind(int channel, int key) {
		HashMap<Integer, Integer> channelMap = midiMap.get(channel);

		if (channelMap == null || !channelMap.containsKey(key))
			return -1;

		return channelMap.get(key);
	}

	public void unBind(int control) {
		int[] keyBind = getBind(control);

		if (keyBind == null)
			return;

		midiMap.get(keyBind[0]).remove(keyBind[1]);
		bound.remove(control);
	}

	public boolean hasBind(int control) {
		return bound.contains(control);
	}

	protected ArrayList<PropertyMap> getBinds() {
		ArrayList<PropertyMap> binds = new ArrayList<PropertyMap>();

		for (Integer chan : midiMap.keySet()) {
			HashMap<Integer, Integer> channel = midiMap.get(chan);

			for (Integer key : channel.keySet()) {
				PropertyMap mapping = new PropertyMap("midi");

				mapping.setAttribute("control", channel.get(key));
				mapping.setAttribute("channel", chan);
				mapping.setAttribute("key", key);

				if (isAbsoluteBind(chan, key))
					mapping.setAttribute("absolute", true);

				binds.add(mapping);
			}
		}

		return binds;
	}

	private int[] getBind(int control) {

		for (Integer chan : midiMap.keySet()) {
			HashMap<Integer, Integer> channel = midiMap.get(chan);

			for (Integer key : channel.keySet())
				if (channel.get(key) == control)
					return new int[] { chan, key };
		}

		return null;
	}

	private void setAbsoluteBind(int channel, int key) {
		HashMap<Integer, Boolean> channelMap = absoluteBinds.get(channel);

		if (channelMap == null) {
			channelMap = new HashMap<Integer, Boolean>();
			absoluteBinds.put(channel, channelMap);
		}

		channelMap.put(key, true);
	}

	public boolean isAbsoluteBind(int channel, int key) {
		HashMap<Integer, Boolean> channelMap = absoluteBinds.get(channel);

		if (channelMap == null || !channelMap.containsKey(key))
			return false;

		return channelMap.get(key);
	}

}
