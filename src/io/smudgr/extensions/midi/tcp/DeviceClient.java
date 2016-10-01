package io.smudgr.extensions.midi.tcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;

import io.smudgr.extensions.midi.Device.DeviceObserver;
import io.smudgr.test.DeviceTest;

public class DeviceClient {

	private static final boolean DEBUG = true;
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

		if (ip != null)
			attemptConnect(ip);

		listener = new ServerListener();
		listener.start();
	}

	private String getLANAddress() {
		try {
			for (final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces
					.hasMoreElements();) {
				final NetworkInterface cur = interfaces.nextElement();

				if (cur.isLoopback()) {
					continue;
				}

				if (DEBUG)
					System.out.println("interface " + cur.getName());

				for (final InterfaceAddress addr : cur.getInterfaceAddresses()) {
					final InetAddress inet_addr = addr.getAddress();

					if (!(inet_addr instanceof Inet4Address)) {
						continue;
					}

					if (DEBUG) {
						System.out.println(
								"  address: " + inet_addr.getHostAddress() + "/" + addr.getNetworkPrefixLength());
						System.out.println("  broadcast address: " + addr.getBroadcast().getHostAddress());

						return inet_addr.getHostAddress();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private void attemptLocalServerFind() {
		String thisIp = getLANAddress();

		if (thisIp == null)
			return;

		System.out.println("Using local IP: " + thisIp);

		String searchSpace = thisIp.substring(0, thisIp.lastIndexOf('.') + 1);

		System.out.print("\nSearching..");
		for (int i = 1; i < 255; i++) {
			String testIp = searchSpace + i;

			if (testIp.equals(thisIp))
				continue;

			System.out.print(".");
			boolean connected = attemptConnect(testIp);

			if (connected)
				return;
		}
	}

	private boolean attemptConnect(String tryIp) {
		try {
			serverSocket = new Socket();
			serverSocket.connect(new InetSocketAddress(tryIp, DeviceServer.SMUDGR_PORT), 50);

			PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);

			out.println(CONNECT_HELLO);
			out.flush();

			server = new DataInputStream(serverSocket.getInputStream());
			ip = tryIp;

			System.out.println("\nConnected to: " + ip);
			return true;
		} catch (IOException e) {
		}

		return false;
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

		private volatile boolean listening, waiting;

		public void start() {
			waiting = true;
			(new Thread(this)).start();
		}

		public void run() {
			while (waiting) {
				if (ip == null)
					attemptLocalServerFind();

				if (ip == null)
					System.out.println("Unable to find smudgr server on local network, trying again...");
				else
					waiting = false;
			}

			listening = true;
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
			waiting = false;
			listening = false;
		}
	}

	public static void main(String[] args) {
		new DeviceClient(new DeviceTest());
	}

}
