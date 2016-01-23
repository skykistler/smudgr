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
	private HashMap<Integer, Controllable> midiMap;
	private HashMap<String, Integer> savedMap;

	public MidiControlMap(String filename) {
		midiMap = new HashMap<Integer, Controllable>();
		savedMap = new HashMap<String, Integer>();

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
				savedMap.put(parts[1], Integer.parseInt(parts[0]));
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
			for (Integer i : midiMap.keySet()) {
				sb.append(i + ":" + midiMap.get(i) + "\r\n");
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

	public void assign(int key, Controllable c) {
		midiMap.put(key, c);
	}

	public Controllable getKeyBind(int key) {
		return midiMap.get(key);
	}

	public boolean isSaved(Controllable c) {
		boolean contains = savedMap.containsKey(c.toString());

		if (contains) {
			int key = savedMap.get(c.toString());

			Controllable bound = getKeyBind(key);
			if (bound == null) {
				assign(key, c);
				return true;
			} else if (!bound.equals(c.toString())) {
				System.out.println("Bound conflict - " + c.toString() + " saved map has been overridden. Please bind to a different key.");
				return false;
			}
		}

		return false;

	}

}
