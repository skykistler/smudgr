package io.smudgr.extensions.midi.tcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.sound.midi.MidiMessage;

import io.smudgr.extensions.midi.Device;
import io.smudgr.extensions.midi.Device.DeviceObserver;

public class DeviceServer implements DeviceObserver {

	public static final int SMUDGR_PORT = 45454;
	public static final String CONNECT_RESPONSE = "connected";

	private ServerSocket server;
	private ClientListener listener;
	private ArrayList<DataOutputStream> clients = new ArrayList<DataOutputStream>();

	public DeviceServer() {
		try {
			server = new ServerSocket(SMUDGR_PORT);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Unable to start Device server!");
		}

		System.out.println("Starting device server on port " + SMUDGR_PORT);
		listener = new ClientListener();
		listener.start();
	}

	public void midiInput(MidiMessage message) {
		byte[] byteMessage = message.getMessage();

		for (DataOutputStream client : clients) {
			try {
				client.write(byteMessage);
				client.flush();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Unable to send MIDI message to client");
			}
		}
	}

	public void stop() {
		listener.stop();

		for (DataOutputStream client : clients) {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class ClientListener implements Runnable {

		private volatile boolean listening;

		public void start() {
			listening = true;
			(new Thread(this)).start();
		}

		public void run() {
			while (listening) {
				Socket client;
				try {
					client = server.accept();

					BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					String received = in.readLine();

					if (received.equals(DeviceClient.CONNECT_HELLO)) {
						System.out.println("Connected to client: " + client.getInetAddress().getHostAddress());
						clients.add(new DataOutputStream(client.getOutputStream()));
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Unable to accept client connection");
				}
			}
		}

		public void stop() {
			listening = false;
		}

	}

	public static void main(String[] args) {
		ArrayList<DeviceObserver> observers = new ArrayList<DeviceObserver>();
		observers.add(new DeviceServer());
		new Device("PAD", observers);
	}

}
