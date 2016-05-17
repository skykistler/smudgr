package io.smudgr.extensions.midi.tcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import io.smudgr.extensions.midi.Device.DeviceObserver;
import io.smudgr.test.DeviceTest;

public class DeviceClient {

	public static final String CONNECT_HELLO = "smudgr";

	private DeviceObserver observer;
	private String ip;

	private Socket serverSocket;
	private DataInputStream server;
	private ServerListener listener;

	public DeviceClient(DeviceObserver observer) {
		this(observer, null);
	}

	public DeviceClient(DeviceObserver observer, String ip) {
		this.observer = observer;
		this.ip = ip;

		if (ip == null)
			attemptLocalServerFind();

		if (server == null) {
			System.out.println("Unable to connect to smudgr server");
			return;
		}

		listener = new ServerListener();
		listener.start();
	}

	private void attemptLocalServerFind() {
		String thisIp = null;
		try {
			thisIp = Inet4Address.getLocalHost().getHostAddress();
			System.out.println("Local IP: " + thisIp);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return;
		}

		String searchSpace = thisIp.substring(0, thisIp.lastIndexOf('.') + 1);

		System.out.print("\nSearching..");
		for (int i = 1; i < 255; i++) {
			String testIp = searchSpace + i;

			System.out.print(".");
			try {
				serverSocket = new Socket();
				serverSocket.connect(new InetSocketAddress(testIp, DeviceServer.SMUDGR_PORT), 50);

				PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);

				out.println(CONNECT_HELLO);
				out.flush();

				server = new DataInputStream(serverSocket.getInputStream());
				ip = testIp;

				System.out.println("\nConnected to: " + ip);
				return;
			} catch (IOException e) {
			}
		}

		System.out.println("Unable to find smudgr server on local network");
	}

	public void stop() {
		listener.stop();

		try {
			server.close();
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getServerIP() {
		return ip;
	}

	private class ServerListener implements Runnable {

		private volatile boolean listening;

		public void start() {
			listening = true;
			(new Thread(this)).start();
		}

		public void run() {
			byte[] message = new byte[64];
			int length = 0;

			try {
				while (listening && (length = server.read(message)) != -1) {
					if (length > 0) {
						DeviceMessage midiMessage = new DeviceMessage(message, length);
						observer.midiInput(midiMessage);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void stop() {
			listening = false;
		}
	}

	public static void main(String[] args) {
		new DeviceClient(new DeviceTest());
	}

}
