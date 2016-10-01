package io.smudgr.extensions.midi;

import java.util.ArrayList;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

public class Device {

	private static ArrayList<Info> boundDevices = new ArrayList<Info>();

	private ArrayList<DeviceObserver> observers;
	private MidiDevice device;

	public Device(String name, ArrayList<DeviceObserver> observers) {
		this.observers = observers;

		Info[] midiDevices = MidiSystem.getMidiDeviceInfo();
		Info desired = null;
		String lowerName = name.toLowerCase();
		for (Info i : midiDevices) {
			if (boundDevices.contains(i))
				continue;

			try {
				MidiDevice d = MidiSystem.getMidiDevice(i);
				Transmitter t = d.getTransmitter();
				String deviceName = i.getName().toLowerCase();
				if (t != null && deviceName.equals(lowerName) || (desired == null && deviceName.startsWith(lowerName)))
					desired = i;
			} catch (Exception e) {
			}
		}

		if (desired == null) {
			System.out.println("No available device named '" + name + "' found.\nPlease choose one of the following:");
			ArrayList<String> devices = getAvailableDevices();
			for (int i = 0; i < devices.size(); i++) {
				System.out.println("[Device #" + i + "] " + devices.get(i));
			}
		} else
			try {
				boundDevices.add(desired);
				device = MidiSystem.getMidiDevice(desired);
				device.open();

				Transmitter t = device.getTransmitter();
				t.setReceiver(new DeviceReceiver());
				if (t.getReceiver() == null)
					throw new MidiUnavailableException("Unable to bind to " + name);
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
	}

	public String toString() {
		if (device == null)
			return "no device";
		else
			return device.getDeviceInfo().getName();
	}

	public class DeviceReceiver implements Receiver {

		public void send(MidiMessage message, long timeStamp) {
			for (DeviceObserver observer : observers)
				observer.midiInput(message);
		}

		public String toString() {
			return device.getDeviceInfo().getName();
		}

		public void close() {
		}

	}

	/**
	 * Return a list of device names.
	 * 
	 * @return String[]
	 */
	public static ArrayList<String> getAvailableDevices() {
		Info[] midiDevices = MidiSystem.getMidiDeviceInfo();
		ArrayList<String> devices = new ArrayList<String>();

		for (Info i : midiDevices) {
			MidiDevice device = null;

			try {
				device = MidiSystem.getMidiDevice(i);
				Transmitter t = device.getTransmitter();
				if (t != null)
					devices.add(i.getName());
			} catch (Exception e) {
			} finally {
				if (device.isOpen())
					device.close();
			}
		}

		return devices;
	}

	public interface DeviceObserver {
		public void midiInput(MidiMessage message);
	}
}
