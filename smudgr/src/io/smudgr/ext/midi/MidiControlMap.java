package io.smudgr.ext.midi;

import java.util.HashMap;

import io.smudgr.app.controls.Controllable;

public class MidiControlMap {

	private HashMap<Integer, HashMap<Integer, Controllable>> midiMap = new HashMap<Integer, HashMap<Integer, Controllable>>();

	public void assign(Controllable c, int channel, int key) {
		HashMap<Integer, Controllable> channelMap = midiMap.get(channel);

		if (channelMap == null) {
			channelMap = new HashMap<Integer, Controllable>();
			midiMap.put(channel, channelMap);
		}

		channelMap.put(key, c);
	}

	public int[] getKeyBind(Controllable c) {
		for (Integer chan : midiMap.keySet()) {
			HashMap<Integer, Controllable> channel = midiMap.get(chan);
			for (Integer key : channel.keySet())
				if (channel.get(key) == c)
					return new int[] { chan, key };
		}

		return null;
	}

	public Controllable getControl(int channel, int key) {
		HashMap<Integer, Controllable> channelMap = midiMap.get(channel);

		if (channelMap != null)
			return channelMap.get(key);

		return null;
	}

	public boolean isBound(Controllable c) {
		return getKeyBind(c) != null;

	}

}
