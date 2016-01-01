package io.smudgr;

import javax.sound.midi.MidiMessage;

import io.smudgr.controller.device.Device;
import io.smudgr.controller.device.DeviceObserver;

public class DeviceTest implements DeviceObserver {

	public static void main(String[] args) {
		//		Device.listAvailableDevices();

		Device beatstep = new Device(new DeviceTest(), "Arturia BeatStep");
		System.out.println(beatstep);
	}

	@Override
	public void midiInput(MidiMessage message) {
		byte[] m = message.getMessage();

		System.out.print(Integer.toHexString(message.getStatus()) + ":");
		for (int i = 0; i < message.getLength(); i++) {
			System.out.print(" " + m[i]);
		}
		System.out.println();
	}

}
