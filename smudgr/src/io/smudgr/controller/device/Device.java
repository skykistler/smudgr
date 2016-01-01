package io.smudgr.controller.device;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

public class Device {

	private DeviceObserver parent;
	private MidiDevice device;

	public Device(DeviceObserver applet, String name) {
		parent = applet;

		Info[] midiDevices = MidiSystem.getMidiDeviceInfo();
		Info desired = null;
		for (Info i : midiDevices) {
			MidiDevice d = null;

			try {
				d = MidiSystem.getMidiDevice(i);
				Transmitter t = d.getTransmitter();
				if (t != null && i.getName().equals(name))
					desired = i;
			} catch (Exception e) {
			} finally {
				if (d.isOpen())
					d.close();
			}
		}

		if (desired == null) {
			System.out.println("No available device named '" + name + "' found.\nPlease choose one of the following:");
			listAvailableDevices();
		} else
			try {
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
		return device.getDeviceInfo().getName();
	}

	public class DeviceReceiver implements Receiver {

		@Override
		public void send(MidiMessage message, long timeStamp) {
			parent.midiInput(message);
		}

		public String toString() {
			return device.getDeviceInfo().getName();
		}

		@Override
		public void close() {
		}

	}

	public static void listAvailableDevices() {
		Info[] midiDevices = MidiSystem.getMidiDeviceInfo();

		for (Info i : midiDevices) {
			MidiDevice device = null;

			try {
				device = MidiSystem.getMidiDevice(i);
				Transmitter t = device.getTransmitter();
				if (t != null)
					System.out.println(i);
			} catch (Exception e) {
			} finally {
				if (device.isOpen())
					device.close();
			}
		}
	}

}
