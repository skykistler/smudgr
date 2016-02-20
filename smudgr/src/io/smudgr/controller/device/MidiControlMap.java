package io.smudgr.controller.device;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import io.smudgr.controller.controls.Controllable;

public class MidiControlMap {

	private String filename;
	private HashMap<Integer, HashMap<Integer, Controllable>> midiMap;
	private HashMap<String, String> savedMap;

	public MidiControlMap(String filename) {
		midiMap = new HashMap<Integer, HashMap<Integer, Controllable>>();
		savedMap = new HashMap<String, String>();

		this.filename = "data/midi/" + filename;
		load();
	}

	private void load() {
		File midiFolder = new File("data/midi");
		midiFolder.mkdirs();

		File f = new File(filename);

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
		} catch (FileNotFoundException e) {
			System.out.println("MIDI map file not found, creating new one...");
		}

		if (br == null)
			return;

		String line;
		try {
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(":");
				if (parts.length == 3)
					savedMap.put(parts[2], parts[0] + ":" + parts[1]);
			}
		} catch (Exception e) {
			System.out.println("Error while reading the MIDI map file!");
			e.printStackTrace();
		}
	}

	public void save() {
		System.out.print("Saving MIDI mappings to: " + filename);
		try {
			System.out.print(".");

			StringBuffer sb = new StringBuffer();

			for (Integer channel : midiMap.keySet()) {
				HashMap<Integer, Controllable> channelMap = midiMap.get(channel);
				for (Integer key : channelMap.keySet())
					sb.append(channel + ":" + key + ":" + channelMap.get(key) + "\r\n");
			}

			System.out.print(".");

			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			bw.write(sb.toString());
			bw.close();

			System.out.println(". completed!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void assign(int channel, int key, Controllable c) {
		HashMap<Integer, Controllable> channelMap = midiMap.get(channel);

		if (channelMap == null) {
			channelMap = new HashMap<Integer, Controllable>();
			midiMap.put(channel, channelMap);
		}

		channelMap.put(key, c);
	}

	public Controllable getKeyBind(int channel, int key) {
		HashMap<Integer, Controllable> channelMap = midiMap.get(channel);

		if (channelMap != null)
			return channelMap.get(key);

		return null;
	}

	public boolean isSaved(Controllable c) {
		boolean contains = savedMap.containsKey(c.toString());

		if (contains) {
			String channelAndKey = savedMap.get(c.toString());
			String[] parts = channelAndKey.split(":");

			int channel = Integer.parseInt(parts[0]);
			int key = Integer.parseInt(parts[1]);

			Controllable bound = getKeyBind(channel, key);
			if (bound == null) {
				assign(channel, key, c);
				return true;
			} else if (!bound.equals(c.toString())) {
				System.out.println("Bound conflict - " + c.toString() + " saved map has been overridden. Please bind to a different key.");
				return false;
			}
		}

		return false;

	}

}
