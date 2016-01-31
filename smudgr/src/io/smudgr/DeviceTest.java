package io.smudgr;

import javax.sound.midi.MidiMessage;

import io.smudgr.controller.device.Device;
import io.smudgr.controller.device.DeviceObserver;

public class DeviceTest implements DeviceObserver {

	public static void main(String[] args) {
		Device.listAvailableDevices();

		// String device = "MIDIIN2 (Ableton Push)";
		String device = "Arturia BeatStep Pro";

		System.out.println(new Device(new DeviceTest(), device));
	}

	@Override
	public void midiInput(MidiMessage message) {
		byte[] m = message.getMessage();
		int status = message.getStatus();

		System.out.print(Integer.toHexString(status) + " - " + status + ":");
		for (int i = 0; i < message.getLength(); i++) {
			System.out.print(" " + m[i]);
		}
		System.out.println();
	}

}
