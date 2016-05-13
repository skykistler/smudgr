package io.smudgr.extensions.midi;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.sound.midi.MidiMessage;

import io.smudgr.extensions.midi.Device.DeviceObserver;

public class DeviceServer implements DeviceObserver {

	private ServerSocket server;
	private ClientListener listener;
	private ArrayList<DataOutputStream> clients = new ArrayList<DataOutputStream>();;

	public DeviceServer() {
		try {
			server = new ServerSocket(9999);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Unable to start Device server!");
		}

		listener = new ClientListener();
		listener.start();
	}

	public void midiInput(MidiMessage message) {
		byte[] byteMessage = message.getMessage();

		for (DataOutputStream client : clients) {
			try {
				client.writeInt(byteMessage.length);
				client.write(byteMessage);
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
					clients.add(new DataOutputStream(client.getOutputStream()));
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

}
