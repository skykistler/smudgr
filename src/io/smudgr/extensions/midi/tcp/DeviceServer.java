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

/**
 * The {@link DeviceServer} listens to connections and broadcasts MIDI messages
 * to clients.
 */
public class DeviceServer implements DeviceObserver {

	/**
	 * Default port to listen on.
	 */
	public static final int SMUDGR_PORT = 45454;

	/**
	 * Default response to confirm successful connection.
	 */
	public static final String CONNECT_RESPONSE = "connected";

	private ServerSocket server;
	private ClientListener listener;
	private ArrayList<DataOutputStream> clients = new ArrayList<DataOutputStream>();

	/**
	 * Create and start a new {@link DeviceServer}
	 */
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

	@Override
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

	/**
	 * Stops the {@link DeviceServer} and closes all connections
	 */
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

		@Override
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

	/**
	 * Unit test
	 * 
	 * @param args
	 *            unused
	 */
	public static void main(String[] args) {
		ArrayList<DeviceObserver> observers = new ArrayList<DeviceObserver>();
		observers.add(new DeviceServer());
		new Device("PAD", observers);
	}

}
