package io.smudgr.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.sound.midi.MidiMessage;

import io.smudgr.extensions.midi.Device;
import io.smudgr.extensions.midi.Device.DeviceObserver;

/**
 * Helper class used to test input signals from a MIDI device
 */
public class DeviceTest implements DeviceObserver {

	protected SimpleDateFormat dateFormat;

	/**
	 * Create a new DeviceTest instance
	 */
	public DeviceTest() {
		dateFormat = new SimpleDateFormat("HH:mm:ss");
	}

	/**
	 * Start a new Console instance for listening to MIDI
	 *
	 * @param args
	 *            unused
	 */
	public static void main(String[] args) {
		String deviceIndex = selectDevice();

		ArrayList<DeviceObserver> observers = new ArrayList<DeviceObserver>();
		observers.add(new DeviceTest());
		Device device = new Device(deviceIndex, observers);

		System.out.println("Selected device " + device.toString());
	}

	protected static void renderDeviceList(ArrayList<String> devices) {
		for (int i = 0; i < devices.size(); i++) {
			System.out.println("[Device #" + i + "] " + devices.get(i));
		}

		System.out.println("Enter " + devices.size() + " to refresh device list.");
	}

	protected static String selectDevice() {
		ArrayList<String> devices = Device.getAvailableDevices();
		if (devices.size() == 0) {
			System.out.println("Could not locate any devices.");
			return "";
		}

		Scanner scan = new Scanner(System.in);

		renderDeviceList(devices);

		// set value initially to a number outside the range we want
		int deviceNumber = -1;

		// loop, while the value is below zero
		while (deviceNumber >= devices.size() || deviceNumber < 0) {
			try {
				System.out.print("\nPlease enter device number > ");
				deviceNumber = scan.nextInt();

				if (deviceNumber == devices.size()) {
					devices = Device.getAvailableDevices();
					renderDeviceList(devices);
				}
			}
			// if the user types in anything but an integer,
			// it will 'throw' this 'exception'.
			catch (InputMismatchException ime) {
				// clear the keyboard buffer
				scan.nextLine();
			}
		}

		scan.close();

		return devices.get(deviceNumber);
	}

	@Override
	public void midiInput(MidiMessage message) {
		byte[] m = message.getMessage();
		int status = message.getStatus();
		int channel = status & 0x00F;
		String command = getMessageCommand(status);
		String time = dateFormat.format(new Date());

		System.out.print("[" + time + "] [Controller " + (channel + 1) + "] [" + command + "]:");

		// First byte is the status.
		for (int i = 1; i < message.getLength(); i++) {
			int data = m[i] & 0xFF;
			System.out.print(" " + data);
		}
		System.out.println();
	}

	/**
	 * Return the command type from a MIDI status
	 *
	 * @param command
	 *            {@code byte}
	 * @return String
	 */
	public String getMessageCommand(int command) {
		// 0x8N (128-143)
		if (command >= 0x80 && command <= 0x8F) {
			return "Note Off";
		}

		// 0x9N (144-159)
		if (command >= 0x90 && command <= 0x9F) {
			return "Note On";
		}

		// 0xAN (160-175)
		if (command >= 0xA0 && command <= 0xAF) {
			return "Polyphonic Aftertouch";
		}

		// 0xBN (176-191)
		if (command >= 0xB0 && command <= 0xBF) {
			return "Control Change";
		}

		// 0xCN (192-207)
		if (command >= 0xC0 && command <= 0xCF) {
			return "Program Change";
		}

		// 0xDN (208-223)
		if (command >= 0xD0 && command <= 0xDF) {
			return "Channel Aftertouch";
		}

		// 0xEN (224-239)
		if (command >= 0xE0 && command <= 0xEF) {
			return "Pitch Wheel";
		}

		return "0x" + Integer.toHexString(command);
	}

}